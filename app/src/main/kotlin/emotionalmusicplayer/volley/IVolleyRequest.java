package emotionalmusicplayer.volley;

import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;

public interface IVolleyRequest {
    default void getResponse(JSONObject response, String message, int resultCode) throws JSONException, ParseException {
    }

    default void onSuccess(JSONObject response) throws JSONException, ParseException {
    }

    default void onFail(VolleyError error) throws JSONException {
    }
}
