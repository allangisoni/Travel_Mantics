package com.example.android.travelmantics;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class TravelEntryViewHolder extends RecyclerView.ViewHolder {

    public ImageView ivtravel;
    public TextView tvcity, tvamount, tvhotel;

    public TravelEntryViewHolder(View view){
        super(view);

        ivtravel = view.findViewById(R.id.journalivtravel);
        tvcity = view.findViewById(R.id.etcity);
        tvamount = view.findViewById(R.id.etamount);
        tvhotel = view.findViewById(R.id.etHotel);
    }
}
