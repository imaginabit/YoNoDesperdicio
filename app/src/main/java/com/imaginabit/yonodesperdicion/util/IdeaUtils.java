package com.imaginabit.yonodesperdicion.util;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.imaginabit.yonodesperdicion.model.Idea;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by fer2015julio on 21/11/15.
 */
public class IdeaUtils {
    private static final String TAG = "IdeaUtils ";
    public static List<Idea> ideas = new ArrayList<>();

    public static List<Idea> getIdeas() {
        return ideas;
    }

    public static void setIdeas(List<Idea> ideas) {
        IdeaUtils.ideas = ideas;
    }


    public static void fetchIdeas(final Activity activity, final FetchIdeasCallback callback ){
        //new DownloadIdeasTask().execute(Constants.IDEAS_API_URL);
        String TAG = "IdeaUtils fetchIdeas";

        AsyncTask<Void, Void, Void> fetchIdeasTask = new AsyncTask<Void, Void, Void>() {
            JSONObject jObj = null;
            String TAG = "IdeaUtils DownloadIdeasTask";
            public List<Idea> ideas = null;
            private Exception e = null;

            //            @Override
//            protected void onPreExecute() {
//                if (activity != null) {
//                    pd = ProgressDialog.show(activity, "", message, false, true);
//                }
//            }
            @Override
            protected Void doInBackground(Void... params) {
                String json = null;
                try {
                    json = AppUtils.downloadJsonUrl(Constants.IDEAS_API_URL);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

                // try parse the string to a JSON object
                try {
                    jObj = new JSONObject(json);
                } catch (JSONException e) {
                    Log.e(TAG + " JSON Parser", "Error parsing data " + e.toString());
                } catch (Throwable t) {
                    Log.e(TAG, "Could not parse malformed JSON: \"" + json + "\"");
                }


                try {
                    if (jObj.has("ideas")) {
                        Log.d(TAG,"has Ideas");
                        JSONArray jsonItems = null;
                        try {
                            jsonItems = jObj.getJSONArray("ideas");
                        } catch (JSONException e2) {
                            e = e2;
                            //e2.printStackTrace();
                        }
//                        Log.d(TAG, jObj.toString() );
                        Log.d(TAG,"has Ideas " + jsonItems.length());
                        if (jsonItems.length() > 0) {
                            ideas = new ArrayList<>();
                            //only the two last ideas no jsonItems.length()
                            for (int i = 0; i < 2; i++) {
                                JSONObject jsonItem = null;
                                try {
                                    jsonItem = jsonItems.getJSONObject(i);
                                } catch (JSONException e3) {
                                    e = e3;
                                    //e.printStackTrace();
                                }

                                // Extract properties
                                // title, id, category, image_url, introduction
                                //long idea_id = jsonItem.optLong("id", 0L);
                                String idea_id = jsonItem.optString("id", "");
                                String title = jsonItem.optString("title", "");
                                String category = jsonItem.optString("category", "");
                                String image_url = jsonItem.optString("image", "");
                                String introduction = jsonItem.optString("introduction", "");

                                Log.d(TAG, "add idea " + jsonItem.toString()  );

                                Log.d(TAG, "add idea id:" + idea_id + " title:" + title + " cat:" + category + " image:" + image_url + " ");
                                Log.d(TAG, "add idea id:" + idea_id + " intro: " + introduction);

                                try {
                                    if (AppUtils.isNotEmptyOrNull(title) && AppUtils.isNotEmptyOrNull(idea_id)) {
                                        Idea itemIdea = new Idea(title, idea_id, category, image_url, introduction);
                                        ideas.add(itemIdea);
                                    }
                                } catch ( Exception e ){
                                    e.printStackTrace();
                                }
                                Log.d(TAG,"added " + ideas.size() );
                            }
                        }
                    }
                } catch (Exception e4) {
                    //e.printStackTrace();
                    e = e4;
//                Log.e(TAG + " JSON Parser", "Error parsing data " + e.toString());
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
//                if (pd != null && pd.isShowing()) {
//                    pd.dismiss();
//                }

                if (e == null) {
                    callback.done(ideas, null);
                } else {
                    callback.done(null, e);
                }

            }


        };
        TasksUtils.execute(fetchIdeasTask);


    }

    /*
    private static class DownloadIdeasTask extends AsyncTask<String, Void, String> {
        JSONObject jObj = null;
        String TAG = "IdeaUtils DownloadIdeasTask";
        public List<Idea> ideas;

        @Override
        protected String doInBackground(String... urls) {
            // params comes from the execute() call: params[0] is the url.
            try {
                return AppUtils.downloadJsonUrl(urls[0]);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            // try parse the string to a JSON object
            try {
                jObj = new JSONObject(result);
            } catch (JSONException e) {
                Log.e(TAG + " JSON Parser", "Error parsing data " + e.toString());
            } catch (Throwable t) {
                Log.e(TAG, "Could not parse malformed JSON: \"" + result + "\"");
            }

            ideas = new ArrayList<Idea>();
            try {
                if (jObj.has("ideas")) {
                    Log.d(TAG,"has Ideas");
                    JSONArray jsonItems = null;
                    try {
                        jsonItems = jObj.getJSONArray("ideas");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.d(TAG,"has Ideas " + jsonItems.length() );

                    if (jsonItems.length() > 0) {
                        for (int i = 0; i < jsonItems.length(); i++) {
                            JSONObject jsonItem = null;
                            try {
                                jsonItem = jsonItems.getJSONObject(i);
                                Log.d( TAG, jsonItem.toString() );
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            // Extract properties

                            // String title,        String id,      String category,
                            //   String image_url,      String introduction
                            //long idea_id = jsonItem.optLong("id", 0L);
                            String idea_id = jsonItem.optString("id", "");
                            String title = jsonItem.optString("username", "");
                            String category = jsonItem.optString("category", "");
                            String image_url = jsonItem.optString("image_url", "");
                            String introduction = jsonItem.optString("introduction", "");

                            //Log.d(TAG, "se va a crear la idea " + title );

                            ideas.add(new Idea(title, idea_id, category, image_url, introduction));
                            Log.d(TAG, "creada idea " + ideas.toString());

                            // Validate and fill ideas array!
                            if (AppUtils.isNotEmptyOrNull(title) && AppUtils.isNotEmptyOrNull(idea_id)) {

                            }
                        }
                    }
                    IdeaUtils.setIdeas(ideas);
                    //callback()
                }
                //Toast.makeText(MoreInfo.this, result, Toast.LENGTH_SHORT).show();
                //textView.setText(result);
            } catch (Exception e) {
                e.printStackTrace();
//                Log.e(TAG + " JSON Parser", "Error parsing data " + e.toString());

            }
        }

    }

    */
    public interface FetchIdeasCallback {
        public void done(List<Idea> ideas, Exception e);
    }

    public static ArrayList<Idea> sampleData() {

        Log.d(TAG, "sampleData: Load sample data for Ideas");
        ArrayList<Idea> ideas = new ArrayList<>();
        // String title, String id, String category, String image_url, String introduction
        ideas.add(new Idea("Sopa de aprovechamiento de verduras", "10", "recetas", "/system/ideas/images/000/000/010/original/sopa_aprovechamiento_verduras.jpg", "intro"));
        ideas.add(new Idea("Hojaldre relleno de mandarinas y nata", "10", "recetas", "propias/d_brick_original.png", "intro"));
        ideas.add(new Idea("Una idea", "10", "recetas", "/system/ideas/images/000/000/001/original/croquetas-pollo.jpg?1443097172", "intro"));

        return ideas;
    }

}
