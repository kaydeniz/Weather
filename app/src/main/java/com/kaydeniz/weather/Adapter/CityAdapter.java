package com.kaydeniz.weather.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.kaydeniz.weather.Model.City;
import com.kaydeniz.weather.Model.MessageEvent;
import com.kaydeniz.weather.R;
import com.kaydeniz.weather.SearchActivity;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class CityAdapter extends RecyclerView.Adapter<CityAdapter.ViewHolder>{

    private static final String TAG="CityAdapter";

    private List<City> cityArrayList;
    private Context myContext;

    private int cIndex=0;

    public CityAdapter(Context myContext, List<City> cityArrayList) {

        this.myContext = myContext;
        this.cityArrayList = cityArrayList;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_city,parent,false);
        ViewHolder holder=new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int i) {

        String name= cityArrayList.get(i).getName();
        Character firstLetter=name.charAt(0);
        String fL= String.valueOf(firstLetter);

        if(i==0){

            holder.tvHeaderCityName.setText("Loc: "+name);

            holder.tvLogo.setText(fL);

        } else {


            if(name.equals("+")){
                holder.tvHeaderCityName.setText("Add City");

                holder.tvLogo.setText(fL);
            } else{

                holder.tvHeaderCityName.setText(name);

                holder.tvLogo.setText(fL);
            }
        }

        holder.llCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                notifyDataSetChanged();
                cIndex=i;

                for (int k=0;k<cityArrayList.size();k++){

                    if(k==i){
                        cityArrayList.get(k).setChecked(true);

                        if(i==(cityArrayList.size()-1)){
                            Intent in=new Intent(myContext, SearchActivity.class);
                            in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            myContext.startActivity(in);
                        }

                        //send chosen info to activiy with Eventbus
                        MessageEvent event=new MessageEvent();
                        event.setCityIndex(i);
                        event.setCityName(cityArrayList.get(i).getName());
                        event.setCoord(cityArrayList.get(i).getCoord());
                        EventBus.getDefault().postSticky(event);

                    } else {
                        cityArrayList.get(k).setChecked(false);
                    }
                }

            }
        });


        if (cIndex == i) {

                holder.llCity.setAlpha(1f);
                holder.tvHeaderCityName.setTypeface(Typeface.DEFAULT_BOLD);
            } else {
                holder.llCity.setAlpha(0.5f);
                holder.tvHeaderCityName.setTypeface(Typeface.DEFAULT);
            }

    }


    @Override
    public int getItemCount() {
        return cityArrayList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvHeaderCityName,tvLogo;
        LinearLayout llCity;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvHeaderCityName=itemView.findViewById(R.id.tvHeaderCityName);
            tvLogo=itemView.findViewById(R.id.tvLogo);
            llCity=itemView.findViewById(R.id.llCity);
        }
    }

    public List<City> getCityArrayList() {
        return cityArrayList;
    }


}
