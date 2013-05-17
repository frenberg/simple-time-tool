package com.frenberg.tid;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Timer;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;
import javax.swing.JCheckBox;

public class TidJFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private final JButton btnCalculate = new JButton("Beräkna");
	private JTextArea txtTimes;
	private JTextPane txtResultPane;
	private Timer timer;
	private JButton btnFetch = new JButton("Hämta");
	private JTextField kortnr;
	private JLabel lblKortnr;
	private JLabel lblPinkod;
	private JPasswordField pin;
	private JCheckBox chkDayBeforeHoliday;

	/**
	 * Create the frame.
	 */
	public TidJFrame(String title) {
		super(title);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 570, 350);
		setResizable(false);
		contentPane = new JPanel();
		contentPane.setBackground(Color.WHITE);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);

		GridBagLayout contentPaneGBLayout = new GridBagLayout();
		contentPaneGBLayout.columnWidths = new int[] { 0, 83, 0, 0 };
		contentPaneGBLayout.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0 };
		contentPaneGBLayout.columnWeights = new double[] { 0.0, 0.0, 1.0,
				Double.MIN_VALUE };
		contentPaneGBLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 1.0, 1.0, 0.0,
				Double.MIN_VALUE };
		contentPane.setLayout(contentPaneGBLayout);

		lblKortnr = new JLabel("Anställningsnr");
		GridBagConstraints lblKortnrGBLayout = new GridBagConstraints();
		lblKortnrGBLayout.anchor = GridBagConstraints.NORTHWEST;
		lblKortnrGBLayout.insets = new Insets(0, 0, 5, 5);
		lblKortnrGBLayout.gridx = 0;
		lblKortnrGBLayout.gridy = 0;
		contentPane.add(lblKortnr, lblKortnrGBLayout);

		lblPinkod = new JLabel("Pinkod");
		GridBagConstraints lblPinkodGBLayout = new GridBagConstraints();
		lblPinkodGBLayout.anchor = GridBagConstraints.NORTHWEST;
		lblPinkodGBLayout.insets = new Insets(0, 0, 5, 5);
		lblPinkodGBLayout.gridx = 1;
		lblPinkodGBLayout.gridy = 0;
		contentPane.add(lblPinkod, lblPinkodGBLayout);

		kortnr = new JTextField();
		GridBagConstraints kortnrGBLayout = new GridBagConstraints();
		kortnrGBLayout.anchor = GridBagConstraints.WEST;
		kortnrGBLayout.insets = new Insets(0, 0, 5, 5);
		kortnrGBLayout.gridx = 0;
		kortnrGBLayout.gridy = 1;
		contentPane.add(kortnr, kortnrGBLayout);
		kortnr.setColumns(3);

		pin = new JPasswordField();
		GridBagConstraints pinGBLayout = new GridBagConstraints();
		pinGBLayout.anchor = GridBagConstraints.WEST;
		pinGBLayout.insets = new Insets(0, 0, 5, 5);
		pinGBLayout.gridx = 1;
		pinGBLayout.gridy = 1;
		contentPane.add(pin, pinGBLayout);
		pin.setColumns(4);

		GridBagConstraints fetchButtonGBLayout = new GridBagConstraints();
		fetchButtonGBLayout.insets = new Insets(0, 0, 5, 0);
		fetchButtonGBLayout.anchor = GridBagConstraints.NORTHWEST;
		fetchButtonGBLayout.gridx = 2;
		fetchButtonGBLayout.gridy = 1;
		btnFetch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ArrayList<String> stamplingar = null;
				CronaCom con = new CronaCom();
				try {
					stamplingar = con.getTimesFromCronaTid(kortnr.getText(),
							new String(pin.getPassword()));
				} catch (Exception x) {
					x.printStackTrace();
				}
				if (stamplingar != null) {
					String newtimes = new String();
					for (String stampling : stamplingar) {
						// newtimes.concat(stampling + "\n");
						newtimes += stampling + "\n";
					}
					txtTimes.setText(newtimes);
				}
			}
		});
		contentPane.add(btnFetch, fetchButtonGBLayout);

		JTextPane txtHelptext = new JTextPane();
		txtHelptext.setEnabled(false);
		txtHelptext.setEditable(false);
		txtHelptext
				.setText("Skriv tid för in, utstämpling. En stämpling per rad, varannan in och varannan ut. Ex.\n08:00 <ENTER> (in)\n12:00 <ENTER> (ut)\n12:30 <ENTER> (in)\n16:30 <ENTER> (ut)\nKlicka på Beräkna och så summerar programmet din dag.");

		GridBagConstraints txtHelptextGBLayout = new GridBagConstraints();
		txtHelptextGBLayout.gridwidth = 3;
		txtHelptextGBLayout.insets = new Insets(0, 0, 5, 0);
		txtHelptextGBLayout.fill = GridBagConstraints.BOTH;
		txtHelptextGBLayout.gridx = 0;
		txtHelptextGBLayout.gridy = 2;
		contentPane.add(txtHelptext, txtHelptextGBLayout);

		txtTimes = new JTextArea(5, 5);
		txtTimes.setLineWrap(true);
		JScrollPane scrollPane = new JScrollPane(txtTimes);
		scrollPane
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		GridBagConstraints txtTimesGBLayout = new GridBagConstraints();
		txtTimesGBLayout.gridwidth = 3;
		txtTimesGBLayout.insets = new Insets(0, 0, 5, 0);
		txtTimesGBLayout.fill = GridBagConstraints.BOTH;
		txtTimesGBLayout.gridx = 0;
		txtTimesGBLayout.gridy = 3;
		contentPane.add(scrollPane, txtTimesGBLayout);

		GridBagConstraints btnCalculateGBLayout = new GridBagConstraints();
		btnCalculateGBLayout.anchor = GridBagConstraints.NORTHWEST;
		btnCalculateGBLayout.insets = new Insets(0, 0, 5, 5);
		btnCalculateGBLayout.gridx = 0;
		btnCalculateGBLayout.gridy = 4;
		btnCalculate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Map<String, String> response = new Tid().calculate(txtTimes
						.getText(),
						chkDayBeforeHoliday.isSelected());
				txtResultPane.setText(response.get("response"));
				if (response.get("warning") != null) {
					JOptionPane.showMessageDialog(contentPane,
							response.get("warning"), "Kontrollera",
							JOptionPane.WARNING_MESSAGE);
				}

				// Hämta timern, rensa befintliga notifieringar
				if (timer != null) {
					timer.cancel();
					timer.purge();
				}

				if (response.get("notificationTime") != null) {
					long date = Long.parseLong(response.get("notificationTime"));

					// Skapa ny och schemalägg
					timer = new Timer(true);
					NotificationTimerTask tt = new NotificationTimerTask();
					timer.schedule(tt, new Date(date));
				}
			}
		});
		contentPane.add(btnCalculate, btnCalculateGBLayout);

		txtResultPane = new JTextPane();
		txtResultPane.setEditable(false);
		GridBagConstraints txtResultPaneGBLayout = new GridBagConstraints();
		txtResultPaneGBLayout.insets = new Insets(0, 0, 5, 0);
		txtResultPaneGBLayout.gridwidth = 2;
		txtResultPaneGBLayout.anchor = GridBagConstraints.NORTH;
		txtResultPaneGBLayout.fill = GridBagConstraints.HORIZONTAL;
		txtResultPaneGBLayout.gridx = 1;
		txtResultPaneGBLayout.gridy = 4;
		contentPane.add(txtResultPane, txtResultPaneGBLayout);
		
		chkDayBeforeHoliday = new JCheckBox("Dag före röd dag");
		chkDayBeforeHoliday.setBackground(Color.WHITE);
		GridBagConstraints chkDayBeforeHolidayGBLayout = new GridBagConstraints();
		chkDayBeforeHolidayGBLayout.anchor = GridBagConstraints.WEST;
		chkDayBeforeHolidayGBLayout.gridwidth = 2;
		chkDayBeforeHolidayGBLayout.insets = new Insets(0, 0, 0, 5);
		chkDayBeforeHolidayGBLayout.gridx = 0;
		chkDayBeforeHolidayGBLayout.gridy = 5;
		contentPane.add(chkDayBeforeHoliday, chkDayBeforeHolidayGBLayout);
	}

}
