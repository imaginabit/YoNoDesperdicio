package com.imaginabit.yonodesperdicion.util;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * @author Antonio de Sousa Barroso
 */
public class HttpUtils {

    public static final String RESPONSE_CODE = "responseCode";
    public static final String RESPONSE_MESSAGE = "responseMessage";

    /**
     * POST operations:
     *
     *     (1) Url Encoded:
     *
     *     Uri.Builder builder = new Uri.Builder()
     *                                  .appendQueryParameter("email", userEmail)
     *                                  .appendQueryParameter("password", userPassword)
     *                                  .appendQueryParameter("pushid", gcmRegId);
     *
     *     String queryString builder.build().getEncodeQuery();
     *
     *     (2) JSON
     *
     *     JSONObject jsonMessage = new JSONObject();
     *     jsonMessage.put("email", userEmail)
     *                .put("password", userPassword)
     *                .put("pushid", gcmRegId);
     */
    public static JSONObject postJson(String url, String jsonMessage) throws JSONException {
        JSONObject jsonObject = null;
        String line = null;

        // Connection components
        URL requestUrl = null;
        HttpURLConnection c = null;

        OutputStream requestOutputStream = null;
        BufferedWriter requestWriter = null;
        OutputStreamWriter requestOutputWriter = null;

        BufferedReader requestReader = null;
        InputStreamReader requestInputStreamReader = null;

        try {
            requestUrl = new URL(url);
            c = (HttpURLConnection) requestUrl.openConnection();

            // Timeout milliseconds
            c.setReadTimeout(15000);
            c.setConnectTimeout(25000);
            c.setFixedLengthStreamingMode(jsonMessage.length());   // HTTP request body

            // POST
            c.setRequestMethod("POST");
            c.setDoInput(true);
            c.setDoOutput(true);

            // Make some HTTP header nicety
            c.setRequestProperty("Content-Type", "application/json;charset=utf-8");
            c.setRequestProperty("Accept", "application/json");

            // User-Agent
            c.addRequestProperty("User-Agent", "yonodesperdicio-android/1.0");

            // Open
            c.connect();

            // POST stream writer
            requestOutputStream = c.getOutputStream();
            requestOutputWriter = new OutputStreamWriter(requestOutputStream, "UTF-8");
            requestWriter = new BufferedWriter(requestOutputWriter);

            // Send POST
            requestWriter.write(jsonMessage);
            requestWriter.flush();

            // Check the response
            int responseCode = c.getResponseCode();
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                requestInputStreamReader = new InputStreamReader(c.getInputStream());
                requestReader = new BufferedReader(requestInputStreamReader);

                // Server response
                String jsonResponse = "";
                while ((line = requestReader.readLine()) != null) {
                    jsonResponse += line;
                }
Log.i("--->", "jsonResponse=" + jsonResponse);
                if (AppUtils.isEmptyOrNull(jsonResponse)) {
                    jsonObject = new JSONObject();
                    jsonObject.put(RESPONSE_CODE, responseCode);
                }
                else {
                    // Parse the Server response
                    jsonObject = new JSONObject(jsonResponse);
                    jsonObject.put(RESPONSE_CODE, responseCode);
                }
            }
            else {
                // Create a return object
                jsonObject = new JSONObject();
                jsonObject.put(RESPONSE_CODE, responseCode);
                jsonObject.put(RESPONSE_MESSAGE, c.getResponseMessage());
            }
        }
        catch (Exception e) {
            jsonObject = new JSONObject();
            jsonObject.put(RESPONSE_CODE, -1);
            jsonObject.put(RESPONSE_MESSAGE, e.getMessage());
        }
        finally {
            if (requestWriter != null) {
                try {
                    requestWriter.close();
                } catch (Exception e) {
                    // Ignored
                }
            }

            if (requestOutputStream != null) {
                try {
                    requestOutputStream.close();
                } catch (Exception e) {
                    // Ignored
                }
            }

            if (requestInputStreamReader != null) {
                try {
                    requestInputStreamReader.close();
                } catch (Exception e) {
                    // Ignored
                }
            }

            if (requestReader != null) {
                try {
                    requestReader.close();
                } catch (IOException e) {
                    // Ignored
                }
            }

            if (c != null) {
                c.disconnect();
            }

            // Marked for GC
            requestOutputStream = null;
            requestWriter = null;
            requestOutputWriter = null;
            requestReader = null;
            requestInputStreamReader = null;
            c = null;
            line = null;
        }

        return jsonObject;
    }
}
