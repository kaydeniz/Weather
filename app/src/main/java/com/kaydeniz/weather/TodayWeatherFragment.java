package com.kaydeniz.weather;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.kaydeniz.weather.Model.WeatherResult;
import com.squareup.picasso.Picasso;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;


public class TodayWeatherFragment extends Fragment {

    private ImageView weatherImage;
    private TextView tvCityName,tvTemp,tvDescp,tvDateTime,tvWind,tvPressure,tvHumidty,tvSunrise,tvSunset,tvCoord;
    private LinearLayout panel;
    private ProgressBar progressBar;

    CompositeDisposable compositeDisposable;
    IOWM service;

    static TodayWeatherFragment instance;

    public static TodayWeatherFragment getInstance() {

        if (instance==null)
            instance=new TodayWeatherFragment();
        return instance;
    }

    public TodayWeatherFragment() {

        compositeDisposable= new CompositeDisposable();
        Retrofit retrofit=RetrofitClient.getInstance();
        service=retrofit.create(IOWM.class);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_today_weather, container, false);

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

        getWeather();


        return view;
    }

    private void getWeather() {
        compositeDisposable.add(service.getWeatherByLatLng(String.valueOf(OWM.currentLocation.getLatitude()),
                String.valueOf(OWM.currentLocation.getLongitude()),
                OWM.APP_ID,"metric").
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
                                tvDateTime.setText(OWM.convertUnixToDate(weatherResult.getDt()));
                                tvPressure.setText(new StringBuilder(String.valueOf(weatherResult.getMain().getPressure())).append(" hpa").toString());
                                tvHumidty.setText(new StringBuilder(String.valueOf(weatherResult.getMain().getHumidity())).append(" %").toString());
                                tvSunrise.setText(OWM.convertUnixToHour(weatherResult.getSys().getSunrise()));
                                tvSunset.setText(OWM.convertUnixToHour(weatherResult.getSys().getSunset()));
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
    public void onStop() {
        compositeDisposable.clear();
        super.onStop();
    }

}
