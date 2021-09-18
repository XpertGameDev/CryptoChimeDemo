package com.example.multilayoutdemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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

public class MainActivity extends AppCompatActivity {

    //Declare RecyclerView
    RecyclerView currenciesRV;
    //Declare Adapter
    CurrencyRVAdapter currencyRVAdapter;
    //Declare data which I want to show in RV
    public ArrayList<CurrencyRVModal> currencyRVModalArrayList;
    //Initialize Bottom Layout variable
    Button priceAlert, home;
    //Declare Alert Layout
    RelativeLayout alert_layout;
    //Botton layout Buttons
    Button priceAlertButton, homeButton;


    // Variable for alertView Layout
    TextView spinningTextView;
    Dialog dialog;
    ArrayList<String> coinNameList;
    Button setAlertButton;
    public String selectedSymbol;
    EditText puEditText, pdEditText;

    TextView alertRLSymbolTextView, alertRLNameTextView, alertRLPriceTextView, alertRL24PercentTextView;

    NotificationManagerCompat notificationManager;

    static DecimalFormat df2 = new DecimalFormat("#.##");

    Double userInputPU, pc24h;

    ProgressBar loadingPB;

    //public SharedPreferences sharedPreferences;

    SharedPreferences pref, prefPU, prefPD, prefNotify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pref = getApplicationContext().getSharedPreferences("mySymbol", MODE_PRIVATE);
        prefPU = getApplicationContext().getSharedPreferences("userPU", MODE_PRIVATE);
        prefPD = getApplicationContext().getSharedPreferences("userPD", MODE_PRIVATE);
        prefNotify = getApplicationContext().getSharedPreferences("isNotified", MODE_PRIVATE);





        alert_layout = findViewById(R.id.alertLayout);
        priceAlertButton = findViewById(R.id.priceAlertButton);
        homeButton = findViewById(R.id.homeButton);


        //Assign alertView Variables
        spinningTextView = findViewById(R.id.spinningTextViewId);
        coinNameList = new ArrayList<String>();
        setAlertButton = findViewById(R.id.setAlertButton);
        puEditText = findViewById(R.id.puEdtTxt);



        pdEditText = findViewById(R.id.pdEdtTxt);


        alertRLSymbolTextView = findViewById(R.id.alertRLSymbolTextView);
        alertRLNameTextView = findViewById(R.id.alertRLNameTextView);
        alertRLPriceTextView = findViewById(R.id.alerRLPriceTextView);
        alertRL24PercentTextView = findViewById(R.id.alertRL24PercentTextView);

        notificationManager = NotificationManagerCompat.from(this);



        spinningTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Initialize dialog
                dialog = new Dialog(MainActivity.this);
                //Set custom dialog
                dialog.setContentView(R.layout.dialog_searchable_spinner);
                //Set custom height and width
                dialog.getWindow().setLayout(650, 800);
                //Set tranperant background
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                //Show dialog
                dialog.show();

                //Initialize and assign variable
                EditText editText = dialog.findViewById(R.id.searchEditText);
                ListView listView = dialog.findViewById(R.id.searchListView);


                //Initialize Array adapter for spinning list
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, coinNameList);


                //Set Adapter to spinning list
                listView.setAdapter(adapter);

                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        //Filter array list
                        adapter.getFilter().filter(charSequence);

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {



                    }
                });

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        //When item selected from list
                        //Set selected item on text view
                        spinningTextView.setText(adapter.getItem(i));

                        selectedSymbol = adapter.getItem(i);

                        pref.edit().putString("mySymbol", selectedSymbol).apply();

                        //Show Info in Infor RL
                        for (CurrencyRVModal element : currencyRVModalArrayList) {

                            if (element.symbol == selectedSymbol) {

                                alertRLSymbolTextView.setText(element.symbol);
                                alertRLNameTextView.setText(element.name);
                                alertRLPriceTextView.setText("$"+ df2.format(element.price));
                                alertRL24PercentTextView.setText(df2.format(element.pc24h)+"%");


                            }
                        }


                        //Dismiss dialog
                        dialog.dismiss();
                    }
                });
            }
        });



        getRVData();

        getCurrencyData();


    }

    public void homeLayoutButton(View view){
        alert_layout.setVisibility(View.INVISIBLE);
        homeButton.setVisibility(View.INVISIBLE);
        currenciesRV.setVisibility(View.VISIBLE);
        priceAlertButton.setVisibility(View.VISIBLE);


        getRVData();
        getCurrencyData();
    }

    public void alertLayoutButton(View view){
        currenciesRV.setVisibility(View.INVISIBLE);
        priceAlertButton.setVisibility(View.INVISIBLE);
        alert_layout.setVisibility(View.VISIBLE);
        homeButton.setVisibility(View.VISIBLE);
    }

