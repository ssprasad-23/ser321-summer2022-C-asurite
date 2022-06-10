package Assigment3.quote;

import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.*;

public class QuoteService {

    //Key character name id, Object - submitted quota
    private final Map<String, List<Quote>> resultMap = new HashMap<>();
    //Key character name, value quote object
    private final Map<String,Quote> quoteMap = new HashMap<>();

    public QuoteService() {
        //Add another line to add new records
        //Key and quote.answer must be the same
        quoteMap.put("Brad Pitt" ,new Quote(1L, "Brad Pitt", "img/fight_club.jpg"));
        quoteMap.put("Dambledore" ,new Quote(2L, "Dambledore", "img/dambldor.jpg"));
        quoteMap.put("Ras Al Ghul" ,new Quote(3L, "Ras Al Ghul", "img/rasalgul.jpg"));
    }

    public JSONObject getNextQuote(JSONObject message) {
        Random newRandom = new Random();
        int randomIndex = newRandom.ints(0, quoteMap.size())
                .findFirst()
                .getAsInt();

        Quote quote = new ArrayList<>(quoteMap.values())
                .get(randomIndex);

        return toJsonObject(quote);
    }

    private JSONObject toJsonObject(Quote quote) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("img",imageBase64(quote.getPathToFile()));
        jsonObject.put("id", quote.getId());

        return jsonObject;
    }


    public static String imageBase64(String filePath) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                System.err.println("Cannot find file: " + file.getAbsolutePath());
                return null;
            }
            // Read in image
            BufferedImage img = ImageIO.read(file);
            byte[] bytes = null;
            try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                ImageIO.write(img, "png", out);
                bytes = out.toByteArray();
            }
            if (bytes != null) {
                Base64.Encoder encoder = Base64.getEncoder();
                return encoder.encodeToString(bytes);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public JSONObject submit(JSONObject message) {
        Long quoteId = message.getLong("quote_id");
        String answer = message.getString("answer");
        String sessionId = message.getString("session_id");

        Quote quote = null;
        for(Quote qu :quoteMap.values()) {
            if(qu.getId().equals(quoteId)) {
               quote = qu;
            }
        }

        boolean isCorrect = quote.getAnswer().equalsIgnoreCase(answer);
        List<Quote> answered = resultMap.getOrDefault(sessionId, new ArrayList<>());

        if(isCorrect) {
            answered.add(quote);
            resultMap.put(sessionId, answered);

            JSONObject result =  new JSONObject()
                    .put("is_correct", true)
                    .put("total_score", getUserScore(sessionId));

            if(answered.size() >= 3) {
                result.put("is_won", true)
                        .put("image", imageBase64("img/won.png"));
            }

            return result;
        }

        return new JSONObject().put("is_correct", false)
                    .put("total_score", getUserScore(sessionId));


    }

    private int getUserScore(String sessionId) {
        return resultMap.getOrDefault(sessionId,new ArrayList<>()).size() * 10;
    }
}
