package com.kaydeniz.weather;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kaydeniz.weather.Model.WeatherResult;
import com.label305.asynctask.SimpleAsyncTask;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class CityFragment extends Fragment {

    private List<String> cityList;
    private MaterialSearchBar searchBar;

    private ImageView weatherImage;
    private TextView tvCityName,tvTemp,tvDescp,tvDateTime,tvWind,tvPressure,tvHumidty,tvSunrise,tvSunset,tvCoord;
    private LinearLayout panel;
    private ProgressBar progressBar;

    CompositeDisposable compositeDisposable;
    IOWM service;

    static CityFragment instance;

    public static CityFragment getInstance() {

        if (instance==null)
            instance=new CityFragment();
        return instance;
    }

    public CityFragment() {

        compositeDisposable= new CompositeDisposable();
        Retrofit retrofit=RetrofitClient.getInstance();
        service=retrofit.create(IOWM.class);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_city, container, false);

        weatherImage=(ImageView) view.findViewById(R.id.weatherImage);
        tvCityName=(TextView) view.findViewById(R.id.tvCityName);
        tvTemp=(TextView) view.findViewById(R.id.tvTemp);
        tvDescp=(TextView) view.findViewById(R.id.tvDescp);
        tvDateTime=(TextView) view.findViewById(R.id.tvDateTime);
        tvWind=(TextView) view.findViewById(R.id.tvWind);
        tvPressure=(TextView) view.findViewById(R.id.tvPressure);
        tvHumidty=(TextView) view.findViewById(R.id.tvHumidty);
        tvSunrise=(TextView) view.findViewById(R.id.tvSunrise);
        tvSunset=(TextView) view.findViewById(R.id.tvSunset);
        tvCoord=(TextView) view.findViewById(R.id.tvCoord);
        panel=(LinearLayout) view.findViewById(R.id.panel);
        progressBar=(ProgressBar) view.findViewById(R.id.progressBar);

        searchBar= (MaterialSearchBar) view.findViewById(R.id.searchBar);
        searchBar.setEnabled(false);

        new LoadCities().execute();

        return view;
    }

    private class LoadCities extends SimpleAsyncTask<List<String>> {
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

                    getWeatherInfo(text.toString());
                    searchBar.setLastSuggestions(listCity);

                }

                @Override
                public void onButtonClicked(int buttonCode) {

                }
            });

            searchBar.setLastSuggestions(listCity);

            progressBar.setVisibility(View.GONE);

        }
    }

    private void getWeatherInfo(String cityName) {

        compositeDisposable.add(service.getWeatherByCityName(cityName,
                General.APP_ID,"metric").
                subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<WeatherResult>() {
                    @Override
                    public void accept(WeatherResult weatherResult) throws Exception {

                        Picasso.get().load(new StringBuilder("https://openweathermap.org/img/w/").
                                append(weatherResult.getWeather().get(0).getIcon()).append(".png").toString()).into(weatherImage);

                        tvCityName.setText(weatherResult.getName()+", "+weatherResult.getSys().getCountry());
                        tvDescp.setText(weatherResult.getWeather().get(0).getDescription());
                        tvWind.setText(String.valueOf(weatherResult.getWind().getSpeed()));
                        tvTemp.setText(new StringBuilder(String.valueOf(weatherResult.getMain().getTemp())).append("°C").toString());
                        tvDateTime.setText(General.convertUnixToDate(weatherResult.getDt()));
                        tvPressure.setText(new StringBuilder(String.valueOf(weatherResult.getMain().getPressure())).append(" hpa").toString());
                        tvHumidty.setText(new StringBuilder(String.valueOf(weatherResult.getMain().getHumidity())).append(" %").toString());
                        tvSunrise.setText(General.convertUnixToHour(weatherResult.getSys().getSunrise()));
                        tvSunset.setText(General.convertUnixToHour(weatherResult.getSys().getSunset()));
                        tvCoord.setText(new StringBuilder(weatherResult.getCoord().toString()).toString());

                        Log.d("Country",weatherResult.getSys().getCountry());
                        Log.d("ID",String.valueOf(weatherResult.getSys().getId()));
                        Log.d("Sıcaklık Max",String.valueOf(weatherResult.getMain().getTemp_max()));
                        Log.d("Sıcaklık Min",String.valueOf(weatherResult.getMain().getTemp_min()));
                        Log.d("Description",weatherResult.getWeather().get(0).getDescription());
                        Log.d("Getweather Id",String.valueOf(weatherResult.getWeather().get(0).getId()));
                        Log.d("Main",weatherResult.getWeather().get(0).getMain());



                        panel.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(getActivity(),throwable.getMessage(),Toast.LENGTH_LONG).show();
                    }
                })

        );
    }

    @Override
    public void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    @Override
    public void onStop() {
        compositeDisposable.clear();
        super.onStop();
    }
}
