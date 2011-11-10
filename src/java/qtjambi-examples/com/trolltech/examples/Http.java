/****************************************************************************
**
** Copyright (C) 1992-2009 Nokia. All rights reserved.
**
** This file is part of Qt Jambi.
**
** ** $BEGIN_LICENSE$
** Commercial Usage
** Licensees holding valid Qt Commercial licenses may use this file in
** accordance with the Qt Commercial License Agreement provided with the
** Software or, alternatively, in accordance with the terms contained in
** a written agreement between you and Nokia.
** 
** GNU Lesser General Public License Usage
** Alternatively, this file may be used under the terms of the GNU Lesser
** General Public License version 2.1 as published by the Free Software
** Foundation and appearing in the file LICENSE.LGPL included in the
** packaging of this file.  Please review the following information to
** ensure the GNU Lesser General Public License version 2.1 requirements
** will be met: http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html.
** 
** In addition, as a special exception, Nokia gives you certain
** additional rights. These rights are described in the Nokia Qt LGPL
** Exception version 1.0, included in the file LGPL_EXCEPTION.txt in this
** package.
** 
** GNU General Public License Usage
** Alternatively, this file may be used under the terms of the GNU
** General Public License version 3.0 as published by the Free Software
** Foundation and appearing in the file LICENSE.GPL included in the
** packaging of this file.  Please review the following information to
** ensure the GNU General Public License version 3.0 requirements will be
** met: http://www.gnu.org/copyleft/gpl.html.
** 
** If you are unsure which license is appropriate for your use, please
** contact the sales department at qt-sales@nokia.com.
** $END_LICENSE$

**
** This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
** WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
**
****************************************************************************/

package com.trolltech.examples;

import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.network.*;


@QtJambiExample(name = "Http Example")
public class Http extends QDialog
{
    private QLabel statusLabel;
    private QLabel urlLabel;
    private QLineEdit urlLineEdit;
    private QProgressDialog progressDialog;
    private QPushButton downloadButton;
    private QPushButton quitButton;
    private QDialogButtonBox buttonBox;

    private QHttp http;
    private QFile file;
    private int httpGetId;
    private boolean httpRequestAborted;

    public Http()
    {
        urlLineEdit = new QLineEdit("https://");

        urlLabel = new QLabel(tr("&URL:"));
        urlLabel.setBuddy(urlLineEdit);
        statusLabel = new QLabel(tr("Please enter the URL of a file you want "
                                    + " to download."));

        downloadButton = new QPushButton(tr("Download"));
        downloadButton.setDefault(true);
        quitButton = new QPushButton(tr("Quit"));
        quitButton.setAutoDefault(false);

        buttonBox = new QDialogButtonBox();
        buttonBox.addButton(downloadButton, QDialogButtonBox.ButtonRole.ActionRole);
        buttonBox.addButton(quitButton, QDialogButtonBox.ButtonRole.RejectRole);

        progressDialog = new QProgressDialog(this);

        http = new QHttp(this);

        urlLineEdit.textChanged.connect(this, "enableDownloadButton()");
        http.requestFinished.connect(this, "httpRequestFinished(int, boolean)");
        http.dataReadProgress.connect(this, "updateDataReadProgress(int,int)");
        http.responseHeaderReceived.connect(this,
            "readResponseHeader(QHttpResponseHeader)");
        http.authenticationRequired.connect(this,
            "slotAuthenticationRequired(String,int,QAuthenticator)");
        progressDialog.canceled.connect(this, "cancelDownload()");
        downloadButton.clicked.connect(this, "downloadFile()");
        quitButton.clicked.connect(this, "close()");

        QHBoxLayout topLayout = new QHBoxLayout();
        topLayout.addWidget(urlLabel);
        topLayout.addWidget(urlLineEdit);

        QVBoxLayout mainLayout = new QVBoxLayout();
        mainLayout.addLayout(topLayout);
        mainLayout.addWidget(statusLabel);
        mainLayout.addWidget(buttonBox);
        setLayout(mainLayout);

        setWindowTitle(tr("HTTP"));
        urlLineEdit.setFocus();
    }

