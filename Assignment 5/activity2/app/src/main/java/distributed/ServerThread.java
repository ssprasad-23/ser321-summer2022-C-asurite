package distributed;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public abstract class ServerThread extends Thread {
	private ServerSocket serverSocket;
	
	public ServerThread(int portNum) throws IOException {
		serverSocket = new ServerSocket(portNum);
	}

	abstract public void onNewClient(NodeThread clientNode);
	
	abstract public void onMessage(String message);

	abstract public void onClientDisconnect(NodeThread clientNode);

	@Override
	public void run() {
		try {
			while (true) {
				Socket socket = serverSocket.accept();
				NodeThread clientNote = new NodeThread(socket) {
					@Override
					public void newMessage(String message) {
						onMessage(message);
					}

					@Override
					public void onDisconnect() {
						onClientDisconnect(this);
					}
				};
                if (clientNote.keepAlive) {
				    clientNote.start();
                }
				onNewClient(clientNote);
			}
		} catch (Exception e) {
			try {
				serverSocket.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
	}
	
}