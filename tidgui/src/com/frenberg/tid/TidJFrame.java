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
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JPasswordField;

public class TidJFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private final JButton button = new JButton("Beräkna");
	private JTextArea textArea;
	private JTextPane textPane;
	private Timer timer;
	private JButton fetch = new JButton("Hämta");
	private JTextField kortnr;
	private JLabel lblKortnr;
	private JLabel lblPinkod;
	private JPasswordField pin;
	
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
		
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{0, 83, 0, 0};
		gbl_contentPane.rowHeights = new int[]{0, 0, 0, 0, 0, 0};
		gbl_contentPane.columnWeights = new double[]{0.0, 0.0, 1.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{0.0, 0.0, 0.0, 1.0, 1.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);
		
		lblKortnr = new JLabel("Anställningsnr");
		GridBagConstraints gbc_lblKortnr = new GridBagConstraints();
		gbc_lblKortnr.anchor = GridBagConstraints.NORTHWEST;
		gbc_lblKortnr.insets = new Insets(0, 0, 5, 5);
		gbc_lblKortnr.gridx = 0;
		gbc_lblKortnr.gridy = 0;
		contentPane.add(lblKortnr, gbc_lblKortnr);
		
		lblPinkod = new JLabel("Pinkod");
		GridBagConstraints gbc_lblPinkod = new GridBagConstraints();
		gbc_lblPinkod.anchor = GridBagConstraints.NORTHWEST;
		gbc_lblPinkod.insets = new Insets(0, 0, 5, 5);
		gbc_lblPinkod.gridx = 1;
		gbc_lblPinkod.gridy = 0;
		contentPane.add(lblPinkod, gbc_lblPinkod);
		
		kortnr = new JTextField();
		GridBagConstraints gbc_kortnr = new GridBagConstraints();
		gbc_kortnr.anchor = GridBagConstraints.WEST;
		gbc_kortnr.insets = new Insets(0, 0, 5, 5);
		gbc_kortnr.gridx = 0;
		gbc_kortnr.gridy = 1;
		contentPane.add(kortnr, gbc_kortnr);
		kortnr.setColumns(3);
		
		pin = new JPasswordField();
		GridBagConstraints gbc_pin = new GridBagConstraints();
		gbc_pin.anchor = GridBagConstraints.WEST;
		gbc_pin.insets = new Insets(0, 0, 5, 5);
		gbc_pin.gridx = 1;
		gbc_pin.gridy = 1;
		contentPane.add(pin, gbc_pin);
		pin.setColumns(4);
		
		GridBagConstraints gbc_fetch = new GridBagConstraints();
		gbc_fetch.insets = new Insets(0, 0, 5, 0);
		gbc_fetch.anchor = GridBagConstraints.NORTHWEST;
		gbc_fetch.gridx = 2;
		gbc_fetch.gridy = 1;
		fetch.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						ArrayList<String> stamplingar = null;
						CronaCom con = new CronaCom();
						try {
							stamplingar = con.getTimesFromCronaTid(kortnr.getText(), new String(pin.getPassword())); 
						} catch(Exception x) {
							x.printStackTrace();
						}
						if (stamplingar != null) {
							String newtimes = new String();
							for (String stampling : stamplingar) {
								//newtimes.concat(stampling + "\n");
								newtimes += stampling + "\n";
							}
							textArea.setText(newtimes);
						}
					}
				});
		contentPane.add(fetch, gbc_fetch);
		
		JTextPane txtpnSkrivTidFr = new JTextPane();
		txtpnSkrivTidFr.setEnabled(false);
		txtpnSkrivTidFr.setEditable(false);
		txtpnSkrivTidFr.setText("Skriv tid för in, utstämpling. En stämpling per rad, varannan in och varannan ut. Ex.\n08:00 <ENTER> (in)\n12:00 <ENTER> (ut)\n12:30 <ENTER> (in)\n16:30 <ENTER> (ut)\nKlicka på Beräkna och så summerar programmet din dag.");
		
		GridBagConstraints gbc_txtpnSkrivTidFr = new GridBagConstraints();
		gbc_txtpnSkrivTidFr.gridwidth = 3;
		gbc_txtpnSkrivTidFr.insets = new Insets(0, 0, 5, 0);
		gbc_txtpnSkrivTidFr.fill = GridBagConstraints.BOTH;
		gbc_txtpnSkrivTidFr.gridx = 0;
		gbc_txtpnSkrivTidFr.gridy = 2;
		contentPane.add(txtpnSkrivTidFr, gbc_txtpnSkrivTidFr);
		
		textArea = new JTextArea(5, 5);
		textArea.setLineWrap(true);
		JScrollPane scrollPane = new JScrollPane(textArea);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
		GridBagConstraints gbc_textArea = new GridBagConstraints();
		gbc_textArea.gridwidth = 3;
		gbc_textArea.insets = new Insets(0, 0, 5, 0);
		gbc_textArea.fill = GridBagConstraints.BOTH;
		gbc_textArea.gridx = 0;
		gbc_textArea.gridy = 3;
		contentPane.add(scrollPane, gbc_textArea);
		
		GridBagConstraints gbc_button = new GridBagConstraints();
		gbc_button.anchor = GridBagConstraints.NORTHWEST;
		gbc_button.insets = new Insets(0, 0, 0, 5);
		gbc_button.gridx = 0;
		gbc_button.gridy = 4;
		button.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						Map<String, String> response = new Tid().calculate(textArea.getText());
						textPane.setText(response.get("response"));
						
						//Hämta timern, rensa befintliga notifieringar
						if (timer != null) {
							timer.cancel();
							timer.purge();
						}
						
						if (response.get("notificationTime") != null) {
							long date = Long.parseLong(response.get("notificationTime"));
							
							//Skapa ny och schemalägg
							timer = new Timer(true);
							NotificationTimerTask tt = new NotificationTimerTask();
							timer.schedule(tt, new Date(date));
						}
					}
				}
		);
		contentPane.add(button, gbc_button);
		
		textPane = new JTextPane();
		textPane.setEditable(false);
		GridBagConstraints gbc_textPane = new GridBagConstraints();
		gbc_textPane.gridwidth = 2;
		gbc_textPane.anchor = GridBagConstraints.NORTH;
		gbc_textPane.fill = GridBagConstraints.HORIZONTAL;
		gbc_textPane.gridx = 1;
		gbc_textPane.gridy = 4;
		contentPane.add(textPane, gbc_textPane);
	}

}
