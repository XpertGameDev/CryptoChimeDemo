package com.example.multilayoutdemo;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NotificationBroadcast extends BroadcastReceiver {

    static DecimalFormat df2 = new DecimalFormat("#.##");

    String mySymbol;
    float userPU, userPD;

    int isNotified;

    String notifyTitle, notifyText;

    ArrayList<CurrencyRVModal> rvModelArrayList2 = new ArrayList<>();

    SharedPreferences prefSymbol, prefPU, prefPD, prefNotify;


    //MainActivity mainActivity = new MainActivity();
    @Override
    public void onReceive(Context context, Intent intent) {


         prefSymbol = context.getSharedPreferences("mySymbol", Context.MODE_PRIVATE);
         prefPU = context.getSharedPreferences("userPU", Context.MODE_PRIVATE);
         prefPD = context.getSharedPreferences("userPD", Context.MODE_PRIVATE);
         prefNotify = context.getSharedPreferences("isNotified", Context.MODE_PRIVATE);

        mySymbol = prefSymbol.getString("mySymbol", null);
        userPU = prefPU.getFloat("userPU", 0);
        userPD = prefPD.getFloat("userPD", 0);
        isNotified = prefNotify.getInt("isNotified", 2);


        //if Not yet notified
        // Check for price change
        //if Market Price percentage changed in expected value
        //Show Notification

        Log.i("is Notified", String.valueOf(isNotified));

        if(isNotified == 0){
            String url = "https://pro-api.coinmarketcap.com/v1/cryptocurrency/listings/latest";

            RequestQueue requestQueue = Volley.newRequestQueue(context);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @SuppressLint("CommitPrefEdits")
                @Override
                public void onResponse(JSONObject response) {


                    try {
                        JSONArray dataArray = response.getJSONArray("data");

                        for (int i=0; i<dataArray.length(); i++){
                            JSONObject dataObj = dataArray.getJSONObject(i);
                            String symbol = dataObj.getString("symbol");
                            String name = dataObj.getString("name");

                            JSONObject quote = dataObj.getJSONObject("quote");
                            JSONObject USD = quote.getJSONObject("USD");

                            double price = USD.getDouble("price");
                            double pc24h = USD.getDouble("percent_change_24h");

                            float pc24hf = (float)pc24h;

                            if (symbol.equals(mySymbol)){

                                if (  pc24hf > userPU) {
                                    notifyTitle = symbol +" Price is Up | Last 24h Change: "+ df2.format(pc24hf)+ "%";
                                    notifyText = name + " Current price is : "+df2.format(price);
                                    showNotification(context, notifyTitle, notifyText);

                                    isNotified = 1;
                                    prefNotify.edit().putInt("isNotified", isNotified).apply();
                                    isNotified = prefNotify.getInt("isNotified", isNotified);
                                    Log.i("isNotified", String.valueOf(isNotified));

                                    //Toast.makeText(context, "Price is Up.", Toast.LENGTH_SHORT).show();
                                } else if (pc24hf < userPD){
                                    notifyTitle = symbol +" Price is Down | Last 24h Change: "+ df2.format(pc24hf)+ "%";
                                    notifyText = name + " Current price is : "+df2.format(price);
                                    showNotification(context, notifyTitle, notifyText);

                                    isNotified = 1;
                                    prefNotify.edit().putInt("isNotified", isNotified).apply();
                                    isNotified = prefNotify.getInt("isNotified", isNotified);
                                    Log.i("isNotified", String.valueOf(isNotified));

                                    //Toast.makeText(context, "Price is Down", Toast.LENGTH_SHORT).show();
                                }

                            }

                            rvModelArrayList2.add(new CurrencyRVModal(symbol, name, price, pc24h));
                        }

                    } catch (JSONException e) {

                        e.printStackTrace();
                        Toast.makeText(context, "Fail to extract json data...", Toast.LENGTH_SHORT).show();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Toast.makeText(context, "Fail to get the data", Toast.LENGTH_SHORT).show();

                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {

                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("X-CMC_PRO_API_KEY", "cf22e625-7f13-4277-857c-6c60021e50dd");
                    return headers;
                }
            };
            requestQueue.add(jsonObjectRequest);
        }


    }

    //Show Notification
    private void showNotification(Context context, String titleNotify, String textNotify){
        Notification notification = new NotificationCompat.Builder(context, NotifyApp.CHANNEL_1_ID)
                .setSmallIcon(R.drawable.ic_dollar)
                .setContentTitle(titleNotify)
                .setContentText(textNotify)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .build();


        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(1, notification);
    }



}
