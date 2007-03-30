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

package com.trolltech.examples;

import com.trolltech.qt.core.*;
import com.trolltech.qt.core.Qt.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.network.*;

import java.util.*;

@QtJambiExample(name = "Chat")
public class Chat extends QDialog {

    public static void main(String[] args) {

        QApplication.initialize(args);        
        Chat chat = new Chat();
        chat.show();
        QApplication.exec();
        
    }

    private Ui_ChatDialog ui = new Ui_ChatDialog();
    private Client client = new Client();
    private String myNickName;
    private QTextTableFormat tableFormat = new QTextTableFormat();

    public Chat() {
        this(null);
    }

    public Chat(QWidget parent) {
        super(parent);

        ui.setupUi(this);

        ui.lineEdit.setFocusPolicy(Qt.FocusPolicy.StrongFocus);
        ui.textEdit.setFocusPolicy(Qt.FocusPolicy.NoFocus);
        ui.textEdit.setReadOnly(true);
        ui.listWidget.setFocusPolicy(Qt.FocusPolicy.NoFocus);

        ui.lineEdit.returnPressed.connect(this, "returnPressed()");

        client.newMessage__from__message.connect(this, "appendMessage(String, String)");
        client.newParticipant__nick.connect(this, "newParticipant(String)");
        client.participantLeft__nick.connect(this, "participantLeft(String)");

        myNickName = client.nickName();
        newParticipant(myNickName);
        tableFormat.setBorder(0);

        QTimer.singleShot(10 * 1000, this, "showInformation()");
    }

    public String tr(String str, Object... arguments) {
        return String.format(tr(str), arguments);
    }

    private void appendMessage(final String from, final String message) {
        if (from.equals("") || message.equals(""))
            return;

        QTextCursor cursor = new QTextCursor(ui.textEdit.textCursor());
        cursor.movePosition(QTextCursor.MoveOperation.End);
        QTextTable table = cursor.insertTable(1, 2, tableFormat);
        table.cellAt(0, 0).firstCursorPosition().insertText("<" + from + "> ");
        table.cellAt(0, 1).firstCursorPosition().insertText(message);
        QScrollBar bar = ui.textEdit.verticalScrollBar();
        bar.setValue(bar.maximum());
    }

    void returnPressed() {
        String text = ui.lineEdit.text();
        if (text.equals(""))
            return;

        if (text.startsWith("/")) {
            QColor color = ui.textEdit.textColor();
            ui.textEdit.setTextColor(QColor.red);
            ui.textEdit.append(tr("! Unknown command: ") + text.substring(text.indexOf(' ')));
            ui.textEdit.setTextColor(color);
        } else {
            client.sendMessage(text);
            appendMessage(myNickName, text);
        }

        ui.lineEdit.clear();
    }

    private void newParticipant(final String nick) {
        if (nick.equals(""))
            return;

        QColor color = ui.textEdit.textColor();
        ui.textEdit.setTextColor(QColor.gray);
        ui.textEdit.append(tr("* %1$s has joined", nick));
        ui.textEdit.setTextColor(color);
        ui.listWidget.addItem(nick);
    }

    void participantLeft(final String nick) {
        if (nick.equals(""))
            return;

        List<QListWidgetItem> items = ui.listWidget.findItems(nick, MatchFlag.MatchExactly);        

        // temporary workaround, should be replaced by items.get(0).displose();
        for (int i = 0; i < ui.listWidget.count(); i++) {
            if (ui.listWidget.item(i).data(0).equals(items.get(0).data(0))) {
                ui.listWidget.takeItem(i).dispose();
            }
        }
        
        QColor color = ui.textEdit.textColor();
        ui.textEdit.setTextColor(QColor.gray);
        ui.textEdit.append(tr("* %1$s has left", nick));
        ui.textEdit.setTextColor(color);
    }

    void showInformation() {
        if (ui.listWidget.count() == 1) {
            QMessageBox.information(this, tr("Chat"), tr("Launch several instances of this "
                    + "program on your local network and " + "start chatting!"));
        }
    }
}

class Client extends QObject {

    private PeerManager peerManager;
    private Server server = new Server(this);
    private Hashtable<QHostAddress, Vector<Connection>> peers = new Hashtable<QHostAddress, Vector<Connection>>();

    // from, message
    Signal2<String, String> newMessage__from__message = new Signal2<String, String>();
    // nick
    Signal1<String> newParticipant__nick = new Signal1<String>();
    // nick
    Signal1<String> participantLeft__nick = new Signal1<String>();

