//package com.imaginabit.yonodesperdicion.helpers;
//
//import android.app.ProgressDialog;
//import android.content.Context;
//import android.util.Log;
//
//import com.android.volley.Request;
//import com.android.volley.RequestQueue;
//import com.android.volley.Response;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.JsonObjectRequest;
//import com.android.volley.toolbox.Volley;
//import com.imaginabit.yonodesperdicion.Constants;
//import com.imaginabit.yonodesperdicion.R;
//import com.imaginabit.yonodesperdicion.data.UserData;
//import com.imaginabit.yonodesperdicion.models.Ad;
//import com.imaginabit.yonodesperdicion.models.User;
//import com.imaginabit.yonodesperdicion.utils.Utils;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
///**
// * Enviar datos de los anucios
// */
//public class AdsHelper {
//
//    /**
//     * Create new ad , send to api
//     */
//   public static void create(final Context context,final Ad ad ){
//
//       // Show message
//       final ProgressDialog pd = ProgressDialog.show(context, "", context.getString(R.string.ad_create_message));
//
//       // Json request
//       try {
//           JSONObject jsonAd = new JSONObject().put("title", ad.getTitle())
//                   .put("body", ad.getBody())
//                   .put("grams", ad.getWeightGrams())
//                   .put("status", 1)
//                   .put("food_category", "")
//                   //.put("province", province) //TODO calcular provincia por cp
//                   .put("zipcode", ad.getPostalCode())
//                   ;
//
//           JSONObject jsonRequest = new JSONObject().put("ad", jsonAd);
//
//           // Request queue
//           RequestQueue requestQueue = Volley.newRequestQueue(context);
//
//
//           JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
//                   Request.Method.POST,
//                   Constants.ADS_API_URL,
//                   jsonRequest,
//                   new Response.Listener<JSONObject>() {
//                       @Override
//                       public void onResponse(JSONObject jsonResponse) {
//                           Utils.dismissProgressDialog(pd);
//                           Log.i("--->", "create:" + jsonResponse.toString());
//                           // Created user
//                           UserData user = extractUserData(jsonResponse);
//                           user.fullname = name;
//                           user.username = username;
//                           user.password = password;
//                           user.email = email;
//                           user.city = city;
//                           user.province = province;
//                           user.zipCode = zipCode;
//
//                           callback.onFinished(user);
//                       }
//                   },
//                   new Response.ErrorListener() {
//                       @Override
//                       public void onErrorResponse(VolleyError error) {
//                           Utils.dismissProgressDialog(pd);
//                           callback.onError(extractErrorMessage(context, error));
//                       }
//                   }
//           ){
//               JSONObject params = new JSONObject();
//               String token = "";
//               //Custom header:
//               params.put("Authorization", token);
//               params.
//
//                Request
//           };
//       } catch (JSONException e) {
//           e.printStackTrace();
//       }
//
//
//
//
//
//
//
//
//   }
//
//
//
//
//}
