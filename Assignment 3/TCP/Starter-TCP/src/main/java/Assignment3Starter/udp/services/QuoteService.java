package Assignment3Starter.udp.services;

import Assignment3Starter.udp.models.QuoteResult;
import Assignment3Starter.udp.models.QuoteSubmitResult;
import Assignment3Starter.udp.socket.SocketService;
import org.json.JSONObject;

import java.util.Base64;

public class QuoteService {

    private final SocketService socketService;
    private final SessionService sessionService;

    public QuoteService(SocketService socketService, SessionService sessionService) {
        this.socketService = socketService;
        this.sessionService = sessionService;
    }

    public QuoteResult getNextQuote() {
        JSONObject request = new JSONObject();
        request.put("method", "GET_QUOTE");

        JSONObject response = this.socketService.sendRequest(request);

        byte[] bytes = Base64.getDecoder().decode(response.getString("img"));
        Long id = response.getLong("id");

        return new QuoteResult(id, bytes);
    }

    public QuoteSubmitResult submit(Long id, String input) {

        JSONObject request = new JSONObject();
        request.put("quote_id", id);
        request.put("answer", input);
        request.put("session_id", sessionService.getCurrentSessionId());
        request.put("method","SUBMIT_ANSWER");

        JSONObject response = this.socketService.sendRequest(request);

        boolean isWon = response.has("is_won") ? response.getBoolean("is_won") : false;
        byte[] image = null;
        if(isWon) {
            image = Base64.getDecoder().decode(response.getString("image"));
        }

        return new QuoteSubmitResult(response.getBoolean("is_correct"), response.getInt("total_score"), isWon, image);
    }
}
