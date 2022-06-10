package Assigment3.udp;

import java.io.IOException;
import java.net.DatagramSocket;

import Assigment3.messages.MessageHandler;
import org.json.*;

public class ServerUDP {
  /*
   * request: { "selected": <int: 1=joke, 2=quote, 3=image, 4=random>,
   * (optional)"min": <int>, (optional)"max":<int> }
   * 
   * response: {"datatype": <int: 1-string, 2-byte array>, "type": <"joke",
   * "quote", "image"> "data": <thing to return> }
   * 
   * error response: {"error": <error string> }
   */

  public static JSONObject error(String err) {
    JSONObject json = new JSONObject();
    json.put("error", err);
    return json;
  }

  public static void main(String[] args) throws IOException {
    DatagramSocket sock = null;
    MessageHandler messageHandler = new MessageHandler();
    System.out.println("SERVER READY UDP");
    try {
      sock = new DatagramSocket(9000);
      // NOTE: SINGLE-THREADED, only one connection at a time
      while (true) {
        try {
          while (true) {
            NetworkUtils.Tuple messageTuple = NetworkUtils.Receive(sock);
            JSONObject message = JsonUtils.fromByteArray(messageTuple.Payload);
            JSONObject returnMessage = messageHandler.execute(message);
            System.out.println("UDP REQUEST");
            if (message == null) {
              returnMessage = error("Invalid message received");
            }

            byte[] output = JsonUtils.toByteArray(returnMessage);
            NetworkUtils.Send(sock, messageTuple.Address, messageTuple.Port, output);
          }
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (sock != null) {
        sock.close();
      }
    }
  }
}