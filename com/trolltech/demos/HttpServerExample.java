/****************************************************************************
**
** Copyright (C) 1992-$THISYEAR$ $TROLLTECH$. All rights reserved.
**
** This file is part of $PRODUCT$.
**
** $JAVA_LICENSE$
**
** This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
** WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
**
****************************************************************************/

package com.trolltech.demos;
import com.trolltech.examples.QtJambiExample;
import com.trolltech.qt.core.QObject;
import com.trolltech.qt.core.QTextStream;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.network.*;
import com.trolltech.qt.network.QHostAddress.SpecialAddress;

@QtJambiExample(name = "Simple HTTP Server")
public class HttpServerExample extends QWidget {

	public static void main(String[] args) {
		QApplication.initialize(args);

		HttpServerExample example = new HttpServerExample(null);
		example.show();

		QApplication.exec();
	}

	HttpServer server;
	QTextEdit editor;

	public HttpServerExample(QWidget parent) {
		server = new HttpServer(this);
		if (!server.start()) {
			QMessageBox.critical(this, tr("HTTP Server"),
					tr("Unable to start the server: ") + server.errorString());
			close();
		}

		QPushButton publishButton = new QPushButton(this);
		publishButton.setText("Publish");
		editor = new QTextEdit(this);

		editor.setPlainText("<h1>Server is up and running!</h1>"
				+ "You should be able to view it in a normal web browser."
				+ " Try this address: http://localhost:" + (int) server.serverPort());

		QGridLayout layout = new QGridLayout(this);
		setLayout(layout);
		layout.addWidget(publishButton);
		layout.addWidget(editor);

		publishButton.clicked.connect(this, "publish()");
		
        setWindowTitle(tr("Simple HTTP Server"));
        setWindowIcon(new QIcon("classpath:com/trolltech/images/qt-logo.png"));
	}

	protected void publish() {
		server.publish(editor.toPlainText());
	}

	class HttpServer extends QTcpServer {
		private String text;

		public HttpServer(QObject parent) {
			super(parent);
		}

		public void publish(String text) {
			this.text = text;
		}

		public boolean start() {
			if (!listen(new QHostAddress(SpecialAddress.Any), (char) 8080)) {
				close();
				return false;
			}

			this.newConnection.connect(this, "newConnection()");

			return true;
		}

		protected void newConnection() {
			QTcpSocket socket = nextPendingConnection();
			if (socket != null) {
				socket.readyRead.connect(this, "readClient()");
				socket.disconnected.connect(socket, "disposeLater()");
			}
		}

		protected void readClient() {
			QTcpSocket socket = (QTcpSocket) signalSender();

			if (socket.canReadLine()) {
				if (socket.readLine().startsWith("GET ")) {
					QTextStream os = new QTextStream(socket);
					os.setCodec("utf-8");

					os.operator_shift_left("HTTP/1.0 200 Ok\r\n");
					os.operator_shift_left("Content-Type: text/html; charset=\"utf-8\"\r\n");
					os.operator_shift_left("\r\n");
					if (text != null && !text.equals("")) {
						os.operator_shift_left(text);
					} else {
						os.operator_shift_left("<h1>This page is empty</h1>");
					}
					socket.close();
				}
			}
		}
	}
}
