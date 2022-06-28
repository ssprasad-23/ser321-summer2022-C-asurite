package distributed;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.function.BiConsumer;

import org.json.JSONObject;

public abstract class NodeThread extends Thread {
	private Socket socket;
	private BufferedReader bufferedReader;
	public String id;
	public String type;
	private int debit = 0;
	private HashMap<String, Integer> credit = new HashMap<String, Integer>();
	public boolean keepAlive;

	public void editDebit(int amount) {
		debit += amount;
	}

	public int getDebit() {
		return debit;
	}

	public void editCredit(String id, int amount) {
		credit.putIfAbsent(id, 0);
		credit.put(id, credit.get(id) + amount);
	}

	public int getCredit(String id) {
		Integer amount = credit.get(id);
		return amount == null ? 0 : amount;
	}

	public String getBackupData() {
		JSONObject json = new JSONObject();
		json.put("command", "backup");
		json.put("id", id);
		json.put("type", type);
		json.put("debit", debit);
		JSONObject jsonCredit = new JSONObject(credit);
		json.put("credit", jsonCredit);
		return json.toString();
	}

	public void setBackupData(String data) {
		sendMessage(data);
		JSONObject json = new JSONObject(data);
		debit = json.getInt("debit");
		credit.clear();
		json.getJSONObject("credit").toMap().forEach(new BiConsumer<String, Object>(){
			@Override
			public void accept(String arg0, Object arg1) {
				credit.put(arg0, (Integer)arg1);
			}
		});
	}
	
	public NodeThread(Socket socket) throws IOException {
		this.socket = socket;
		bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		JSONObject json = new JSONObject(bufferedReader.readLine());
		id = json.getString("id");
		type = json.getString("type");
		keepAlive = type.equals("client");
	}

	abstract public void newMessage(String message);
	abstract public void onDisconnect();
	
	public void run() {
		while (keepAlive) {
			try {
				String message = bufferedReader.readLine();
				newMessage(message);
			} catch (IOException e) {
				onDisconnect();
				interrupt();
				break;
			}
		}
	}
	
	public String sendMessage(String message) {
		try {
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			out.println(message);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (!keepAlive) {
			try {
				String response = bufferedReader.readLine();
				return response;
			} catch (IOException e) {
				e.printStackTrace();
				onDisconnect();
			}
		}
		return null;
	}
}
