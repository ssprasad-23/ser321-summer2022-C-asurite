/**
  File: Performer.java
  Author: Student in Fall 2020B
  Description: Performer class in package taskone.
*/

package taskone;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;

/**
 * Class: Performer 
 * Description: Threaded Performer for server tasks.
 */
class Performer {

    private StringList state;
    private Socket conn;

    public Performer(Socket sock, StringList strings) {
        this.conn = sock;
        this.state = strings;
    }

    public JSONObject add(String str) {
        JSONObject json = new JSONObject();
        json.put("datatype", 1);
        json.put("type", "add");
        state.add(str);
        json.put("data", state.toString());
        return json;
    }

    public JSONObject pop() {
        JSONObject json = new JSONObject();
        json.put("datatype", 1);
        json.put("type", "pop");
        json.put("data", state.pop());
        return json;
    }

    public JSONObject display() {
        JSONObject json = new JSONObject();
        json.put("datatype", 1);
        json.put("type", "pop");
        json.put("data", state.display());
        return json;
    }

    public JSONObject count() {
        JSONObject json = new JSONObject();
        json.put("datatype", 1);
        json.put("type", "count");
        json.put("data", String.valueOf(state.count()));
        return json;
    }

    public JSONObject quit() {
        JSONObject json = new JSONObject();
        json.put("datatype", 1);
        json.put("type", "quit");
        json.put("data", "Closing connection");
        return json;
    }

    public JSONObject switchElements(String input) {
        String parsed[] = input.split(" ");
        String returnVal;

        if(parsed.length == 2) {
            returnVal = state.switchElements(Integer.parseInt(parsed[0]),Integer.parseInt(parsed[1]));
        }
        else {
            System.out.println("switch: need to define exactly two index");
            returnVal = null;
        }

        JSONObject json = new JSONObject();
        json.put("datatype", 2);
        json.put("type", "switch");
        json.put("data", returnVal);
        return json;
    }



    public static JSONObject error(String err) {
        JSONObject json = new JSONObject();
        json.put("error", err);
        return json;
    }

    public void doPerform() {
        boolean quit = false;
        OutputStream out = null;
        InputStream in = null;
        try {
            out = conn.getOutputStream();
            in = conn.getInputStream();
            System.out.println("Server connected to client:");
            while (!quit) {
                byte[] messageBytes = NetworkUtils.receive(in);
                JSONObject message = JsonUtils.fromByteArray(messageBytes);
                JSONObject returnMessage = new JSONObject();
   
                int choice = message.getInt("selected");
                    switch (choice) {
                        case (1):
                            String inStr = (String) message.get("data");
                            returnMessage = add(inStr);
                            break;
                        case (2):
                            returnMessage = pop();
                            break;
                        case (3):
                            returnMessage = display();
                            break;
                        case (4):
                            returnMessage = count();
                            break;
                        case (5):
                            String input = (String) message.get("data");
                            returnMessage = switchElements(input);
                            break;
                        case (0):
                            returnMessage = quit();
                            break;
                        default:
                            returnMessage = error("Invalid selection: " + choice 
                                    + " is not an option");
                            break;
                    }
                // we are converting the JSON object we have to a byte[]
                byte[] output = JsonUtils.toByteArray(returnMessage);
                NetworkUtils.send(out, output);
            }
            // close the resource
            System.out.println("close the resources of client ");
            out.close();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
