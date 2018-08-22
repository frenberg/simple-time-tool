package com.frenberg.tid;

import static java.awt.Color.WHITE;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

class TidJFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private final JButton btnCalculate = new JButton("Beräkna");
	private JTextArea txtTimes;
	private JTextPane txtResultPane;
	private Timer timer;
	private JButton btnFetch = new JButton("Hämta");
	private JTextField kortnr;
	private JPasswordField pin;
	private JCheckBox chkDayBeforeHoliday;
	private JTextField mondayField;
	private JTextField tuesdayField;
	private JTextField wednesdayField;
	private JTextField thursdayField;
	private JTextField fridayField;
	private JTextField saturdayField;
	private JTextField sundayField;

	/**
	 * Create the frame.
	 */
	TidJFrame(String title) {
		super(title);
		setBackground(WHITE);

		//noinspection MagicConstant
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setBounds(100, 100, 590, 380);
		setResizable(false);

		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.setBorder(null);
		tabbedPane.setBackground(WHITE);

		JPanel contentPane = setupMainContentPane();
		JPanel schemaPane = setupSchemaContentPane();

		Map<Integer, Double> schema = getSchemaFromXML();
		mondayField.setText(schema.get(0).toString());
		tuesdayField.setText(schema.get(1).toString());
		wednesdayField.setText(schema.get(2).toString());
		thursdayField.setText(schema.get(3).toString());
		fridayField.setText(schema.get(4).toString());
		saturdayField.setText(schema.get(5).toString());
		sundayField.setText(schema.get(6).toString());

		tabbedPane.addTab("Main", contentPane);
		tabbedPane.addTab("Schema", schemaPane);

		setContentPane(tabbedPane);
	}

	private JPanel setupMainContentPane() {
		JPanel contentPane = new JPanel();
		contentPane.setBackground(WHITE);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		GridBagLayout contentPaneGBLayout = new GridBagLayout();
		contentPaneGBLayout.columnWidths = new int[] { 0, 83, 0, 0 };
		contentPaneGBLayout.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0 };
		contentPaneGBLayout.columnWeights = new double[] { 0.0, 0.0, 1.0,
				Double.MIN_VALUE };
		contentPaneGBLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 1.0,
				1.0, 0.0, Double.MIN_VALUE };
		contentPane.setLayout(contentPaneGBLayout);

		JLabel lblKortnr = new JLabel("Anställningsnr");
		GridBagConstraints lblKortnrGBLayout = new GridBagConstraints();
		lblKortnrGBLayout.anchor = GridBagConstraints.NORTHWEST;
		lblKortnrGBLayout.insets = new Insets(0, 0, 5, 5);
		lblKortnrGBLayout.gridx = 0;
		lblKortnrGBLayout.gridy = 0;
		contentPane.add(lblKortnr, lblKortnrGBLayout);

		JLabel lblPinkod = new JLabel("Pinkod");
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
		fetchButtonGBLayout.anchor = GridBagConstraints.NORTHEAST;
		fetchButtonGBLayout.gridx = 2;
		fetchButtonGBLayout.gridy = 1;
		btnFetch.addActionListener(e -> {
			ArrayList<String> stamplingar = null;
			CronaCom con = new CronaCom();
			try {
				stamplingar = con.getTimesFromCronaTid(kortnr.getText(),
						new String(pin.getPassword()));
			} catch (Exception x) {
				x.printStackTrace();
			}
			if (stamplingar != null) {
				String newtimes = "";
				for (String stampling : stamplingar) {
					newtimes += stampling + "\n";
				}
				txtTimes.setText(newtimes);
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
		btnCalculate.addActionListener(e -> {
			HashMap<Integer, Double> schema = getSchemaFromXML();
			Map<String, String> response = new Tid().calculate(
					txtTimes.getText(), chkDayBeforeHoliday.isSelected(),
					schema);
			txtResultPane.setText(response.get("response"));
			if (response.get("warning") != null) {
				JOptionPane.showMessageDialog(null,
						response.get("warning"), "Kontrollera",
						JOptionPane.WARNING_MESSAGE);
			}

			// Hämta timern, rensa befintliga notifieringar
			if (timer != null) {
				timer.cancel();
			}

			if (response.get("notificationTime") != null) {
				long date = Long.parseLong(response.get("notificationTime"));

				// Skapa ny och schemalägg
				timer = new Timer(true);
				NotificationTimerTask tt = new NotificationTimerTask();
				timer.schedule(tt, new Date(date));
			} else if (timer != null) {
				timer.cancel();
				timer = null;
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
		chkDayBeforeHoliday.setBackground(WHITE);
		GridBagConstraints chkDayBeforeHolidayGBLayout = new GridBagConstraints();
		chkDayBeforeHolidayGBLayout.anchor = GridBagConstraints.WEST;
		chkDayBeforeHolidayGBLayout.gridwidth = 2;
		chkDayBeforeHolidayGBLayout.insets = new Insets(0, 0, 0, 5);
		chkDayBeforeHolidayGBLayout.gridx = 0;
		chkDayBeforeHolidayGBLayout.gridy = 5;
		contentPane.add(chkDayBeforeHoliday, chkDayBeforeHolidayGBLayout);

		return contentPane;
	}

	private JPanel setupSchemaContentPane() {
		JPanel schemaPane = new JPanel();
		schemaPane.setBackground(WHITE);
		GridBagLayout layout = new GridBagLayout();
		layout.columnWidths = new int[] { 30, 67, 454, 0, 0 };
		layout.rowHeights = new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		layout.columnWeights = new double[] { 0.0, 1.0, 0.0, Double.MIN_VALUE };
		layout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
				0.0, 0.0, 0.0, 0.0 };

		schemaPane.setLayout(layout);
		
				JTextPane lblSkrivDittVeckoschema = new JTextPane();
				lblSkrivDittVeckoschema.setEditable(false);
				lblSkrivDittVeckoschema
						.setText("Skriv ditt veckoschema om du arbetar deltid. Dagar med full tid\nanges med '8.18' som räknas om till 7.18 under sommaren.");
				GridBagConstraints gbc_lblSkrivDittVeckoschema = new GridBagConstraints();
				gbc_lblSkrivDittVeckoschema.anchor = GridBagConstraints.NORTH;
				gbc_lblSkrivDittVeckoschema.fill = GridBagConstraints.HORIZONTAL;
				gbc_lblSkrivDittVeckoschema.gridwidth = 3;
				gbc_lblSkrivDittVeckoschema.insets = new Insets(0, 0, 5, 5);
				gbc_lblSkrivDittVeckoschema.gridx = 1;
				gbc_lblSkrivDittVeckoschema.gridy = 0;
				schemaPane.add(lblSkrivDittVeckoschema, gbc_lblSkrivDittVeckoschema);

		JLabel lblMonday = new JLabel("Måndag");
		GridBagConstraints gbc1 = new GridBagConstraints();
		gbc1.anchor = GridBagConstraints.EAST;
		gbc1.insets = new Insets(0, 0, 5, 5);
		gbc1.gridx = 1;
		gbc1.gridy = 1;
		schemaPane.add(lblMonday, gbc1);

		mondayField = new JTextField();
		mondayField
				.setToolTipText("Schemalagd arbetstid (hundradelar) Ex 8h 11m => 8.18");
		mondayField.setInputVerifier(new SchemaInputVerifier());
		GridBagConstraints gbc_mondayField = new GridBagConstraints();
		gbc_mondayField.anchor = GridBagConstraints.WEST;
		gbc_mondayField.insets = new Insets(0, 0, 5, 5);
		gbc_mondayField.gridx = 2;
		gbc_mondayField.gridy = 1;
		schemaPane.add(mondayField, gbc_mondayField);
		mondayField.setColumns(5);

		JLabel lblTuesday = new JLabel("Tisdag");
		GridBagConstraints gbc2 = new GridBagConstraints();
		gbc2.anchor = GridBagConstraints.EAST;
		gbc2.insets = new Insets(0, 0, 5, 5);
		gbc2.gridx = 1;
		gbc2.gridy = 2;
		schemaPane.add(lblTuesday, gbc2);

		tuesdayField = new JTextField();
		tuesdayField.setInputVerifier(new SchemaInputVerifier());
		GridBagConstraints gbc_tuesdayField = new GridBagConstraints();
		gbc_tuesdayField.anchor = GridBagConstraints.WEST;
		gbc_tuesdayField.insets = new Insets(0, 0, 5, 5);
		gbc_tuesdayField.gridx = 2;
		gbc_tuesdayField.gridy = 2;
		schemaPane.add(tuesdayField, gbc_tuesdayField);
		tuesdayField.setColumns(5);

		JLabel lblWednesday = new JLabel("Onsdag");
		GridBagConstraints gbc3 = new GridBagConstraints();
		gbc3.anchor = GridBagConstraints.EAST;
		gbc3.insets = new Insets(0, 0, 5, 5);
		gbc3.gridx = 1;
		gbc3.gridy = 3;
		schemaPane.add(lblWednesday, gbc3);

		wednesdayField = new JTextField();
		wednesdayField.setInputVerifier(new SchemaInputVerifier());
		GridBagConstraints gbc_wednesdayField = new GridBagConstraints();
		gbc_wednesdayField.anchor = GridBagConstraints.WEST;
		gbc_wednesdayField.insets = new Insets(0, 0, 5, 5);
		gbc_wednesdayField.gridx = 2;
		gbc_wednesdayField.gridy = 3;
		schemaPane.add(wednesdayField, gbc_wednesdayField);
		wednesdayField.setColumns(5);

		JLabel lblThursday = new JLabel("Torsdag");
		GridBagConstraints gbc4 = new GridBagConstraints();
		gbc4.anchor = GridBagConstraints.EAST;
		gbc4.insets = new Insets(0, 0, 5, 5);
		gbc4.gridx = 1;
		gbc4.gridy = 4;
		schemaPane.add(lblThursday, gbc4);

		thursdayField = new JTextField();
		thursdayField.setInputVerifier(new SchemaInputVerifier());
		GridBagConstraints gbc_thursdayField = new GridBagConstraints();
		gbc_thursdayField.anchor = GridBagConstraints.WEST;
		gbc_thursdayField.insets = new Insets(0, 0, 5, 5);
		gbc_thursdayField.gridx = 2;
		gbc_thursdayField.gridy = 4;
		schemaPane.add(thursdayField, gbc_thursdayField);
		thursdayField.setColumns(5);

		JLabel lblFriday = new JLabel("Fredag");
		GridBagConstraints gbc5 = new GridBagConstraints();
		gbc5.anchor = GridBagConstraints.EAST;
		gbc5.insets = new Insets(0, 0, 5, 5);
		gbc5.gridx = 1;
		gbc5.gridy = 5;
		schemaPane.add(lblFriday, gbc5);

		fridayField = new JTextField();
		fridayField.setInputVerifier(new SchemaInputVerifier());
		GridBagConstraints gbc_fridayField = new GridBagConstraints();
		gbc_fridayField.anchor = GridBagConstraints.WEST;
		gbc_fridayField.insets = new Insets(0, 0, 5, 5);
		gbc_fridayField.gridx = 2;
		gbc_fridayField.gridy = 5;
		schemaPane.add(fridayField, gbc_fridayField);
		fridayField.setColumns(5);

		JLabel lblSaturday = new JLabel("Lördag");
		GridBagConstraints gbc6 = new GridBagConstraints();
		gbc6.anchor = GridBagConstraints.EAST;
		gbc6.insets = new Insets(0, 0, 5, 5);
		gbc6.gridx = 1;
		gbc6.gridy = 6;
		schemaPane.add(lblSaturday, gbc6);

		saturdayField = new JTextField();
		saturdayField.setInputVerifier(new SchemaInputVerifier());
		GridBagConstraints gbc_saturdayField = new GridBagConstraints();
		gbc_saturdayField.anchor = GridBagConstraints.WEST;
		gbc_saturdayField.insets = new Insets(0, 0, 5, 5);
		gbc_saturdayField.gridx = 2;
		gbc_saturdayField.gridy = 6;
		schemaPane.add(saturdayField, gbc_saturdayField);
		saturdayField.setColumns(5);

		JLabel lblSunday = new JLabel("Söndag");
		GridBagConstraints gbc7 = new GridBagConstraints();
		gbc7.anchor = GridBagConstraints.EAST;
		gbc7.insets = new Insets(0, 0, 5, 5);
		gbc7.gridx = 1;
		gbc7.gridy = 7;
		schemaPane.add(lblSunday, gbc7);

		sundayField = new JTextField();
		sundayField.setInputVerifier(new SchemaInputVerifier());
		GridBagConstraints gbc_sundayField = new GridBagConstraints();
		gbc_sundayField.insets = new Insets(0, 0, 5, 5);
		gbc_sundayField.anchor = GridBagConstraints.WEST;
		gbc_sundayField.gridx = 2;
		gbc_sundayField.gridy = 7;
		schemaPane.add(sundayField, gbc_sundayField);
		sundayField.setColumns(5);

		JButton btnSpara = new JButton("Spara");
		GridBagConstraints gbc_btnSpara = new GridBagConstraints();
		gbc_btnSpara.insets = new Insets(0, 0, 5, 5);
		gbc_btnSpara.gridx = 1;
		gbc_btnSpara.gridy = 8;
		btnSpara.addActionListener(e -> {
			// save to xml resource file
			HashMap<Integer, Double> schema = new HashMap<>(7);
			schema.put(0, Double.parseDouble(mondayField.getText()));
			schema.put(1, Double.parseDouble(tuesdayField.getText()));
			schema.put(2, Double.parseDouble(wednesdayField.getText()));
			schema.put(3, Double.parseDouble(thursdayField.getText()));
			schema.put(4, Double.parseDouble(fridayField.getText()));
			schema.put(5, Double.parseDouble(saturdayField.getText()));
			schema.put(6, Double.parseDouble(sundayField.getText()));

			writeSchemaToXML(schema);
		});
		schemaPane.add(btnSpara, gbc_btnSpara);

		JButton btnterstll = new JButton("Återställ");
		GridBagConstraints gbc_btnterstll = new GridBagConstraints();
		gbc_btnterstll.insets = new Insets(0, 0, 5, 5);
		gbc_btnterstll.anchor = GridBagConstraints.WEST;
		gbc_btnterstll.gridx = 2;
		gbc_btnterstll.gridy = 8;
		btnterstll.addActionListener(e -> {
			HashMap<Integer, Double> schema = getDefaultSchema();
			mondayField.setText(Double.toString(schema.get(0)));
			tuesdayField.setText(Double.toString(schema.get(1)));
			wednesdayField.setText(Double.toString(schema.get(2)));
			thursdayField.setText(Double.toString(schema.get(3)));
			fridayField.setText(Double.toString(schema.get(4)));
			saturdayField.setText(Double.toString(schema.get(5)));
			sundayField.setText(Double.toString(schema.get(6)));
		});
		schemaPane.add(btnterstll, gbc_btnterstll);

		return schemaPane;
	}

	private HashMap<Integer, Double> getSchemaFromXML() {
		HashMap<Integer, Double> schema = new HashMap<>(7);

		String filePath = System.getProperty("user.home")
				+ System.getProperty("file.separator") + "schema.xml";

		File file = new File(filePath);

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;
		Document doc;
		try {
			db = dbf.newDocumentBuilder();
			doc = db.parse(file);

			NodeList list = doc.getDocumentElement().getChildNodes();
			for (int i = 0, dow = 0; i < list.getLength(); i++) {
				if (list.item(i).getNodeType() == Node.ELEMENT_NODE) {
					schema.put(dow++,
							Double.parseDouble((list.item(i).getTextContent().isEmpty()) ? "0" :
											   list.item(i).getTextContent()));
				}
			}
			return schema;
		} catch (Exception e) {
			// This is ok, we use default instead...
			return getDefaultSchema();
		}
	}

	private boolean writeSchemaToXML(HashMap<Integer, Double> schema) {
		String filePath = System.getProperty("user.home")
				+ System.getProperty("file.separator") + "schema.xml";
		try {
			String xml = buildXMLString(schema);

			PrintWriter out = new PrintWriter(new File(filePath));
			out.write(xml);
			out.close();
			return true;
		} catch (ParserConfigurationException | TransformerException | IOException e) {
			e.printStackTrace();
		}

		return false;
	}

	private String buildXMLString(HashMap<Integer, Double> schema)
			throws ParserConfigurationException, TransformerException {
		Element el;

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.newDocument();
		Element root = doc.createElement("schema");

		el = doc.createElement("monday");
		el.appendChild(doc.createTextNode(Double.toString(schema.get(0))));
		root.appendChild(el);

		el = doc.createElement("tuesday");
		el.appendChild(doc.createTextNode(Double.toString(schema.get(1))));
		root.appendChild(el);

		el = doc.createElement("wednesday");
		el.appendChild(doc.createTextNode(Double.toString(schema.get(2))));
		root.appendChild(el);

		el = doc.createElement("thursday");
		el.appendChild(doc.createTextNode(Double.toString(schema.get(3))));
		root.appendChild(el);

		el = doc.createElement("friday");
		el.appendChild(doc.createTextNode(Double.toString(schema.get(4))));
		root.appendChild(el);

		el = doc.createElement("saturday");
		el.appendChild(doc.createTextNode(Double.toString(schema.get(5))));
		root.appendChild(el);

		el = doc.createElement("sunday");
		el.appendChild(doc.createTextNode(Double.toString(schema.get(6))));
		root.appendChild(el);

		doc.appendChild(root);

		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer t = tf.newTransformer();
		t.setOutputProperty(OutputKeys.INDENT, "yes");

		StringWriter sw = new java.io.StringWriter();
		StreamResult sr = new StreamResult(sw);
		t.transform(new DOMSource(doc), sr);
		return sw.toString();

	}

	private HashMap<Integer, Double> getDefaultSchema() {
		HashMap<Integer, Double> schema = new HashMap<>(7);
		schema.put(0, 8.18d);
		schema.put(1, 8.18d);
		schema.put(2, 8.18d);
		schema.put(3, 8.18d);
		schema.put(4, 8.18d);
		schema.put(5, 0d);
		schema.put(6, 0d);
		return schema;
	}

}