    Client() {
        peerManager = new PeerManager(this);
        peerManager.setServerPort(server.serverPort());
        peerManager.startBroadcasting();

        peerManager.newConnection.connect(this, "newConnection(Connection)");
        server.newConnection.connect(this, "newConnection(Connection)");

    }

    void sendMessage(final String message) {
        if (message.equals(""))
            return;

        Collection<Vector<Connection>> connectionVector = peers.values();

        for (Vector<Connection> connections : connectionVector) {

            for (Connection connection : connections) {
                connection.sendMessage(message);
            }
        }
    }

    String nickName() {
        return peerManager.userName() + "@" + QHostInfo.localHostName() + ":"
                + (int) server.serverPort();
    }

    boolean hasConnection(final QHostAddress senderIp, int senderPort) {

        if (senderPort == -1) {
            return peers.containsKey(senderIp);
        }

        if (!peers.containsKey(senderIp)) {
            return false;
        }

        List<Connection> connections = peers.get(senderIp);
        if (connections != null)
            for (Connection connection : connections) {
                if (connection.peerPort() == (char) senderPort) {
                    return true;
                }
            }

        return false;
    }

    void newConnection(Connection connection) {
        connection.setGreetingMessage(peerManager.userName().toString());

        connection.error.connect(this, "connectionError(QAbstractSocket$SocketError)");
        connection.disconnected.connect(this, "disconnected()");
        connection.readyForUse.connect(this, "readyForUse()");
    }

    void readyForUse() {
        Connection connection = (Connection) signalSender();
        if (connection == null || hasConnection(connection.peerAddress(), connection.peerPort()))
            return;

        connection.newMessage.connect(this.newMessage__from__message);

        Vector<Connection> connections = peers.get(connection.peerAddress());
        if (connections != null) {
            connections.add(connection);
        } else {
            connections = new Vector<Connection>();
            connections.add(connection);
            peers.put(connection.peerAddress(), connections);
        }
        String nick = connection.name();

        if (!nick.equals(""))
            newParticipant__nick.emit(nick);
    }

    void disconnected() {
        Connection connection = (Connection) signalSender();
        if (connection != null)
            removeConnection(connection);
    }

    void connectionError(QAbstractSocket.SocketError socketError) {
        Connection connection = (Connection) signalSender();
        if (connection != null)
            removeConnection(connection);
    }

    private void removeConnection(Connection connection) {
        if (peers.containsKey(connection.peerAddress())) {
            peers.remove(connection.peerAddress());
            String nick = connection.name();
            if (!nick.equals(""))
                participantLeft__nick.emit(nick);
        }
    }
}

class Connection extends QTcpSocket {

    static final int MaxBufferSize = 1024000;
    static final int TransferTimeout = 30 * 1000;
    static final int PongTimeout = 60 * 1000;
    static final int PingInterval = 5 * 1000;
    static final char SeparatorToken = ' ';

    private enum ConnectionState {
        WaitingForGreeting, ReadingGreeting, ReadyForUse
    };

    private enum DataType {
        PlainText, Ping, Pong, Greeting, Undefined
    };

    Signal0 readyForUse = new Signal0();
    Signal2<String, String> newMessage = new Signal2<String, String>();

    private QTextCodec codec = QTextCodec.codecForName(new QByteArray("UTF-8"));

    private String greetingMessage;
    private String username;
    private QTimer pingTimer = new QTimer();
    private QTime pongTime = new QTime();
    private QByteArray buffer = new QByteArray();
    private ConnectionState state;
    private DataType currentDataType;
    private int numBytesForCurrentDataType;
    private int transferTimerId;
    private boolean isGreetingMessageSent;

    Connection(QObject parent) {
        super(parent);
        greetingMessage = tr("undefined");
        username = tr("unknown");
        state = ConnectionState.WaitingForGreeting;
        currentDataType = DataType.Undefined;
        numBytesForCurrentDataType = -1;
        transferTimerId = 0;
        isGreetingMessageSent = false;
        pingTimer.setInterval(PingInterval);

        readyRead.connect(this, "processReadyRead()");
        disconnected.connect(pingTimer, "stop()");
        pingTimer.timeout.connect(this, "sendPing()");
        connected.connect(this, "sendGreetingMessage()");

    }

    String name() {
        return username;
    }

    void setGreetingMessage(final String message) {
        greetingMessage = message;
    }

    boolean sendMessage(final String message) {
        if (message.equals(""))
            return false;

        QByteArray msg = codec.fromUnicode(message);
        QByteArray data = new QByteArray("MESSAGE " + msg.length() + " ");
        data.append(msg);
        return write(data) == data.size();
    }

