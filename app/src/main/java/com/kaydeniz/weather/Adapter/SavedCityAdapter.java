package com.kaydeniz.weather.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.kaydeniz.weather.MainActivity;
import com.kaydeniz.weather.ManageCityActivity;
import com.kaydeniz.weather.Model.City;
import com.kaydeniz.weather.Model.Main;
import com.kaydeniz.weather.Model.MessageEvent;
import com.kaydeniz.weather.R;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class SavedCityAdapter extends RecyclerView.Adapter<SavedCityAdapter.ViewHolder>{

    private static final String TAG="SavedCityAdapter";

    private ArrayList<String> savedCity;
    private Context myContext;


    public SavedCityAdapter(Context myContext, ArrayList<String> savedCity) {

        this.myContext = myContext;
        this.savedCity = savedCity;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_saved_list,parent,false);
        ViewHolder holder=new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int i) {

        final String name= savedCity.get(i);

        holder.tvSavedCity.setText(name);

        holder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AlertDialog.Builder(myContext).setIcon(R.drawable.ic_warning_black_24dp).setTitle("Delete City")
                        .setMessage("Are you sure to delete "+name+"?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                savedCity.remove(i);
                                saveCity(savedCity);
                                notifyDataSetChanged();
                                //Intent in=new Intent(myContext, MainActivity.class);
                                //myContext.startActivity(in);
                            }
                        }).setNegativeButton("No", null).show();




            }
        });



    }


    @Override
    public int getItemCount() {
        return savedCity.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvSavedCity;
        ImageView ivDelete;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvSavedCity=itemView.findViewById(R.id.tvSavedCity);
            ivDelete=itemView.findViewById(R.id.ivDelete);

        }
    }

    public ArrayList<String> getSavedCity() {
        return savedCity;
    }

    private void saveCity(ArrayList<String> list) {

        SharedPreferences sharedPref=myContext.getSharedPreferences("shared preferences",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        Gson gson= new Gson();
        String jSon= gson.toJson(list);
        editor.putString("cityName",jSon);
        editor.commit();
    }


}
