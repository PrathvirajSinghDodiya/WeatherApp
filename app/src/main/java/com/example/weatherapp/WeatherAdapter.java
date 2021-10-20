package com.example.weatherapp;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;


import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class WeatherAdapter extends  RecyclerView.Adapter<WeatherAdapter.ViewHolder> {
    Context context;
    ArrayList<WeatherModel> weatherModelArrayList;

    public WeatherAdapter(Context context, ArrayList<WeatherModel> weatherModelArrayList) {
        this.context = context;
        this.weatherModelArrayList = weatherModelArrayList;
    }

    @NonNull
    @Override
    public WeatherAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycle_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherAdapter.ViewHolder holder, int position) {
        holder.tempTv.setText(weatherModelArrayList.get(position).getTemp()+"°c");
        holder.windSpeedTv.setText(weatherModelArrayList.get(position).getWindSpeed()+"km/hr");
        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd mm:hh");
        SimpleDateFormat output = new SimpleDateFormat(" mm:hh aa");
        try {
            Date date = input.parse(weatherModelArrayList.get(position).getTime());
            holder.timeTV.setText(output.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }


        Picasso.get().load("http:".concat(weatherModelArrayList.get(position).getIcon())).into(holder.ConditTv);
        if(weatherModelArrayList.get(position).day_night==1){
            holder.relativeLayout.setBackgroundResource(R.drawable.back_one);
        }
        else {
            holder.relativeLayout.setBackgroundResource(R.drawable.backrecycle);
        }




    }

    @Override
    public int getItemCount() {
        return weatherModelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder  {
        private TextView timeTV,tempTv,windSpeedTv;
        private ImageView ConditTv;
        RelativeLayout relativeLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            timeTV = itemView.findViewById(R.id.timeshow);
            tempTv = itemView.findViewById(R.id.tempshow);
            windSpeedTv = itemView.findViewById(R.id.windSpeedshow);
            ConditTv = itemView.findViewById(R.id.conditshow);
            relativeLayout = itemView.findViewById(R.id.colorchange);
        }


    }
}
