package com.trolltech.qtproject.editors;

import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.Token;

import com.trolltech.qtproject.QtProConstants;

public class ProPartitionScanner extends RuleBasedPartitionScanner
{
	public final static String PRO_COMMENT = "__pro_comment";
	public final static String PRO_SETTINGS = "__pro_settings";
	public final static String PRO_FILES = "__pro_files";
	public final static String[] PARTITION_TYPES = 
		{PRO_COMMENT, PRO_SETTINGS, PRO_FILES};
	
	public ProPartitionScanner()
	{
		super();
		IPredicateRule[] rules = new IPredicateRule[3];
		Token comment = new Token(PRO_COMMENT);
		Token settings = new Token(PRO_SETTINGS);
		Token files = new Token(PRO_FILES);
		
		rules[0] = new MultiLineRule(QtProConstants.SETTINGS_BEGIN_TAG, 
				QtProConstants.SETTINGS_END_TAG, settings);
		rules[1] = new MultiLineRule(QtProConstants.FILES_BEGIN_TAG, 
				QtProConstants.FILES_END_TAG, files);
		rules[2] = new EndOfLineRule("#", comment);
		
		setPredicateRules(rules);
	}
}