    public void timerEvent(QTimerEvent timerEvent) {
        if (timerEvent.timerId() == transferTimerId) {
            abort();
            killTimer(transferTimerId);
            transferTimerId = 0;
        }
    }

    void processReadyRead() {
        if (state == ConnectionState.WaitingForGreeting) {
            if (!readProtocolHeader())
                return;
            if (currentDataType != DataType.Greeting) {
                abort();
                return;
            }
            state = ConnectionState.ReadingGreeting;
        }

        if (state == ConnectionState.ReadingGreeting) {
            if (!hasEnoughData())
                return;

            buffer = read(numBytesForCurrentDataType);
            if (buffer.size() != numBytesForCurrentDataType) {
                abort();
                return;
            }

            username = buffer + "@" + peerAddress().toString() + ":" + (int) peerPort();
            currentDataType = DataType.Undefined;
            numBytesForCurrentDataType = 0;
            buffer.clear();

            if (!isValid()) {
                abort();
                return;
            }

            if (!isGreetingMessageSent)
                sendGreetingMessage();

            pingTimer.start();
            pongTime.start();
            state = ConnectionState.ReadyForUse;

            readyForUse.emit();
        }

        do {
            if (currentDataType == DataType.Undefined) {
                if (!readProtocolHeader())
                    return;
            }
            if (!hasEnoughData())
                return;
            processData();
        } while (bytesAvailable() > 0);
    }

    void sendPing() {
        if (pongTime.elapsed() > PongTimeout) {
            abort();
            return;
        }

        write(new QByteArray("PING 1 p"));
    }

    private void sendGreetingMessage() {
        QByteArray greeting = new QByteArray(greetingMessage); // FIXME
        QByteArray data = new QByteArray("GREETING " + greeting.length() + " " + greeting);
        if (write(data) == data.size())
            isGreetingMessageSent = true;
    }

    private int readDataIntoBuffer(int maxSize) {
        if (maxSize > MaxBufferSize)
            return 0;

        int numBytesBeforeRead = buffer.size();
        if (numBytesBeforeRead == MaxBufferSize) {
            abort();
            return 0;
        }

        while (bytesAvailable() > 0 && buffer.size() < maxSize) {
            buffer.append(read(1));
            if (buffer.endsWith("" + SeparatorToken))
                break;

        }
        return buffer.size() - numBytesBeforeRead;
    }

    private int dataLengthForCurrentDataType() {
        if (bytesAvailable() <= 0 || readDataIntoBuffer(MaxBufferSize) <= 0
                || !buffer.endsWith("" + SeparatorToken))
            return 0;

        buffer.chop(1);
        int number = buffer.toInt();
        buffer.clear();
        return number;
    }

    private boolean readProtocolHeader() {
        if (transferTimerId != 0) {
            killTimer(transferTimerId);
            transferTimerId = 0;
        }

        if (readDataIntoBuffer(MaxBufferSize) <= 0) {
            transferTimerId = startTimer(TransferTimeout);
            return false;
        }

        if (buffer.equals("PING ")) {
            currentDataType = DataType.Ping;
        } else if (buffer.equals("PONG ")) {
            currentDataType = DataType.Pong;
        } else if (buffer.equals("MESSAGE ")) {
            currentDataType = DataType.PlainText;
        } else if (buffer.equals("GREETING ")) {
            currentDataType = DataType.Greeting;
        } else {
            currentDataType = DataType.Undefined;
            abort();
            return false;
        }

        buffer.clear();
        numBytesForCurrentDataType = dataLengthForCurrentDataType();
        return true;
    }

    private boolean hasEnoughData() {
        if (transferTimerId != 0) {
            killTimer(transferTimerId);
            transferTimerId = 0;
        }

        if (numBytesForCurrentDataType <= 0)
            numBytesForCurrentDataType = dataLengthForCurrentDataType();

        if (bytesAvailable() < numBytesForCurrentDataType || numBytesForCurrentDataType <= 0) {
            transferTimerId = startTimer(TransferTimeout);
            return false;
        }

        return true;
    }

    private void processData() {
        buffer = read(numBytesForCurrentDataType);
        if (buffer.size() != numBytesForCurrentDataType) {
            abort();
            return;
        }

        switch (currentDataType) {
        case PlainText:
            newMessage.emit(username, codec.toUnicode(buffer));
            break;
        case Ping:
            write(new QByteArray("PONG 1 p"));
            break;
        case Pong:
            pongTime.restart();
            break;
        default:
            break;
        }

        currentDataType = DataType.Undefined;
        numBytesForCurrentDataType = 0;
        buffer.clear();
    }
}

