package com.kaydeniz.weather;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kaydeniz.weather.Adapter.CityAdapter;
import com.kaydeniz.weather.Adapter.SavedCityAdapter;
import com.kaydeniz.weather.Model.City;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ManageCityActivity extends AppCompatActivity {

    private ArrayList<String> savedList;
    private RecyclerView rvSavedCity;
    private SavedCityAdapter adapter;
    private LinearLayoutManager manager;
    private ImageView ivBackManageCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_city);

        rvSavedCity = findViewById(R.id.rvSavedCity);
        rvSavedCity.setHasFixedSize(true);
        manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvSavedCity.setLayoutManager(manager);
        ivBackManageCity=findViewById(R.id.ivBackManageCity);

        loadData();

        ivBackManageCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });




    }

    private void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("cityName", null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        savedList = gson.fromJson(json, type);

        Log.d("listeJson",json);
        Log.d("listeType",String.valueOf(type));



        if (savedList == null) {
            savedList = new ArrayList<>();
        } else {

            adapter=new SavedCityAdapter(this,savedList);
            rvSavedCity.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }


}
