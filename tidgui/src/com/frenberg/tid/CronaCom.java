/**
 * 
 */
package com.frenberg.tid;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author daniel.frenberg
 * 
 */
public class CronaCom {

	protected String cookie = null;
	protected String sid = null;
	protected String kortnr = null;
	protected String pin = null;

	public ArrayList<String> getTimesFromCronaTid(String kortnr, String pin)
			throws IOException, ParserConfigurationException, SAXException {
		this.kortnr = kortnr;
		this.pin = pin;

		String xml = null;

		if (doLogin()) {
			xml = doGetStamplist();
		}

		ArrayList<String> stamplingar = new ArrayList<String>();
		if (xml != null) {
			stamplingar = parseXMLDoc(xml);
		}
		
		ArrayList<String> response =  new ArrayList<String>();
		Pattern p = Pattern.compile("(\\d\\d\\:\\d\\d)");
		Matcher m;
		for(String stampling : stamplingar) {
			m = p.matcher(stampling);
			if (m.find()) {
				response.add(m.group(1));
			}
		}
		Collections.reverse(response);
		return response;
	}

	private boolean doLogin() throws IOException {
		boolean response = false;

		String formData = "cmd=login&kortnr="
				+ URLEncoder.encode(kortnr, "UTF-8") + "&pinkod="
				+ URLEncoder.encode(pin, "UTF-8") + "&frm_forw="
				+ URLEncoder.encode("Logga in", "UTF-8");

		HttpURLConnection con = (HttpURLConnection) new URL("http://tid.fortnox.local/")
				.openConnection();
		con.setInstanceFollowRedirects(false);
		con.setDoInput(true);
		con.setDoOutput(true);
		con.setRequestMethod("POST");
		con.setRequestProperty("Content-Type",
				"application/x-www-form-urlencoded");
		con.setRequestProperty("Content-Length",
				Integer.toString(formData.getBytes("UTF-8").length));
		con.getOutputStream().write(formData.getBytes("UTF-8"));

		// hämta cookie
		String locationHeader = con.getHeaderField("Location");
		if (locationHeader != null) {
			String[] tmps = locationHeader.split("=");
			this.sid = tmps[1];
		}

		String cookieHeader = con.getHeaderField("SET-COOKIE");
		if (cookieHeader != null) {
			String[] tmps = cookieHeader.split(" ");
			this.cookie = tmps[0];
			response = true;
		}

		con.disconnect();
		return response;
	}

	private String doGetStamplist() throws IOException {

		Long timestamp = (Long)System.currentTimeMillis() / 1000; // milliseconds to seconds

		URL url = new URL("http://tid.fortnox.local/webbtidur/?cmd=getstamplistxml&kortnr="
				+ this.kortnr + "&sid=" + this.sid + "&uid=" + timestamp.toString());
		
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		
		con.setDoInput(true);
		con.setDoOutput(true);
		con.setRequestMethod("GET");
		con.setRequestProperty("Cookie", "crona_cbo_login_value_1=true; "
				+ this.cookie);
		// send request...
		con.connect();
		
		InputStream is = con.getInputStream();
		BufferedReader rd = new BufferedReader(new InputStreamReader(is));
		String line;
		StringBuffer xmldoc = new StringBuffer();
		while ((line = rd.readLine()) != null) {
			xmldoc.append(line);
			xmldoc.append('\n');
		}
		rd.close();
		is.close();
		con.disconnect();
		return xmldoc.toString();
	}

	private ArrayList<String> parseXMLDoc(String xml) throws ParserConfigurationException, SAXException, IOException {
		ArrayList<String> response = new ArrayList<String>();
		
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		InputSource is = new InputSource(new StringReader(xml));
		Document doc = dBuilder.parse(is);
		
		doc.getDocumentElement().normalize();
				 
		NodeList nList = doc.getElementsByTagName("stampling");
	 
		for (int n = 0; n < nList.getLength(); n++) {
	 		Node nNode = nList.item(n);
	 
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
	 			Element eElement = (Element) nNode;
				response.add(eElement.getTextContent());
			}
		}
		
		return response;
	}
}
