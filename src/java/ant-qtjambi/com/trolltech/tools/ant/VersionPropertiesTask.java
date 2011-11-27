/****************************************************************************
**
** Copyright (C) 2011 Darryl L. Miles.  All rights reserved.
** Copyright (C) 2011 D L Miles Consulting Ltd.  All rights reserved.
**
** This file is part of Qt Jambi.
**
**
** $BEGIN_LICENSE$
** GNU Lesser General Public License Usage
** This file may be used under the terms of the GNU Lesser
** General Public License version 2.1 as published by the Free Software
** Foundation and appearing in the file LICENSE.LGPL included in the
** packaging of this file.  Please review the following information to
** ensure the GNU Lesser General Public License version 2.1 requirements
** will be met: http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
** 
** In addition, as a special exception, the copyright holders grant you
** certain additional rights. These rights are described in the Nokia Qt
** LGPL Exception version 1.0, included in the file LGPL_EXCEPTION.txt in
** this package.
** 
** GNU General Public License Usage
** Alternatively, this file may be used under the terms of the GNU
** General Public License version 2.0 as published by the Free Software
** Foundation and appearing in the file LICENSE.GPL2 included in the
** packaging of this file.  Please review the following information to
** ensure the GNU General Public License version 2.0 requirements will be
** met: http://www.gnu.org/licenses/gpl-2.0.html
**
** GNU General Public License Usage
** Alternatively, this file may be used under the terms of the GNU
** General Public License version 3.0 as published by the Free Software
** Foundation and appearing in the file LICENSE.GPL3 included in the
** packaging of this file.  Please review the following information to
** ensure the GNU General Public License version 3.0 requirements will be
** met: http://www.gnu.org/copyleft/gpl.html
** $END_LICENSE$
**
** This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
** WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
**
****************************************************************************/

package com.trolltech.tools.ant;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.PropertyHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

import java.util.Properties;

public class VersionPropertiesTask extends Task {

    private PropertyHelper propertyHelper;

    private boolean verbose;
    private String pathVersionProperties;
    private String pathVersionPropertiesTemplate;

    public static final String DEFAULT_PATH_VERSION_PROPERTIES			= "version.properties";
    public static final String DEFAULT_PATH_VERSION_PROPERTIES_TEMPLATE		= "version.properties.template";

    public boolean isVerbose() {
        return verbose;
    }
    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public String getPathVersionProperties() {
        return pathVersionProperties;
    }
    public void setPathVersionProperties(String pathVersionProperties) {
        this.pathVersionProperties = pathVersionProperties;
    }

    public String getPathVersionPropertiesTemplate() {
        return pathVersionPropertiesTemplate;
    }
    public void setPathVersionPropertiesTemplate(String pathVersionPropertiesTemplate) {
        this.pathVersionPropertiesTemplate = pathVersionPropertiesTemplate;
    }

    public void execute() throws BuildException {
        propertyHelper = PropertyHelper.getPropertyHelper(getProject());

        String tmpPathVersionProperties = pathVersionProperties;
        if(tmpPathVersionProperties == null)
            tmpPathVersionProperties = (String) propertyHelper.getProperty(InitializeTask.QT_VERSION_PROPERTIES);	// ANT 1.7.x
        if(tmpPathVersionProperties == null)
            tmpPathVersionProperties = DEFAULT_PATH_VERSION_PROPERTIES;
        File fileVersion = new File(tmpPathVersionProperties);

        String tmpPathVersionPropertiesTemplate = pathVersionPropertiesTemplate;
        if(tmpPathVersionPropertiesTemplate == null)
            tmpPathVersionPropertiesTemplate = (String) propertyHelper.getProperty(InitializeTask.QT_VERSION_PROPERTIES_TEMPLATE);	// ANT 1.7.x
        if(tmpPathVersionPropertiesTemplate == null)
            tmpPathVersionPropertiesTemplate = DEFAULT_PATH_VERSION_PROPERTIES_TEMPLATE;
        File fileTemplate = new File(tmpPathVersionPropertiesTemplate);

        String qtVersion = (String) propertyHelper.getProperty(InitializeTask.QT_VERSION);	// ANT 1.7.x
        if(qtVersion == null)
            throw new BuildException("Unable to determine Qt version, try editing: " + fileTemplate.getAbsolutePath());

        String qtjambiSonameVersionMajor = (String) propertyHelper.getProperty(InitializeTask.QTJAMBI_SONAME_VERSION_MAJOR);

        buildNewVersionProperties(fileVersion, fileTemplate, qtVersion, qtjambiSonameVersionMajor);
    }

    private boolean buildNewVersionProperties(File fileVersion, File fileTemplate, String qtVersion, String qtjambiSonameVersionMajor) {
        boolean allOk = false;
        // FIXME: This part below should really be a sub-operation / new Ant Task that
        //  modifies the version.properties file

        // If detected, open version.properties to set version (or warning mismatch)
        if(true /*&& !foundInVersionPropertiesTemplate*/) {
            // Build a version.properties file.
            File fileOut = new File(fileVersion.getAbsolutePath());
            if(fileOut.exists()) {
                if(fileOut.delete() == false)
                    throw new BuildException("Unable to delete file: " + fileOut.getAbsolutePath());
                if(verbose)	// getLog() ??
                System.out.println("Deleted file to rebuild " + fileOut.getAbsolutePath());
            }

            InputStream inStream = null;
            OutputStream outStream = null;
            Properties props = null;
            try {
                inStream = new FileInputStream(fileTemplate);

                props = new Properties();
                props.load(inStream);		// read in
                props.put(InitializeTask.VERSION, qtVersion);	// set version
                if(qtjambiSonameVersionMajor != null)
                    props.put(InitializeTask.QTJAMBI_SONAME_VERSION_MAJOR, qtjambiSonameVersionMajor);  // set version

                inStream.close();
                inStream = null;

                outStream = new FileOutputStream(fileOut);
                String header = "Auto-generated by " + getClass().getName();
                props.store(outStream, header);	// write out

                outStream.close();
                outStream = null;

                allOk = true;
            } catch(IOException e) {
                throw new BuildException(e);
            } finally {
                if(outStream != null) {
                    try {
                        outStream.close();
                    } catch(IOException eat) {
                    }
                    outStream = null;
                }
                if(inStream != null) {
                    try {
                        inStream.close();
                    } catch(IOException eat) {
                    }
                    inStream = null;
                }
            }

            if(verbose)	// getLog() ??
                System.out.println("File rebuilt " + fileOut.getAbsolutePath() + " from template file " + fileTemplate.getAbsolutePath());
        }

        return allOk;
    }
}
