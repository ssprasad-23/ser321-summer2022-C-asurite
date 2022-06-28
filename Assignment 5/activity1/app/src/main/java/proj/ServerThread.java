package proj;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

import org.json.JSONObject;

/**
 * SERVER
 * This is the ServerThread class that has a socket where we accept clients contacting us.
 * We save the clients ports connecting to the server into a List in this class. 
 * When we wand to send a message we send it to all the listening ports
 */

public class ServerThread extends Thread{
	private ServerSocket serverSocket;
	private Set<Socket> listeningSockets = new HashSet<Socket>();
	
	public ServerThread(String portNum) throws IOException {
		serverSocket = new ServerSocket(Integer.valueOf(portNum));
	}
	
	/**
	 * Starting the thread, we are waiting for clients wanting to talk to us, then save the socket in a list
	 */
	public void run() {
		try {
			while (true) {
				Socket socket = serverSocket.accept();
				listeningSockets.add(socket);
				receiveInitialMessage(socket);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Receiving first message from a client, print it
	 */
	private void receiveInitialMessage(Socket socket) {
		BufferedReader bufferedReader;
		try {
			bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			JSONObject json = new JSONObject(bufferedReader.readLine());
			System.out.println("initial message [" + json.getString("username")+"]: " + json.getString("message"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Sending the message to the OutputStream for each socket that we saved
	 */
	void sendMessage(String message) {
		try {
			for (Socket s : listeningSockets) {
				PrintWriter out = new PrintWriter(s.getOutputStream(), true);
				out.println(message);
		     }
		} catch(Exception e) {
			e.printStackTrace();
		}
	}	
}