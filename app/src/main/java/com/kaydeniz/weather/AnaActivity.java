package com.kaydeniz.weather;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Window;
import android.view.WindowManager;

import com.kaydeniz.weather.Adapter.CityAdapter;
import com.kaydeniz.weather.Model.City;

import java.util.ArrayList;
import java.util.List;

public class AnaActivity extends AppCompatActivity {

    private List<City> cityArrayList;
    private RecyclerView rvCity;
    private CityAdapter adapter;
    private LinearLayoutManager manager;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout);

        rvCity=findViewById(R.id.rvCity);
        manager= new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvCity.setLayoutManager(manager);
        cityArrayList=new ArrayList<>();

        City city1=new City();
        city1.setName("Isparta");
        cityArrayList.add(city1);

        City city2=new City();
        city2.setName("Ankara");
        cityArrayList.add(city2);

        City city3=new City();
        city3.setName("New York");
        cityArrayList.add(city3);

        City city4=new City();
        city4.setName("+");
        cityArrayList.add(city4);

        adapter=new CityAdapter(getApplication(),cityArrayList);
        rvCity.setAdapter(adapter);
        adapter.notifyDataSetChanged();


    }
}
