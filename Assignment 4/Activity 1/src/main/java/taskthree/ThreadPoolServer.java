/**
  File: Server.java
  Author: Student in Fall 2020B
  Description: Server class in package taskone.
*/

package taskthree;

import java.net.ServerSocket;
import java.net.Socket;

/**
 * Class: Server
 * Description: Server tasks.
 */
class ThreadPoolServer {

    public static void main(String[] args) throws Exception {
        int port;
        int threadCount;
        StringList strings = new StringList();

        if (args.length != 2) {
            // gradle runServerTask3 -Pport=9099 -Pthread=10 -q --console=plain
            System.out.println("Usage: gradle runServerTask3 -Pport=9099 -Pthread=10 -q --console=plain");
            System.exit(1);
        }
        port = -1;
        try {
            port = Integer.parseInt(args[0]);
        } catch (NumberFormatException nfe) {
            System.out.println("[Port] must be an integer");
            System.exit(2);
        }
        threadCount = -1;
        try {
            threadCount = Integer.parseInt(args[1]);
        } catch (NumberFormatException nfe) {
            System.out.println("[Thread] must be an integer");
            System.exit(2);
        }


        ServerSocket server = new ServerSocket(port);
        System.out.println(String.format("Server Started... Thread pool size = %s", threadCount));
        int count = 0;
        while (true) {
            System.out.println("Accepting a Request...");
            Socket sock = server.accept();
            Performer performer = new Performer(sock, strings,count++);

            if(count+1 > threadCount) {
                System.out.println("Number of threads exhausted, cannot accept connection");
                performer.closeConnection(sock.getOutputStream(),sock.getInputStream());
                continue;
            }

            performer.start();
        }
    }
}
