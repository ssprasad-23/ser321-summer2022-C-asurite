package Assignment3Starter.udp.socket;

import Assignment3Starter.udp.utils.TCPJsonUtils;
import Assignment3Starter.udp.utils.TCPNetworkUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class TCPSocketService implements SocketService {

    private final Socket socket;

    public TCPSocketService() {
        try {
            this.socket = new Socket("localhost", 9000);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("CONNECTION NOT ESTABLISHED CHECK SERVER");
        }
    }

    public JSONObject sendRequest(JSONObject request) {
        try {
            OutputStream out = socket.getOutputStream();
            InputStream in = socket.getInputStream();
            if (request != null) {
                    TCPNetworkUtils.Send(out, TCPJsonUtils.toByteArray(request));
                    byte[] responseBytes = TCPNetworkUtils.Receive(in);
                    JSONObject response = TCPJsonUtils.fromByteArray(responseBytes);
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
