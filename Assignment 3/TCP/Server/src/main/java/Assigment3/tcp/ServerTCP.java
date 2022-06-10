package Assigment3.tcp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import Assigment3.messages.MessageHandler;
import org.json.*;

public class ServerTCP {



  public static void main(String[] args) throws IOException {
    ServerSocket serv = null;
    MessageHandler messageHandler = new MessageHandler();
    System.out.println("SERVER READY TCP");
    try {
      serv = new ServerSocket(9000);
      // NOTE: SINGLE-THREADED, only one connection at a time
      while (true) {
        Socket sock = null;
        try {
          sock = serv.accept(); // blocking wait
          OutputStream out = sock.getOutputStream();
          InputStream in = sock.getInputStream();
          while (true) {
            byte[] messageBytes = NetworkUtils.Receive(in);
            JSONObject message = JsonUtils.fromByteArray(messageBytes);
            JSONObject returnMessage = messageHandler.execute(message);
            System.out.println("TCP REQUEST");
            // we are converting the JSON object we have to a byte[]
            byte[] output = JsonUtils.toByteArray(returnMessage);
            NetworkUtils.Send(out, output);
          }
        } catch (Exception e) {
          System.out.println("Client disconnect");
          e.printStackTrace();
        } finally {
          if (sock != null) {
            sock.close();
          }
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (serv != null) {
        serv.close();
      }
    }
  }
}