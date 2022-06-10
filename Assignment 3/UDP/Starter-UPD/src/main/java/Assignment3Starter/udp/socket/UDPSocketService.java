package Assignment3Starter.udp.socket;

import Assignment3Starter.udp.utils.UDPJsonUtils;
import Assignment3Starter.udp.utils.UDPNetworkUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class UDPSocketService implements SocketService {

    private DatagramSocket socket;
    InetAddress address = null;
    int port = 9000;


    public UDPSocketService() {
        try {
            this.socket = new DatagramSocket();
            address = InetAddress.getByName("localhost");
            socket = new DatagramSocket();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("CONNECTION NOT ESTABLISHED CHECK SERVER");
        }
    }

    public JSONObject sendRequest(JSONObject request) {
        try {
            if (request != null) {
                UDPNetworkUtils.Send(socket, address, port, UDPJsonUtils.toByteArray(request));
                UDPNetworkUtils.Tuple responseTuple = UDPNetworkUtils.Receive(socket);
                JSONObject response = UDPJsonUtils.fromByteArray(responseTuple.Payload);

                if (response.has("error")) {
                    System.out.println(response.getString("error"));
                }

                return response;
            }

        }
        catch (UnknownHostException ex) {
            ex.printStackTrace();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }

        return null;
    }
}
