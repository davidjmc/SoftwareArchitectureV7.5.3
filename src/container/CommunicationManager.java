package container;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import framework.configuration.Configuration;
import utils.Utils;

public class CommunicationManager {

	// ----------------- check naming port -------//

	public int executionEnvironmentPort(Configuration conf) {
		int port = 0;

		if (conf.hasNamingService())
			port = Utils.NAMING_SERVICE_PORT;
//		else if (conf.hasMessagingService())
//			port = Utils.MESSAGING_SERVICE_PORT;
		else
			port = Utils.nextPortAvailable();

		return port;
	}

	// ----------------- send --------------------//
	public synchronized int send(byte[] msg, String host, int port) {
		boolean connected = false;
		Socket clientSocket = null;
		DataOutputStream outToClient = null;
		int msgSize = 0;
		int localPort = 99999;
		
		while (!connected) {
				try {
					clientSocket = new Socket(host, port);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				connected = true;
		}
		
		try {
			outToClient = new DataOutputStream(clientSocket.getOutputStream());
			msgSize = msg.length;
			localPort = clientSocket.getLocalPort();
			outToClient.writeInt(msgSize);
			outToClient.write(msg, 0, msgSize);
			outToClient.flush();

			clientSocket.setReuseAddress(true);
			clientSocket.close();
			outToClient.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return localPort;
	}

	// ----------------- receive --------------------//
	public synchronized ArrayList<Object> receive(int port) {
		ServerSocket welcomeSocket = null;
		Socket connectionSocket = null;
		DataInputStream inFromClient = null;
		int rcvMsgSize;
		byte[] rcvMsg = null;
		boolean portBusy = true;
		ArrayList<Object> rcvInformation = new ArrayList<Object>();
		String remoteHost = "";
		int remotePort = 99999;
		
		while (portBusy) {
			try {
				welcomeSocket = new ServerSocket(port);
				connectionSocket = welcomeSocket.accept();
				inFromClient = new DataInputStream(connectionSocket.getInputStream());
				rcvMsgSize = inFromClient.readInt();
				remotePort = ((InetSocketAddress) connectionSocket.getRemoteSocketAddress()).getPort();
				remoteHost = (((InetSocketAddress) connectionSocket.getRemoteSocketAddress()).getAddress()).toString()
						.replace("/", "");
				rcvMsg = new byte[rcvMsgSize];
				inFromClient.read(rcvMsg, 0, rcvMsgSize);
				portBusy = false;
			} catch (IOException e1) {
			}
		}

		try {
			welcomeSocket.setReuseAddress(true);
			welcomeSocket.close();
			connectionSocket.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

		rcvInformation.add(remoteHost);
		rcvInformation.add(remotePort);
		rcvInformation.add(rcvMsg);

		return rcvInformation;
	}
}
