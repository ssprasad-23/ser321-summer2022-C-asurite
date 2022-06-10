package Assigment3.messages;

import org.json.JSONObject;
import Assigment3.quote.QuoteService;
import Assigment3.session.SessionService;

import static Assigment3.messages.MethodType.UNKNOWN;

public class MessageHandler {

    private final QuoteService quoteService;
    private final SessionService sessionService;

    public MessageHandler() {
        this.quoteService = new QuoteService();
        this.sessionService = new SessionService();
    }

    public JSONObject execute(JSONObject message) {
        if (message.has("method")) {
            MethodType method = null;
            try {
                method = MethodType.valueOf(message.get("method").toString());
            } catch (Exception e) {
                e.printStackTrace();
                method = UNKNOWN;
            }
            JSONObject result = null;
            switch (method) {
                case START_GAME: result = sessionService.createNewSession(message);
                break;
                case GET_QUOTE: result = quoteService.getNextQuote(message);
                break;
                case SUBMIT_ANSWER: result = quoteService.submit(message);
                break;
                case CHECK_SESSION: result = sessionService.checkSession(message);
                break;
                case UNKNOWN: result = error("METHOD IS UNKNOWN");
                break;
            }
            System.out.printf("REQUEST METHOD = [%s] \n RESPONSE: %s%n", method,
                    result.toString());

            return result;
        }
            return error("Invalid message received, method field must be defined");

    }

    public static JSONObject error(String err) {
        JSONObject json = new JSONObject();
        json.put("error", err);
        return json;
    }

}