class PeerManager extends QObject {
    static final int BroadcastInterval = 2000;
    static final char broadcastPort = 45000;

    private Client client;
    private Vector<QHostAddress> broadcastAddresses = new Vector<QHostAddress>();
    private Vector<QHostAddress> ipAddresses = new Vector<QHostAddress>();
    private QUdpSocket broadcastSocket = new QUdpSocket();
    private QTimer broadcastTimer = new QTimer();
    private String username = "";
    private int serverPort;

    Signal1<Connection> newConnection = new Signal1<Connection>();

    public PeerManager(Client client) {
        super(client);
        this.client = client;

        List<String> envVariables = new Vector<String>();

        envVariables.add("USERNAME.*");
        envVariables.add("USER.*");
        envVariables.add("USERDOMAIN.*");
        envVariables.add("HOSTNAME.*");
        envVariables.add("DOMAINNAME.*");

        List<String> environment = QProcess.systemEnvironment();

        for (String string : envVariables) {
            int index = 0;
            for (String entry : environment) {
                if (new QRegExp(string).exactMatch(entry))
                    break;
                index++;
            }
            if (index < environment.size()) {
                String[] stringList = environment.get(index).split("=");
                if (stringList.length == 2) {
                    username = stringList[1];
                    break;
                }
            }
        }

        if (username.equals(""))
            username = "unknown";

        updateAddresses();
        serverPort = 0;

        broadcastSocket.bind(new QHostAddress(QHostAddress.SpecialAddress.Any), broadcastPort,
                QUdpSocket.BindFlag.ShareAddress, QUdpSocket.BindFlag.ReuseAddressHint);

        broadcastSocket.readyRead.connect(this, "readBroadcastDatagram()");

        broadcastTimer.setInterval(BroadcastInterval);

        broadcastTimer.timeout.connect(this, "sendBroadcastDatagram()");

    }

    void setServerPort(int port) {
        serverPort = port;
    }

    String userName() {
        return username;
    }

    void startBroadcasting() {
        broadcastTimer.start();
    }

    private boolean isLocalHostAddress(final QHostAddress address) {
        for (QHostAddress localAddress : ipAddresses) {
            if (address.equals(localAddress))
                return true;
        }
        return false;
    }

    void sendBroadcastDatagram() {
        QByteArray datagram = new QByteArray(username + "@" + serverPort);

        boolean validBroadcastAddresses = true;
        for (QHostAddress address : broadcastAddresses) {
            if (broadcastSocket.writeDatagram(datagram, address, broadcastPort) == -1)
                validBroadcastAddresses = false;
        }

        if (!validBroadcastAddresses)
            updateAddresses();

    }

    void readBroadcastDatagram() {

        while (broadcastSocket.hasPendingDatagrams()) {
            byte datagram[] = new byte[(int) broadcastSocket.pendingDatagramSize()];
            QUdpSocket.HostInfo info = new QUdpSocket.HostInfo();
            if (broadcastSocket.readDatagram(datagram, info) == -1)
                continue;

            QByteArray baDatagram = new QByteArray(datagram);
            List<QByteArray> list = baDatagram.split((byte) '@');
            if (list.size() != 2)
                continue;

            char senderServerPort = list.get(1).toChar();

            if (isLocalHostAddress(info.address) && senderServerPort == serverPort)
                continue;

            if (!client.hasConnection(info.address, -1)) {
                Connection connection = new Connection(this);
                newConnection.emit(connection);
                connection.connectToHost(info.address, senderServerPort);
            }
        }
    }

    private void updateAddresses() {
        broadcastAddresses.clear();
        ipAddresses.clear();
        for (QNetworkInterface networkInterface : QNetworkInterface.allInterfaces()) {
            for (QNetworkAddressEntry entry : networkInterface.addressEntries()) {
                QHostAddress broadcastAddress = entry.broadcast();
                if (!broadcastAddress.equals(QHostAddress.SpecialAddress.Null)) {
                    broadcastAddresses.add(broadcastAddress);
                    ipAddresses.add(entry.ip());
                }
            }
        }
    }
}

class Server extends QTcpServer {

    Signal1<Connection> newConnection = new Signal1<Connection>();

    public Server(QObject parent) {
        super(parent);
        listen(new QHostAddress(QHostAddress.SpecialAddress.Any));
    }

    public void incomingConnection(int socketDescriptor) {
        Connection connection = new Connection(this);
        connection.setSocketDescriptor(socketDescriptor);
        newConnection.emit(connection);
    }
}
