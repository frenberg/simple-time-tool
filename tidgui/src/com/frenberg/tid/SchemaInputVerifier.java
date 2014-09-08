/**
 * 
 */
package com.frenberg.tid;

import java.math.BigDecimal;

import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JTextField;

/**
 * @author frenberg
 *
 */
public class SchemaInputVerifier extends InputVerifier {

	/* (non-Javadoc)
	 * @see javax.swing.InputVerifier#verify(javax.swing.JComponent)
	 */
	@Override
	public boolean verify(JComponent input) {
        String text = ((JTextField) input).getText();
        try {
            BigDecimal value = new BigDecimal(text);
            return (value.scale() <= Math.abs(2)); 
        } catch (NumberFormatException e) {
            return false;
        }
	}

}