//    private void pecentEditTextFromet(EditText editText){
//        editText.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                editText.setText((editText.getText().toString()+"%"));
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//
//            }
//        });
//    }



    public void setAlertButton(View view){
        //String notifyTitle = "It's time to trade!";
        //String notifyMessage = "Your selected coin hit your expectation!!";



        for (CurrencyRVModal element : currencyRVModalArrayList){

            if (element.symbol.equals(selectedSymbol)){

                float userPU = Float.parseFloat(String.valueOf(puEditText.getText()));
                prefPU.edit().putFloat("userPU", userPU).apply();

                float userPD = Float.parseFloat(String.valueOf(pdEditText.getText()));
                prefPD.edit().putFloat("userPD", userPD).apply();

                //prefNotify.edit().putBoolean("isNotified", false);

                //Toast.makeText(this, ""+userPU+ " " +userPD, Toast.LENGTH_SHORT).show();




                setAlarm();
            }
        }

//        if (pc24h >= userInputPU){
//            ShowNotification(notifyTitle, notifyMessage);
//        }

    }




    private void getRVData(){
        //Find RecyclerView
        currenciesRV = findViewById(R.id.recyclerView);
        currenciesRV.setHasFixedSize(true);

        loadingPB = findViewById(R.id.idPBLoading);
        currencyRVModalArrayList = new ArrayList<>();

        //Send data to Adapter // After this create sample single view Layout
        currencyRVAdapter = new CurrencyRVAdapter(currencyRVModalArrayList, this);

        //Set the Adapter with RecyclerView
        currenciesRV.setAdapter(currencyRVAdapter);
        //Set Layout Manager to RecyclerView
        currenciesRV.setLayoutManager(new LinearLayoutManager(this));
    }


    public void getCurrencyData(){
        loadingPB.setVisibility(View.VISIBLE);
        String url = "https://pro-api.coinmarketcap.com/v1/cryptocurrency/listings/latest";

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                loadingPB.setVisibility(View.GONE);

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

                        currencyRVModalArrayList.add(new CurrencyRVModal(symbol, name, price, pc24h));
                        //Add name to coinNameList Array fro AlertPage;
                        coinNameList.add(symbol);


                    }

                        currencyRVAdapter.notifyDataSetChanged();


                } catch (JSONException e) {

                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Fail to extract json data...", Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(MainActivity.this, "Fail to get the data", Toast.LENGTH_SHORT).show();

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



//    //Show Notification
//    private void ShowNotification(String title, String message){
//        Notification notification = new NotificationCompat.Builder(this, NotifyApp.CHANNEL_1_ID)
//                .setSmallIcon(R.drawable.ic_dollar)
//                .setContentTitle(title)
//                .setContentText(message)
//                .setPriority(NotificationCompat.PRIORITY_HIGH)
//                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
//                .build();
//
//
//        notificationManager.notify(1, notification);
//    }


    public void setAlarm(){
        Toast.makeText(MainActivity.this, "Reminder Set!", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(MainActivity.this, NotificationBroadcast.class );
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent,0);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        prefNotify.edit().putInt("isNotified", 0).apply();

        long interval = 1000;

        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                1000, interval,
                pendingIntent);
    }
}