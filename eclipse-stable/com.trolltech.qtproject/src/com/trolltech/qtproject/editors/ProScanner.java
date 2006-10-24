package com.trolltech.qtproject.editors;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;

import com.trolltech.qtproject.QtProConstants;

public class ProScanner extends RuleBasedScanner
{
	public ProScanner(ColorManager manager)
	{
		setDefaultReturnToken(new Token(
				new TextAttribute(
                        manager.getColor(QtProConstants.PRO_DEFAULT_COLOR))));

		//add additional stuff if needed
	}
	
}
