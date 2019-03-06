package com.kaydeniz.weather.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.kaydeniz.weather.Model.City;
import com.kaydeniz.weather.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CityAdapter extends RecyclerView.Adapter<CityAdapter.ViewHolder>{

    private static final String TAG="CityAdapter";

    private List<City> cityArrayList;
    private Context myContext;

    private int cIndex=-1;


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
        holder.tvHeaderCityName.setText(name);

        if(firstLetter.equals('A')){
            Picasso.get().load(R.drawable.letter_a).fit().into(holder.cityImage);
        } else {
            Picasso.get().load(R.drawable.letter_b).fit().into(holder.cityImage);
        }



        /*holder.clLit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(myContext, GalleryActivity.class);
                intent.putExtra("cins",litArrayList.get(i).getCins());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                myContext.startActivity(intent);
            }
        });*/


        holder.cityImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                cIndex=i;
                notifyDataSetChanged();


                for (int k=0;k<cityArrayList.size();k++){

                    if(k==i){
                        cityArrayList.get(k).setChecked(true);
                    } else {
                        cityArrayList.get(k).setChecked(false);
                    }
                }

            }
        });

        if(cIndex==i){

            holder.cityImage.setAlpha(1f);
            holder.cityImage.setBorderWidth(5);
            holder.tvHeaderCityName.setTypeface(Typeface.DEFAULT_BOLD);
        } else
        {
            holder.cityImage.setAlpha(0.5f);
            holder.cityImage.setBorderWidth(2);
            holder.tvHeaderCityName.setTypeface(Typeface.DEFAULT);

        }


    }


    @Override
    public int getItemCount() {
        return cityArrayList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView cityImage;
        TextView tvHeaderCityName;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cityImage=itemView.findViewById(R.id.cityImage);
            tvHeaderCityName=itemView.findViewById(R.id.tvHeaderCityName);

        }
    }

    public List<City> getCityArrayList() {
        return cityArrayList;
    }


}
