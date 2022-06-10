package Assignment3Starter.udp.services;

import Assignment3Starter.udp.models.SessionCheckResponse;
import Assignment3Starter.udp.socket.SocketService;
import org.json.JSONObject;

import java.util.Base64;

public class SessionService {

    private final SocketService socketService;
    private String currentSessionId;
    private String currentUserId;

    public SessionService(SocketService socketService) {
        this.socketService = socketService;
    }

    public SessionCheckResponse checkCurrentSession() {
        JSONObject request = new JSONObject();
        request.put("session_id", this.currentSessionId);
        request.put("method", "CHECK_SESSION");

        JSONObject response = this.socketService.sendRequest(request);
        byte image[] = null;
        if(response.has("image")) {
            image = Base64.getDecoder().decode(response.getString("image"));
        }

        return new SessionCheckResponse(response.getBoolean("is_expired"),image,response.getLong("seconds_remaining"));
    }

    public void createSession(String name) {
        JSONObject request = new JSONObject();
        request.put("user_name", name);
        request.put("method", "START_GAME");

        JSONObject response = this.socketService.sendRequest(request);
        this.currentSessionId = response.getString("session_id");
        this.currentUserId = response.getString("user_name");
    }

    public String getCurrentSessionId() {
        return currentSessionId;
    }

    public String getCurrentUserId() {
        return currentUserId;
    }
}
