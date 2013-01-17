/**
 * 
 */
package com.frenberg.tid;

import javax.swing.JFrame;

/**
 * @author daniel.frenberg
 *
 */
public class TidGUI {
	
	private static void createAndShowGUI() {
		TidJFrame frame = new TidJFrame("Tid");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}

}
