package com.kaydeniz.weather;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kaydeniz.weather.Adapter.WeatherForecastAdapter;
import com.kaydeniz.weather.Model.WeatherForecastResult;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class ForecastFragment extends Fragment {

    static ForecastFragment instance;
    CompositeDisposable compositeDisposable;
    IOWM mService;
    TextView tvCityName;
    RecyclerView rvForecast;


    public static ForecastFragment getInstance() {

        if (instance==null)
            instance=new ForecastFragment();
        return instance;
    }

    public ForecastFragment() {

        compositeDisposable= new CompositeDisposable();
        Retrofit retrofit=RetrofitClient.getInstance();
        mService=retrofit.create(IOWM.class);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_forecast, container, false);

        tvCityName=view.findViewById(R.id.tvCityName);
        rvForecast=view.findViewById(R.id.rvForecast);
        rvForecast.setHasFixedSize(true);
        rvForecast.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        
        getForecastWeatherInfo();
        


        return view;
    }

    private void getForecastWeatherInfo() {

        compositeDisposable.add(mService.getForecastWeatherByLatLng(
                String.valueOf(General.currentLocation.getLatitude()),
                        String.valueOf(General.currentLocation.getLongitude()),
                                General.APP_ID,
                                "metric").subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<WeatherForecastResult>() {
                    @Override
                    public void accept(WeatherForecastResult weatherForecastResult) throws Exception {
                        displayForecastWeather(weatherForecastResult);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        
                    }
                })
        );
    }

    private void displayForecastWeather(WeatherForecastResult weatherForecastResult) {

        tvCityName.setText(new StringBuilder(weatherForecastResult.city.name));

        WeatherForecastAdapter adapter=new WeatherForecastAdapter(getContext(),weatherForecastResult);
        rvForecast.setAdapter(adapter);

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
