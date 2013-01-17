package com.frenberg.tid;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

public class TidJFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private final JButton button = new JButton("Beräkna");
	private JTextArea textArea;
	private JTextPane textPane;
	private Timer timer;
	
	/**
	 * Create the frame.
	 */
	public TidJFrame(String title) {
		super(title);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 560, 252);
		contentPane = new JPanel();
		contentPane.setBackground(Color.WHITE);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{0, 0, 0};
		gbl_contentPane.rowHeights = new int[]{0, 0, 0, 0};
		gbl_contentPane.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{1.0, 1.0, 1.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);
		
		JTextPane txtpnSkrivTidFr = new JTextPane();
		txtpnSkrivTidFr.setEnabled(false);
		txtpnSkrivTidFr.setEditable(false);
		txtpnSkrivTidFr.setText("Skriv tid för in, utstämpling. En stämpling per rad, varannan in och varannan ut. Ex.\n08:00 <ENTER> (in)\n12:00 <ENTER> (ut)\n12:30 <ENTER> (in)\n16:30 <ENTER> (ut)\nKlicka på Beräkna och så summerar programmet din dag.");
		
		GridBagConstraints gbc_txtpnSkrivTidFr = new GridBagConstraints();
		gbc_txtpnSkrivTidFr.gridwidth = 2;
		gbc_txtpnSkrivTidFr.insets = new Insets(0, 0, 5, 0);
		gbc_txtpnSkrivTidFr.fill = GridBagConstraints.BOTH;
		gbc_txtpnSkrivTidFr.gridx = 0;
		gbc_txtpnSkrivTidFr.gridy = 0;
		contentPane.add(txtpnSkrivTidFr, gbc_txtpnSkrivTidFr);
		
		textArea = new JTextArea(5, 5);
		textArea.setLineWrap(true);
		JScrollPane scrollPane = new JScrollPane(textArea);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
		GridBagConstraints gbc_textArea = new GridBagConstraints();
		gbc_textArea.gridwidth = 2;
		gbc_textArea.insets = new Insets(0, 0, 5, 0);
		gbc_textArea.fill = GridBagConstraints.BOTH;
		gbc_textArea.gridx = 0;
		gbc_textArea.gridy = 1;
		contentPane.add(scrollPane, gbc_textArea);
		
		GridBagConstraints gbc_button = new GridBagConstraints();
		gbc_button.anchor = GridBagConstraints.NORTHWEST;
		gbc_button.insets = new Insets(0, 0, 0, 5);
		gbc_button.gridx = 0;
		gbc_button.gridy = 2;
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
		gbc_textPane.anchor = GridBagConstraints.NORTH;
		gbc_textPane.fill = GridBagConstraints.HORIZONTAL;
		gbc_textPane.gridx = 1;
		gbc_textPane.gridy = 2;
		contentPane.add(textPane, gbc_textPane);
	}

}
