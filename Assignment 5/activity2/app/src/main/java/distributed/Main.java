package distributed;

import java.io.IOException;

public class Main {

    public void startLeaderNode(int port) {
        try {
            new ServerNode(port).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startClientNode(String id, String host, int port) {
        try {
            new ClientNode(host, port, id).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startBankNode(String id, int amount, String host, int port) {
        try {
            new BankNode(host, port, id, amount).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        if (args[0].equals("error")) {
            System.out.println("type should be provided");
            System.exit(0);
        }
        if (args.length != 5) {
            System.out.println("Usage: args... <type> <id> <amount>");
            return;
        } else if (args[0].equals("leader")) {
            new Main().startLeaderNode(Integer.parseInt(args[4]));
        } else if (args[0].equals("client")) {
            new Main().startClientNode(args[1], args[3], Integer.parseInt(args[4]));
        } else if (args[0].equals("bank")) {
            new Main().startBankNode(args[1], Integer.parseInt(args[2]), args[3], Integer.parseInt(args[4]));
        }
    }
}
