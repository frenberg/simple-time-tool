package com.frenberg.tid;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

class Tid {
	private long currentTimeMillis;

	Map<String, String> calculate(String input, boolean dayBeforeHoliday,
			HashMap<Integer, Double> schema) {
		Calendar cal = Calendar.getInstance();
		currentTimeMillis = cal.getTimeInMillis();

		Map<String, String> returnStrings = new HashMap<>();

		long workingTimeMillis = getScheduledWorkingTimeInMillis(dayBeforeHoliday, schema, cal);

		// Parse input from user/crona tid integration
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");

		int      numberOfValidInputs = 0;
		long     time                = 0, tmpTime = 0, accumulatedTime = 0, lastTime = 0, longestPause = 0;
		String[] lines               = input.split("\r?\n|\r");
		for (String line : lines) {
			time = 0;
			line = line.trim();
			if (!line.equals("") && Pattern.matches("^\\d\\d\\:\\d\\d$", line)) {
				numberOfValidInputs++;

				try {
					time = simpleDateFormat.parse(line).getTime();
				} catch (ParseException e) {
					// Should not be possible with correct regexp above
					numberOfValidInputs--;
					continue;
				}
				if (numberOfValidInputs % 2 == 0) {
					lastTime = time;
					accumulatedTime += (time - tmpTime);
				} else {
					tmpTime = time;
					if (numberOfValidInputs > 1 && longestPause < (time - lastTime)) {
						longestPause = (time - lastTime);
					}
				}
			}
		}

		// We have to take at least .5h lunch break
		if (longestPause > 0 && longestPause < 1800000) {
			returnStrings.put("warning",
					"Om du varit utstämplad under mindre än 30 minuter för lunch,\nmåste du korrigera stämplingstiden och beräkna på nytt.");
		}

		// odd number of inputs => not done yet for today, calculate time to
		// leave
		if (numberOfValidInputs % 2 == 1 && numberOfValidInputs > 0) {

			try {
				time = simpleDateFormat.parse(simpleDateFormat.format(new Date(currentTimeMillis)))
						.getTime(); // minutes
			} catch (ParseException e) {
				// Should not be possible
			}
			accumulatedTime += (time - tmpTime);
			// Only one valid input row and current time of day is > 1pm
			if (numberOfValidInputs == 1 && time > 43200000) {
				accumulatedTime -= 3600000;
			}

			long timeToLeaveInMillis = currentTimeMillis + (workingTimeMillis - accumulatedTime);
			cal.setTime(new Date(timeToLeaveInMillis));
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			timeToLeaveInMillis = cal.getTimeInMillis();

			if (numberOfValidInputs == 1) {
				// Have not punched out for lunch break,
				// add one hour for unpaid lunch break...
				timeToLeaveInMillis += 3600000; // 1 * 3600 * 1000
			}

			returnStrings.put("notificationTime", Long.toString(timeToLeaveInMillis));
			returnStrings.put("response",
					String.format(
							"Du får gå hem %s, går du hem nu har du jobbat %.2f timmar (%.2fh).",
							simpleDateFormat.format(new Date(timeToLeaveInMillis)),
							accumulatedTime / 3600000.0,
							Math.abs((workingTimeMillis - accumulatedTime) / 3600000.0)));
		} else {
			// Summarize (remove one hour for lunch if just two times
			if (numberOfValidInputs == 2) {
				accumulatedTime -= 3600000;
			}
			returnStrings.put("response",
					String.format("Summerad arbetstid: %.2f timmar (%.2fh).",
							accumulatedTime / 3600000.0,
							Math.abs((workingTimeMillis - accumulatedTime) / 3600000.0)));
		}

		writeLog(String.format("%.2f", accumulatedTime / 3600000.0));
		return returnStrings;
	}

	/*
	 * Calculate today scheduled working time
	 * We have reduced scheduled working time during June-August (7,18h).
	 * Rest of year, ordinary schedule (8,18h)
	 */
	private long getScheduledWorkingTimeInMillis(boolean dayBeforeHoliday,
			HashMap<Integer, Double> schema, Calendar cal) {
		if (dayBeforeHoliday) {
			return 22260000; // Day before holiday, (6 * 3600 * 1000) + 660000
		}
		int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY ? 6 :
						cal.get(Calendar.DAY_OF_WEEK) - 2;
		if (cal.get(Calendar.MONTH) > 4 && cal.get(Calendar.MONTH) < 8) {
			if (schema.get(dayOfWeek) != 8.18) {
				return new Double(schema.get(dayOfWeek) * 3600 * 1000).longValue();
			} else {
				return 25860000; // (7 * 3600 * 1000) + 660000
			}
		} else {
			if (schema.get(dayOfWeek) != 8.18) {
				return new Double(schema.get(dayOfWeek) * 3600 * 1000).longValue();
			} else {
				return 29460000; // (8 * 3600 * 1000) + 660000
			}
		}
	}

	private boolean writeLog(String time) {
		String            fileRow;
		ArrayList<String> fileContentRows = new ArrayList<>();
		SimpleDateFormat  formatter       = new SimpleDateFormat("yyyy-MM-dd");
		String            today           = formatter.format(new Date(this.currentTimeMillis));

		// start by adding current calculation as first row in file
		fileContentRows.add(today + "\t" + time);

		// Read file and find a row for current date.
		try {
			FileInputStream fileInputStream = new FileInputStream("tidhistorik.log");

			// Get the object of DataInputStream
			DataInputStream in = new DataInputStream(fileInputStream);
			BufferedReader  br = new BufferedReader(new InputStreamReader(in));

			// Read File Line By Line
			while ((fileRow = br.readLine()) != null) {
				if (!fileRow.startsWith(today)) {
					fileContentRows.add(fileRow);
				}
			}

			// Close the input stream
			in.close();
		} catch (IOException e) {
			// this is ok...
		}

		FileWriter fileOutputStream;
		try {
			fileOutputStream = new FileWriter("tidhistorik.log");
			BufferedWriter out = new BufferedWriter(fileOutputStream);
			for (String textRow : fileContentRows) {
				out.write(textRow + "\n");
			}
			out.close();
		} catch (IOException e) {
			return false;
		}

		return true;
	}
}
