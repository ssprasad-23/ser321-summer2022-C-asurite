package distributed;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.function.BiConsumer;

import org.json.JSONObject;

public class BankNode extends Thread {
	private Socket socket;
	private PrintWriter serverIn;
	private BufferedReader serverOut;
	private String id;
	private int amount;
	private HashMap<String, Integer> balances = new HashMap<String, Integer>();

	public BankNode(String host, int portNum, String id, int amount) throws IOException {
		this.id = id;
		this.amount = amount;
		socket = new Socket(host, portNum);
		serverOut = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		serverIn = new PrintWriter(socket.getOutputStream(), true);
		serverIn.println("{'type':'bank', 'id':'" + id + "'}");
	}

	private void handleMessage(JSONObject json) {
		JSONObject response = new JSONObject();
		response.put("type", "response");
		String clientId = json.getString("id");
		response.put("client_id", clientId);
		response.put("id", id);
		String commad = json.getString("command");
		if (commad.equals("request")) {
			int requestAmount = (int) (json.getInt("amount") * 1.5);
			if (requestAmount <= amount) {
				response.put("value", "yes");
			} else {
				response.put("value", "no");
			}
		} else if (commad.equals("credit")) {
			int amount = json.getInt("amount");
			Integer oldBalance = balances.get(clientId);
			int old = oldBalance == null ? 0 : oldBalance;
			balances.put(clientId, old + amount);
			response.put("value", "done");
			this.amount -= amount;
		} else if (commad.equals("debit")) {
			int amount = json.getInt("amount");
			Integer oldBalance = balances.get(clientId);
			int old = oldBalance == null ? 0 : oldBalance;
			balances.put(clientId, old - amount);
			response.put("value", "done");
			this.amount += amount;
		} else if (commad.equals("report")) {
			System.out.println("Balance of " + clientId + ": " + balances.get(clientId));
		} else if (commad.equals("backup")) {
			json.getJSONObject("credit").toMap().forEach(new BiConsumer<String, Object>(){
				@Override
				public void accept(String arg0, Object arg1) {
					balances.put(arg0, (Integer)arg1);
					amount -= (int)arg1;
				}
			});
			response.put("value", "done");
		} else {
			response.put("value", "error");
		}
		serverIn.println(response.toString());
		if (!commad.equals("request")) {
			System.out.println("Amount left: " + amount);
			System.out.println("Loans: " + balances);
		}
	}

	public void run() {
		while (true) {
			try {
				String serverMessage = serverOut.readLine();
				// System.out.println("BankNode received message: " + serverMessage);
				JSONObject json = new JSONObject(serverMessage);
				handleMessage(json);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