    private void downloadFile()
    {
        String text = urlLineEdit.text();

        QUrl url = new QUrl(text);
        if (text.endsWith(url.host()))
            url = new QUrl(text + "/");

        QFileInfo fileInfo = new QFileInfo(url.path());
        String fileName = fileInfo.fileName();
        if (fileName.equals(""))
            fileName = "index.html";

        if (QFile.exists(fileName)) {
            QMessageBox.StandardButtons buttons = QMessageBox.StandardButton.createQFlags(
                QMessageBox.StandardButton.Ok, QMessageBox.StandardButton.Cancel);

            if (QMessageBox.question(this, tr("HTTP"), tr("There already exists a file called ")
                + fileName + tr(" the current directory. Overwrite?"), buttons,
                QMessageBox.StandardButton.Cancel).equals(QMessageBox.StandardButton.Cancel))
                return;

            QFile.remove(fileName);
        }

        file = new QFile(fileName);
        if (!file.open(QIODevice.OpenModeFlag.WriteOnly)) {
            QMessageBox.information(this, tr("HTTP"), tr("Unable to save the file ")
                                    + fileName + tr(":") + file.errorString());
            file = null;
            return;
        }

        QHttp.ConnectionMode mode = url.scheme().toLowerCase().equals("https")
                                    ? QHttp.ConnectionMode.ConnectionModeHttps
                                    : QHttp.ConnectionMode.ConnectionModeHttp;
        http.setHost(url.host(), mode, url.port() == -1 ? 0 : url.port());

        if (!url.userName().equals(""))
            http.setUser(url.userName(), url.password());

        httpRequestAborted = false;
        httpGetId = http.get(url.path(), file);

        progressDialog.show();
        progressDialog.setWindowTitle(tr("HTTP"));
        progressDialog.setLabelText(tr("Downloading: ") + fileName + tr("."));
        downloadButton.setEnabled(false);
    }

    private void cancelDownload()
    {
        statusLabel.setText(tr("Download canceled."));
        httpRequestAborted = true;
        http.abort();
        downloadButton.setEnabled(true);
    }

    private void httpRequestFinished(int requestId, boolean error)
    {
        if (requestId != httpGetId)
            return;
        if (httpRequestAborted) {
            if (file != null) {
                file.close();
                file.remove();
                file = null;
            }

            progressDialog.hide();
            return;
        }

        if (requestId != httpGetId)
            return;

        progressDialog.hide();
        file.close();

        if (error) {
            file.remove();
            QMessageBox.information(this, tr("HTTP"),
                                     tr("Download failed ") +
                                     http.errorString() + tr("."));
        } else {
            String fileName = new QFileInfo(new QUrl(urlLineEdit.text()).path()).fileName();
            statusLabel.setText(tr("Downloaded: \"") + fileName + tr("\" to current directory."));
        }

        downloadButton.setEnabled(true);
        file = null;
    }

    private void readResponseHeader(QHttpResponseHeader responseHeader)
    {
        if (responseHeader.statusCode() != 200) {
            QMessageBox.information(this, tr("HTTP"),
                                     tr("Download failed: ")
                                     +responseHeader.reasonPhrase() + tr("."));
            httpRequestAborted = true;
            progressDialog.hide();
            http.abort();
        }
    }

    private void updateDataReadProgress(int bytesRead, int totalBytes)
    {
        if (httpRequestAborted)
            return;

        progressDialog.setMaximum(totalBytes);
        progressDialog.setValue(bytesRead);
    }

    private void enableDownloadButton()
    {
        downloadButton.setEnabled(!urlLineEdit.text().equals(""));
    }

    private void slotAuthenticationRequired(String hostName, int i, QAuthenticator authenticator)
    {
        QDialog dlg = new QDialog();
        Ui_Dialog ui = null;	// FIXME: This is must be an error?  test this code sometime
        ui.setupUi(dlg);
        dlg.adjustSize();
        ui.siteDescription.setText(authenticator.realm() + tr(" at ") + hostName);

        if (dlg.exec() == QDialog.DialogCode.Accepted.value()) {
            authenticator.setUser(ui.userEdit.text());
            authenticator.setPassword(ui.passwordEdit.text());
        }
    }

    public static void main(String args[])
    {
        QApplication.initialize(args);

        Http window = new Http();
        window.show();

        QApplication.execStatic();
        QApplication.shutdown();
    }

}
