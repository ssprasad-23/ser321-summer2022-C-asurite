/**
  File: Server.java
  Author: Student in Fall 2020B
  Description: Server class in package taskone.
*/

package tasktwo;

import java.net.ServerSocket;
import java.net.Socket;

/**
 * Class: Server
 * Description: Server tasks.
 */
class ThreadedServer {

    public static void main(String[] args) throws Exception {
        int port;
        StringList strings = new StringList();

        if (args.length != 1) {
            // gradle runServer -Pport=9099 -q --console=plain
            System.out.println("Usage: gradle runServerTask2 -Pport=9099 -q --console=plain");
            System.exit(1);
        }
        port = -1;
        try {
            port = Integer.parseInt(args[0]);
        } catch (NumberFormatException nfe) {
            System.out.println("[Port] must be an integer");
            System.exit(2);
        }
        ServerSocket server = new ServerSocket(port);
        System.out.println("Server Started...");
        int count = 0;
        while (true) {
            System.out.println("Accepting a Request...");
            Socket sock = server.accept();
            Performer performer = new Performer(sock, strings,count++);
            performer.start();
        }
    }
}
