package com.eltrio.emotionalmusicplayer.volley;

import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.eltrio.emotionalmusicplayer.app.MyApp;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.Map;
import java.util.Objects;

public class VolleyRequest {

    private static final int MY_VOLLEY_TIME_OUT = 0;
    private static VolleyRequest volleyRequest;

    public static final int RESULT_SUCCESS = 1, RESULT_FAILED = 0;

    private static RequestQueue mRequestQueue;

    static {
        mRequestQueue = Volley.newRequestQueue(Objects.requireNonNull(MyApp.Companion.getInstance()));
    }

    private VolleyRequest() {
    }

    public static synchronized VolleyRequest getInstance() {
        try {
            if (volleyRequest == null)
                return volleyRequest = new VolleyRequest();
            else
                return volleyRequest;
        } catch (Exception e) {
            throw new IllegalArgumentException();
        }
    }

    public void post(String URL, Map<String, String> map, IVolleyRequest iVolleyRequest) {

        if (URL.isEmpty()) return;

        Log.e(URL, map.toString());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, response -> {

            Log.e("response:done", response);
            try {
                iVolleyRequest.getResponse(new JSONObject(response), null, RESULT_SUCCESS);
                iVolleyRequest.onSuccess(new JSONObject(response));
            } catch (JSONException | ParseException e) {
                e.printStackTrace();
            }

        }, error -> {
            Log.e("response:error", String.valueOf(error));
            try {
                iVolleyRequest.getResponse(null, error.getMessage(), RESULT_FAILED);
                iVolleyRequest.onFail(error);
            } catch (JSONException | ParseException e) {
                e.printStackTrace();
            }
        }) {
            protected Map<String, String> getParams() {
                return map;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                MY_VOLLEY_TIME_OUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        stringRequest.setShouldCache(false);

        mRequestQueue.add(stringRequest);
    }

}
