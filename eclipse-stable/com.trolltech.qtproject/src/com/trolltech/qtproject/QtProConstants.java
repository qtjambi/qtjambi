package com.trolltech.qtproject;

import org.eclipse.swt.graphics.RGB;

public interface QtProConstants
{
	final static String FILES_BEGIN_TAG = "# ECLIPSE_PROJECT_FILES_BEGIN";
	final static String FILES_END_TAG = "# ECLIPSE_PROJECT_FILES_END";
	final static String SETTINGS_BEGIN_TAG = "# ECLIPSE_PROJECT_SETTINGS_BEGIN";
	final static String SETTINGS_END_TAG = "# ECLIPSE_PROJECT_SETTINGS_END";
	
	final static String QTBUILDER_ID = "com.trolltech.qtproject.QtMakefileGenerator";
	final static String QTNATURE_ID = "com.trolltech.qtproject.QtNature";
	
	final static String MAKE_ID = "org.eclipse.cdt.make.core.makeBuilder";
	final static String SCANNER_ID = "org.eclipse.cdt.make.core.ScannerConfigBuilder";
	
	final static String MAKE_BUILD_CMD = "org.eclipse.cdt.make.core.buildCommand";
	final static String MAKE_DEFAULT_BUILD = "org.eclipse.cdt.make.core.useDefaultBuildCmd";
	
	final static String MAKE_PROJECT_ID = "org.eclipse.cdt.make.core.make";
	
	final static String KEY_TARGET = "Target";
	final static String KEY_MODULES = "QtModules";
	final static String KEY_TEMPLATE = "Template";
	final static String KEY_CONFIGURATION = "Configuration";
	
	RGB PRO_SETTINGS_COLOR = new RGB(188, 188, 188);
	RGB PRO_SOURCE_COLOR = new RGB(188, 188, 188);
	RGB PRO_COMMENT_COLOR = new RGB(63, 127, 95);
	RGB PRO_DEFAULT_COLOR = new RGB(0, 0, 255);
	
	static final int NoModules	= 0x00;
	static final int QtCore		= 0x01;
	static final int QtGui		= 0x02;
	static final int QtSql		= 0x04;
	static final int QtXml		= 0x08;
	static final int QtSvg		= 0x10;
	static final int QtOpenGL	= 0x20;
	static final int QtNetwork	= 0x40;
	static final int Qt3Support = 0x80;
}