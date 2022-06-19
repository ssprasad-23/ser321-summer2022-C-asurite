/**
  File: JsonUtils.java
  Author: Student in Fall 2020B
  Description: JsonUtils class in package taskone.
*/

package taskthree;

import org.json.JSONObject;

import static taskthree.Performer.error;

/**
 * Class: JsonUtils
 * Description: Json Utilities.
 */
public class JsonUtils {
    public static JSONObject fromByteArray(byte[] bytes) {
        String jsonString = new String(bytes);
        System.out.println(jsonString);
        try {
            return new JSONObject(jsonString);
        } catch (Exception e) {
            e.printStackTrace();
            return error("Some error has accured while parsing json");
        }
    }

    public static byte[] toByteArray(JSONObject object) {
        return object.toString().getBytes();
    }
}
