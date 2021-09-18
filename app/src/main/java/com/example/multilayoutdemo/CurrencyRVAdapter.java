package com.example.multilayoutdemo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class CurrencyRVAdapter extends RecyclerView.Adapter<CurrencyRVAdapter.MyViewHolder> {

    //Mention data which I want to show in RV
    ArrayList<CurrencyRVModal> currencyRVModalArrayList;
    Context context;
    static DecimalFormat df2 = new DecimalFormat("#.##");

    //Generated Constructor for all Data
    public CurrencyRVAdapter(ArrayList<CurrencyRVModal> currencyRVModalArrayList, Context context) {
        this.currencyRVModalArrayList = currencyRVModalArrayList;
        this.context = context;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //Inflate sample layout
        View view = LayoutInflater.from(context).inflate(R.layout.sample_layout, parent, false);

        return new MyViewHolder(view) ;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        // Set all data into views // Which data we got from MyViewHolder
        CurrencyRVModal currencyRVModal = currencyRVModalArrayList.get(position);
        holder.symbolTextView.setText(currencyRVModal.getSymbol());
        holder.nameTextView.setText(currencyRVModal.getName());
        holder.priceTextView.setText("$"+df2.format(currencyRVModal.getPrice()));

    }

    @Override
    public int getItemCount() {
        return currencyRVModalArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        //Declare variable fro Views // Which we will see on sample_layout
        TextView symbolTextView, nameTextView, priceTextView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            // Find all views from given itemView / We got sample_layout as itemView from onCreateViewHolder using Inflater
            symbolTextView = itemView.findViewById(R.id.symbolTextView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            priceTextView = itemView.findViewById(R.id.priceTextView);


        }
    }
}
