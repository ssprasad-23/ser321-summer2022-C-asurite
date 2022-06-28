package distributed;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import org.json.JSONObject;

public class ClientNode extends Thread {
	private Socket socket;
	private PrintWriter serverIn;
	private BufferedReader serverOut;
	private BufferedReader bufferedReaderInput;
	private int debit = 0;
	private String id;

	public ClientNode(String host, int portNum, String id) throws IOException {
		this.id = id;
		socket = new Socket(host, portNum);
		bufferedReaderInput = new BufferedReader(new InputStreamReader(System.in));
		serverOut = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		serverIn = new PrintWriter(socket.getOutputStream(), true);
		serverIn.println("{'type':'client', 'id':'" + id + "'}");
	}

	public void run() {
		while (true) {
			try {
				System.out.println(
					"Enter command\n"
					+ "{"	+ "\texit\n"
							+ "\tcredit <amount>\n"
							+ "\tdebit <amount>\n"
					+"}: "
				);
				String message = bufferedReaderInput.readLine();
				if (message.equals("exit")) {
					break;
				}
				String[] parts = message.split(" ");
				if (parts.length != 2 || !(parts[0].equals("credit") || parts[0].equals("debit"))) {
					System.out.println("Wrong input. Try again");
					continue;
				}
				String messagePayload = "{'id':'" + id 
										+ "','type':'request"
										+ "','command':'" + parts[0] 
										+ "','amount':'" + parts[1] + "'}";
				// System.out.println("Sending message: " + messagePayload);
				serverIn.println(messagePayload);
				String serverMessage = serverOut.readLine();
				JSONObject json = new JSONObject(serverMessage);
				if (json.getString("command").equals("backup")) {
					debit = json.getInt("debit");
					serverMessage = serverOut.readLine();
					json = new JSONObject(serverMessage);
				}
				int amount = Integer.parseInt(parts[1]);
				if (json.getString("result").equals("SUCCESS")) {
					if (parts[0].equals("credit")) {
						debit += amount;
					} else if (parts[0].equals("debit")){
						debit -= amount;
					}
				}
				System.out.println("Status: " + json.getString("result") + " Amount: " + json.getString("amount"));
				System.out.println("my loan: " + debit);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
