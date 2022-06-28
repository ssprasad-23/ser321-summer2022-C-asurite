package distributed;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.json.JSONObject;

public class ServerNode extends Thread {
	private Set<NodeThread> clients = new HashSet<NodeThread>();
	private Set<NodeThread> banks = new HashSet<NodeThread>();
	private ArrayList<String> messageQueue = new ArrayList<String>();
	private HashMap<String, String> backup = new HashMap<String, String>();
	
	public ServerNode(int portNum) throws IOException {
		new ServerThread(portNum) {

			@Override
			public void onNewClient(NodeThread clientNode) {
				System.out.println("{id: " + clientNode.id + ", type: " + clientNode.type + "} connected");
				if (backup.containsKey(clientNode.id)) {
					clientNode.setBackupData(backup.get(clientNode.id));
				}
				if (clientNode.type.equals("bank")) {
					banks.add(clientNode);
				} else {
					clients.add(clientNode);
				}
			}

			public void onClientDisconnect(NodeThread clientNode) {
				backup.put(clientNode.id, clientNode.getBackupData());
				clients.remove(clientNode);
			};

			@Override
			public void onMessage(String message) {
				synchronized(messageQueue) {
					// System.out.println("New message from client: " + message);
					messageQueue.add(message);
				}
			}
			
		}.start();
	}

	private NodeThread getClient(String id) {
		for (NodeThread c : clients) {
			if (c.id.equals(id)) return c;
		}
		return null;
	}

	private void handleMessage(JSONObject json) {
		String type = json.getString("type");
		if (type.equals("request")) {
			String command = json.getString("command");
			Request request = new Request(json);
			NodeThread client = getClient(request.id);
			if (command.equals("credit")) {
				json.put("command", "request");
				for (NodeThread bank : banks) {
					JSONObject response = new JSONObject(bank.sendMessage(json.toString()));
					String value = response.getString("value");
					if (value.equals("yes")) {
						request.loanNodes.add(bank);
					}
				}
				request.getLoan(client);
			} else {
				if (request.amount <= client.getDebit()) {
					for (NodeThread bank : banks) {
						int amount = bank.getCredit(request.id);
						if (amount > 0) {
							request.loanNodes.add(bank);
						}
					}
				}
				request.payBack(client);
			}
		} else {
			System.out.println("Unknown: " + json);
		}
	}
	
	public void run() {
		while(true) {
			synchronized(messageQueue) {
				if(messageQueue.size() > 0) {
					String message = messageQueue.remove(0);
					JSONObject json = new JSONObject(message);
					handleMessage(json);
				}
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}

class Request {
	public String id;
	public int amount;
	public JSONObject json;
	public ArrayList<NodeThread> loanNodes = new ArrayList<NodeThread>();

	public Request(JSONObject json) {
		this.json = json;
		this.id = json.getString("id");
		this.amount = Integer.parseInt(json.getString("amount"));
	}

	public void getLoan(NodeThread client) {
		JSONObject payload = new JSONObject();
		payload.put("command", "result");
		payload.put("result", "SUCCESS");
		if (!loanNodes.isEmpty()) {
			int partedAmount = amount / loanNodes.size();
			json.put("command", "credit");
			json.put("amount", "" + partedAmount);
			for (NodeThread bank : loanNodes) {
				JSONObject response = new JSONObject(bank.sendMessage(json.toString()));
				if (!response.getString("value").equals("done")) {
					payload.put("result", "FAILURE");
					break;
				}
			}
			client.editDebit(amount);
			for (NodeThread bank : loanNodes) {
				bank.editCredit(id, partedAmount);
			}
		} else {
			payload.put("result", "FAILURE");
		}
		payload.put("amount", "" + client.getDebit());
		client.sendMessage(payload.toString());
	}

	public void payBack(NodeThread client) {
		JSONObject payload = new JSONObject();
		payload.put("command", "result");
		payload.put("result", "SUCCESS");
		int extraAmount = 0;
		Map<NodeThread, Integer> banks = new HashMap<NodeThread, Integer>();
		if (!loanNodes.isEmpty()) {
			int partedAmount = amount / loanNodes.size();
			json.put("command", "debit");
			for (NodeThread bank : loanNodes) {
				int loan = bank.getCredit(id);
				int pay = partedAmount;
				if (loan < partedAmount) {
					pay = loan;
					extraAmount += partedAmount - loan;
				} else if (extraAmount > 0) {
					int extraPayable = loan - partedAmount;
					if (extraAmount > extraPayable) {
						pay += extraPayable;
						extraAmount -= extraPayable;
					} else {
						pay += extraAmount;
						extraAmount = 0;
					}
				}
				json.put("amount", "" + pay);
				JSONObject response = new JSONObject(bank.sendMessage(json.toString()));
				if (!response.getString("value").equals("done")) {
					payload.put("result", "FAILURE");
					break;
				}
				banks.put(bank, -pay);
			}
			client.editDebit(-amount);
			for (Entry<NodeThread, Integer> entry : banks.entrySet()) {
				entry.getKey().editCredit(id, entry.getValue());
			}
		} else {
			payload.put("result", "FAILURE");
		}
		payload.put("amount", "" + client.getDebit());
		client.sendMessage(payload.toString());
	}
}