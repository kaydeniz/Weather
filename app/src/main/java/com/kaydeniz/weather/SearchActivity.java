package com.kaydeniz.weather;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kaydeniz.weather.Model.City;
import com.kaydeniz.weather.Model.WeatherResult;
import com.label305.asynctask.SimpleAsyncTask;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.zip.GZIPInputStream;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

import static android.text.InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener {

    private List<String> cityList;
    private Button btnSaveCity,btnCancel;
    private ArrayList<String> savedList;
    private String city;
    private TextView tvSearch;
    private ProgressBar pbSearch;

    private AutoCompleteTextView etSearchCity;
    ArrayAdapter<String> autoComplete;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        btnSaveCity=findViewById(R.id.btnSaveCity);
        btnCancel=findViewById(R.id.btnCancel);
        tvSearch= findViewById(R.id.tvSearch);
        pbSearch=findViewById(R.id.pbSearch);



        etSearchCity=(AutoCompleteTextView)findViewById(R.id.etSearchCity);
        etSearchCity.setRawInputType(InputType.TYPE_CLASS_TEXT |
                InputType.TYPE_TEXT_VARIATION_NORMAL);


        loadData();

        bringData();

        btnSaveCity.setOnClickListener(this);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });

        try{
            autoComplete = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,cityList);
            etSearchCity.setAdapter(autoComplete);
            etSearchCity.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                        long arg3) {

                    city=(String) arg0.getAdapter().getItem(arg2);
                    btnSaveCity.setVisibility(View.VISIBLE);
                    btnCancel.setVisibility(View.VISIBLE);
                    tvSearch.setText("Do you want add city: "+city);
                    tvSearch.setVisibility(View.VISIBLE);
                }
            });


        } catch (Exception e){

        }
    }

    public void onClick(View view) {
        // detect the view that was "clicked"
        switch (view.getId()) {
            case R.id.btnSaveCity:
                new LongOperation().execute("");
                break;
        }
    }

    private class LongOperation extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            boolean isThere=false;

            for (int k=0;k<savedList.size();k++){
                if(savedList.get(k).equals(city)){
                    isThere=true;
                }
            }

            if(isThere){
                Toast.makeText(SearchActivity.this,"City was already added.",Toast.LENGTH_SHORT).show();
            } else {
                savedList.add(city);
                saveCity();

            }
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {

            Toast.makeText(SearchActivity.this,"City is added.",Toast.LENGTH_SHORT).show();
            Intent in=new Intent(SearchActivity.this,MainActivity.class);
            in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(in);
        }

        @Override
        protected void onPreExecute() {
            pbSearch.setVisibility(View.VISIBLE);

        }

        @Override
        protected void onProgressUpdate(Void... values) {}
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

    private void bringData() {
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
        Intent in=new Intent(SearchActivity.this,MainActivity.class);
        in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(in);
        super.onBackPressed();
    }


}
