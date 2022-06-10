package Assigment3.session;

import Assigment3.messages.MessageHandler;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static Assigment3.quote.QuoteService.imageBase64;

public class SessionService {

    private static String sessionIdField = "session_id";
    //Session Map
    //Key is generated random string
    //Value username
    private final Map<String, SessionInfo> sessionMap = new HashMap<>();

    public JSONObject checkSession(JSONObject jsonObject) {
        String sessionId = jsonObject.getString(sessionIdField);
        SessionInfo info = sessionMap.get(sessionId);
        JSONObject result = new JSONObject();
        LocalDateTime now = LocalDateTime.now();
        if(info.getExpireDate().isAfter(now)) {
            result.put("is_expired", false);
            result.put("seconds_remaining", ChronoUnit.SECONDS.between(now,info.getExpireDate()));
        } else {
            result.put("is_expired", true);
            result.put("seconds_remaining", ChronoUnit.SECONDS.between(now,info.getExpireDate()));
            result.put("image", imageBase64("img/fail.gif"));
        }

        return result;
    }

    public JSONObject createNewSession(JSONObject message) {
        String username = (String) message.get("user_name");

        if(username == null || username.isEmpty()) {
            return MessageHandler.error("Username must be not null");
        }

        String sessionId = UUID.randomUUID().toString();
        sessionMap.put(sessionId, new SessionInfo(LocalDateTime.now().plusSeconds(60),
                username));

        return new JSONObject()
                .put(sessionIdField, sessionId)
                .put("user_name", username);
    }

}
