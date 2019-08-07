package com.example.android.travelmantics;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class TravelEntryAdapter extends RecyclerView.Adapter<TravelEntryViewHolder> {

    private List<TravelManticsModel> entrylist;
    private final Context context;


    public  TravelEntryAdapter (List<TravelManticsModel> entrylist, Context context){

        this.entrylist = entrylist;
        this.context = context;

    }

    @NonNull
    @Override
    public TravelEntryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.travel_list_item, parent, false);
        return new TravelEntryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final TravelEntryViewHolder holder, int position) {

        //holder.bind(photolist.get(position), listener);
        final TravelManticsModel travelManticsModel = entrylist.get(position);
        Picasso.with(context).load(travelManticsModel.getImageView()).placeholder(context.getResources().getDrawable(R.drawable.resort)).into(holder.ivtravel);
        holder.tvcity.setText("City :" +" "+ travelManticsModel.getCity());
        holder.tvhotel.setText("Hotel:" + " "+travelManticsModel.getHotel());
        holder.tvamount.setText("Amount($):" + " "+travelManticsModel.getAmount());
    }

    @Override
    public int getItemCount() {
        return entrylist.size();
    }


    }
