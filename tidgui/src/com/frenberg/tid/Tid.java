package com.frenberg.tid;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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

public class Tid {
	private long currentTimeMillis;
	private long workingTimeMillis;
	private long longestPause = 0;

	public Map<String, String> calculate(String input,
			boolean dayBeforeHoliday, HashMap<Integer, Double> schema) {
		Calendar cal = Calendar.getInstance();
		currentTimeMillis = cal.getTimeInMillis();

		int numberOfValidInputs = 0, dayOfWeek;
		long time = 0, tmpTime = 0, accumulatedTime = 0, lastTime = 0;
		Map<String, String> returnStrings = new HashMap<>();

		// Calculate todays scheduled working time - We have reduced scheduled 
		// working time during June-August (7,18h). Rest of year, ordinary schedule (8,18h)
		dayOfWeek = cal.get( Calendar.DAY_OF_WEEK ) == 1 ? 6 :  cal.get( Calendar.DAY_OF_WEEK ) - 2;
		if (cal.get(Calendar.MONTH) > 4 && cal.get(Calendar.MONTH) < 8) {
			if (schema.get(dayOfWeek) != 8.18) {
				workingTimeMillis = new Double(
						schema.get(dayOfWeek) * 3600 * 1000)
						.longValue();
			} else {
				workingTimeMillis = 25848000; // 7.18 * 3600 * 1000
			}
		} else {
			if (schema.get(dayOfWeek) != 8.18) {
				workingTimeMillis = new Double(
						schema.get(dayOfWeek) * 3600 * 1000)
						.longValue();
			} else {
				workingTimeMillis = 29448000; // 8.18 * 3600 * 1000
			}
		}

		if (dayBeforeHoliday) {
			workingTimeMillis = 21600000; // Day before holiday, 6 * 3600 * 1000
		}

		// Parse input from user/crona tid integration
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");

		String[] lines = input.split("\r?\n|\r");

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
					if (numberOfValidInputs > 1
							&& longestPause < (time - lastTime)) {
						longestPause = (time - lastTime);
					}
				}
			}
		}

		// We have to take at least .5h lunch break
		if (longestPause > 0 && longestPause < 1800000) {
			returnStrings
					.put("warning",
							"Om du varit utstämplad under mindre än 30 minuter för lunch,\nmåste du korrigera stämplingstiden och beräkna på nytt.");
		}

		// odd number of inputs => not done yet for today, calculate time to
		// leave
		if (numberOfValidInputs % 2 == 1 && numberOfValidInputs > 0) {

			try {
				time = simpleDateFormat.parse(
						simpleDateFormat.format(new Date(currentTimeMillis)))
						.getTime(); // minutes
			} catch (ParseException e) {
				// Should not be possible
			}
			accumulatedTime += (time - tmpTime);

			long date = currentTimeMillis
					+ (workingTimeMillis - accumulatedTime);
			if (numberOfValidInputs == 1) {
				// Have not punched out for lunch break,
				// add one hour for unpaid lunch break...
				date += 3600000; // 1 * 3600 * 1000
			}

			// Only on valid input row and current time of day is > 1pm
			if (numberOfValidInputs == 1 && time > 43200000) {
				accumulatedTime -= 3600000;
			}

			writeLog(String.format("%.2f", accumulatedTime / 3600000.0));

			cal.setTime(new Date(date));
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			date = cal.getTimeInMillis();

			returnStrings.put("notificationTime", Long.toString(date));
			returnStrings
					.put("response",
							String.format(
									"Du får gå hem %s, går du hem nu har du jobbat %.2f timmar (%.2fh).",
									simpleDateFormat.format(new Date(date)),
									accumulatedTime / 3600000.0,
									Math.abs((workingTimeMillis - accumulatedTime) / 3600000.0)));
		} else {
			// Summarize
			if (numberOfValidInputs == 2) {
				accumulatedTime -= 3600000;
			}
			returnStrings
					.put("response",
							String.format(
									"Summerad arbetstid: %.2f timmar (%.2fh).",
									accumulatedTime / 3600000.0,
									Math.abs((workingTimeMillis - accumulatedTime) / 3600000.0)));
		}

		writeLog(String.format("%.2f", accumulatedTime / 3600000.0));
		return returnStrings;
	}

	private boolean writeLog(String time) {
		String fileRow;
		ArrayList<String> fileContentRows = new ArrayList<>();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String today = formatter.format(new Date(this.currentTimeMillis));

		// start by adding current calculation as first row in file
		fileContentRows.add(today + "\t" + time);

		// Read file and find a row for current date.
		try {
			FileInputStream fileInputStream = new FileInputStream(
					"tidhistorik.log");

			// Get the object of DataInputStream
			DataInputStream in = new DataInputStream(fileInputStream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));

			// Read File Line By Line
			while ((fileRow = br.readLine()) != null) {
				if (!fileRow.startsWith(today)) {
					fileContentRows.add(fileRow);
				}
			}

			// Close the input stream
			in.close();
		} catch (FileNotFoundException e) {
			// this is ok...
		} catch (Exception e) {// Catch exception if any
			e.printStackTrace();
			return false;
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
