package com.kaydeniz.weather;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kaydeniz.weather.Model.City;
import com.kaydeniz.weather.Model.WeatherResult;
import com.label305.asynctask.SimpleAsyncTask;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class SearchActivity extends AppCompatActivity {

    private List<String> cityList;
    private MaterialSearchBar searchBar;
    private Button btnSaveCity,btnCancel;
    private ArrayList<String> savedList;
    private String city;
    private TextView tvSearch;
    private ProgressBar pbSearch;
    private String lastIndex;
    private int lastIndexInt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        searchBar= (MaterialSearchBar)findViewById(R.id.searchBar);
        searchBar.setEnabled(false);
        btnSaveCity=findViewById(R.id.btnSaveCity);
        btnCancel=findViewById(R.id.btnCancel);
        tvSearch= findViewById(R.id.tvSearch);
        pbSearch=findViewById(R.id.pbSearch);

        loadData();

        new LoadCities().execute();

        btnSaveCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                boolean isThere=false;

                for (int k=0;k<savedList.size();k++){
                    if(savedList.get(k).equals(city)){
                        isThere=true;
                    }
                }

                if(isThere){
                    Toast.makeText(SearchActivity.this,"City was already added.",Toast.LENGTH_SHORT).show();
                } else {
                    pbSearch.setVisibility(View.VISIBLE);
                    savedList.add(city);
                    saveCity();
                    Toast.makeText(SearchActivity.this,"City is added.",Toast.LENGTH_SHORT).show();
                    Intent in=new Intent(SearchActivity.this,MainActivity.class);
                    startActivity(in);
                }

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });

    }

    private void saveCity() {

        SharedPreferences sharedPref = getSharedPreferences("shared preferences",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        Gson gson= new Gson();
        String jSon= gson.toJson(savedList);
        editor.putString("cityName",jSon);
        editor.commit();
    }

    private void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("cityName", null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        savedList = gson.fromJson(json, type);

        if (savedList == null) {
            savedList = new ArrayList<>();
        }
    }

    public class LoadCities extends SimpleAsyncTask<List<String>> {
        @Override
        protected List<String> doInBackgroundSimple() {

            cityList=new ArrayList<>();

            try{
                StringBuilder builder= new StringBuilder();
                InputStream is= getResources().openRawResource(R.raw.city_list);
                GZIPInputStream gzıpInputStream=new GZIPInputStream(is);

                InputStreamReader reader=new InputStreamReader(gzıpInputStream);
                BufferedReader in=new BufferedReader(reader);

                String readed;

                while ((readed=in.readLine())!=null)
                    builder.append(readed);
                cityList=new Gson().fromJson(builder.toString(),new TypeToken<List<String>>(){}.getType());

            } catch (IOException e) {
                e.printStackTrace();
            }

            return cityList;
        }

        @Override
        protected void onSuccess(final List<String> listCity) {
            super.onSuccess(listCity);

            searchBar.setEnabled(true);
            searchBar.addTextChangeListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    List<String> suggest=new ArrayList<>();

                    for (String search: listCity){
                        if(search.toLowerCase().contains(searchBar.getText().toLowerCase() ))
                            suggest.add(search);
                    }

                    searchBar.setLastSuggestions(suggest);

                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
                @Override
                public void onSearchStateChanged(boolean enabled) {

                }

                @Override
                public void onSearchConfirmed(CharSequence text) {

                    city=text.toString();
                    searchBar.setLastSuggestions(listCity);
                    btnSaveCity.setVisibility(View.VISIBLE);
                    btnCancel.setVisibility(View.VISIBLE);
                    tvSearch.setText("Do you want add city: "+city);
                    tvSearch.setVisibility(View.VISIBLE);

                }

                @Override
                public void onButtonClicked(int buttonCode) {

                }
            });

            searchBar.setLastSuggestions(listCity);


        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
