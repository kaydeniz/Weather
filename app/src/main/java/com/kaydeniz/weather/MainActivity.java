package com.kaydeniz.weather;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.tianma8023.model.Time;
import com.github.tianma8023.ssv.SunriseSunsetView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kaydeniz.weather.Adapter.CityAdapter;
import com.kaydeniz.weather.Adapter.WeatherForecastAdapter;
import com.kaydeniz.weather.Model.City;
import com.kaydeniz.weather.Model.Coord;
import com.kaydeniz.weather.Model.MessageEvent;
import com.kaydeniz.weather.Model.Weather;
import com.kaydeniz.weather.Model.WeatherForecastResult;
import com.kaydeniz.weather.Model.WeatherResult;
import com.squareup.picasso.Picasso;
import com.timqi.sectorprogressview.ColorfulRingProgressView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private boolean mLocationPermissionGranted = false;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;

    private ArrayList<String> savedList;
    private List<City> cityArrayList;
    private RecyclerView rvCity,rv5Days;
    private CityAdapter adapter;
    private LinearLayoutManager manager;

    private ImageView weatherType, refresh,settings;
    private TextView tvTemp, tvDescp, tvMaxTemp, tvMinTemp,
            tvDirectionResult, tvSpeedResult, tvPressureResult, tvHumidtyResult,tvSunrise,tvSunset,tvLastUpdate;
    private ColorfulRingProgressView crpv;
    private SunriseSunsetView ssv;
    private Coord tempCord=new Coord();
    WeatherResult wr=new WeatherResult();

    private AdView mAdView;
    CompositeDisposable compositeDisposable;
    IOWM service;

    static MainActivity instance;

    public static MainActivity getInstance() {

        if (instance == null)
            instance = new MainActivity();
        return instance;
    }

    public MainActivity() {

        compositeDisposable = new CompositeDisposable();
        Retrofit retrofit = RetrofitClient.getInstance();
        service = retrofit.create(IOWM.class);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        // Sample AdMob app ID: ca-app-pub-3940256099942544~3347511713
        MobileAds.initialize(this, "ca-app-pub-7950548692452409~9905505254");


        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        cityArrayList = new ArrayList<>();
        refresh = findViewById(R.id.refresh);
        weatherType = (ImageView) findViewById(R.id.weatherType);
        tvTemp = (TextView) findViewById(R.id.tvTemp);
        tvDescp = (TextView) findViewById(R.id.tvDescp);
        tvLastUpdate=(TextView) findViewById(R.id.tvLastUpdate);
        tvDirectionResult = (TextView) findViewById(R.id.tvDirectionResult);
        tvSpeedResult = (TextView) findViewById(R.id.tvSpeedResult);
        tvHumidtyResult = (TextView) findViewById(R.id.tvHumidtyResult);
        tvPressureResult = (TextView) findViewById(R.id.tvPressureResult);
        tvMaxTemp = (TextView) findViewById(R.id.tvMaxTemp);
        tvMinTemp = (TextView) findViewById(R.id.tvMinTemp);
        tvSunrise = (TextView) findViewById(R.id.tvSunrise);
        tvSunset = (TextView) findViewById(R.id.tvSunset);
        crpv=(ColorfulRingProgressView) findViewById(R.id.crpv);
        ssv=(SunriseSunsetView) findViewById(R.id.ssv);
        settings=(ImageView) findViewById(R.id.settings);

        loadData();

        Log.d("naberlen","dfusdh");

        rvCity = findViewById(R.id.rvCity);
        rvCity.setHasFixedSize(true);
        manager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvCity.setLayoutManager(manager);

        rv5Days=findViewById(R.id.rv5Days);
        rv5Days.setHasFixedSize(true);
        rv5Days.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));

        getLocationPermission();

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cityArrayList.clear();
                getLocationPermission();
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in=new Intent(MainActivity.this,SettingsActivity.class);
                startActivity(in);
            }
        });



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


    private int roundToInt(double temp) {

        double dAbs = Math.abs(temp);
        int i = (int) dAbs;
        double result = dAbs - (double) i;
        if(result<0.5){
            return temp<0 ? -i : i;
        }else{
            return temp<0 ? -(i+1) : i+1;
        }
    }

    private void getWeather() {
        compositeDisposable.add(service.getWeatherByLatLng(String.valueOf(General.currentLocation.getLatitude()),
                String.valueOf(General.currentLocation.getLongitude()),
                General.APP_ID, "metric").
                subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<WeatherResult>() {
                    @Override
                    public void accept(WeatherResult weatherResult) throws Exception {

                        Picasso.get().load(new StringBuilder("https://openweathermap.org/img/w/").
                                append(weatherResult.getWeather().get(0).getIcon()).append(".png").toString()).
                                placeholder(R.drawable.placeholder).into(weatherType);

                        City city0 = new City();
                        city0.setName(weatherResult.getName());
                        city0.setCoord(weatherResult.getCoord());
                        cityArrayList.add(city0);


                        for (int k=0;k<savedList.size();k++){
                            City temp=new City();
                            temp.setName(savedList.get(k));
                            cityArrayList.add(temp);
                            Log.d("koordinat",String.valueOf(temp.getCoord()));

                        }

                        City cityAdd = new City();
                        cityAdd.setName("+");
                        cityArrayList.add(cityAdd);

                        adapter = new CityAdapter(getApplication(), cityArrayList);
                        rvCity.setAdapter(adapter);
                        adapter.notifyDataSetChanged();

                        tvTemp.setText(new StringBuilder(String.valueOf(roundToInt(weatherResult.getMain().getTemp()))).append("°C").toString());
                        tvMaxTemp.setText(new StringBuilder(String.valueOf(roundToInt(weatherResult.getMain().getTemp_max()))).append("°C / ").toString());
                        tvMinTemp.setText(new StringBuilder(String.valueOf(roundToInt(weatherResult.getMain().getTemp_min()))).append("°C").toString());
                        tvDescp.setText(weatherResult.getWeather().get(0).getDescription());
                        tvSpeedResult.setText(String.valueOf(weatherResult.getWind().getSpeed()));
                        tvDirectionResult.setText(degToName(weatherResult.getWind().getDeg()));
                        tvPressureResult.setText(new StringBuilder(String.valueOf(weatherResult.getMain().getPressure())).append(" hpa").toString());
                        tvHumidtyResult.setText(new StringBuilder(String.valueOf(weatherResult.getMain().getHumidity())).append(" %").toString());
                        crpv.setPercent(weatherResult.getMain().getHumidity());

                        tvSunrise.setText(General.convertUnixToHour(weatherResult.getSys().getSunrise()));
                        tvSunset.setText(General.convertUnixToHour(weatherResult.getSys().getSunset()));

                        int sunriseHour,sunriseMin,sunsetHour,sunsetMin;
                        String sunriseHourS,sunsetHourS;

                        sunriseHourS= General.convertUnixToHour(weatherResult.getSys().getSunrise());
                        sunsetHourS= General.convertUnixToHour(weatherResult.getSys().getSunset());

                        String[] parts = sunriseHourS.split(":");
                        String part1 = parts[0];
                        String part2 = parts[1];

                        String[] partsSunset = sunsetHourS.split(":");
                        String part3 = partsSunset[0];
                        String part4 = partsSunset[1];

                        sunriseHour=Integer.parseInt(part1);
                        sunriseMin=Integer.parseInt(part2);
                        sunsetHour=Integer.parseInt(part3);
                        sunsetMin=Integer.parseInt(part4);

                        ssv.setSunriseTime(new Time(sunriseHour,sunriseMin));
                        ssv.setSunsetTime(new Time(sunsetHour,sunsetMin));
                        ssv.animate().setDuration(20000);
                        ssv.animate().setStartDelay(30000);
                        ssv.setLabelTextSize(0);
                        ssv.startAnimate();


                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(MainActivity.this, throwable.getMessage(), Toast.LENGTH_LONG).show();
                    }
                })

        );
    }

    private void getWeatherByName(String cityName) {
        compositeDisposable.add(service.getWeatherByCityName(cityName,
                General.APP_ID, "metric").
                subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<WeatherResult>() {
                    @Override
                    public void accept(WeatherResult weatherResult) throws Exception {

                        Picasso.get().load(new StringBuilder("https://openweathermap.org/img/w/").
                                append(weatherResult.getWeather().get(0).getIcon()).append(".png").toString())
                                .placeholder(R.drawable.placeholder).into(weatherType);


                        tvTemp.setText(new StringBuilder(String.valueOf(roundToInt(weatherResult.getMain().getTemp()))).append("°C").toString());
                        tvMaxTemp.setText(new StringBuilder(String.valueOf(roundToInt(weatherResult.getMain().getTemp_max()))).append("°C / ").toString());
                        tvMinTemp.setText(new StringBuilder(String.valueOf(roundToInt(weatherResult.getMain().getTemp_min()))).append("°C").toString());
                        tvDescp.setText(weatherResult.getWeather().get(0).getDescription());
                        tvSpeedResult.setText(String.valueOf(weatherResult.getWind().getSpeed()));
                        tvDirectionResult.setText(degToName(weatherResult.getWind().getDeg()));
                        tvPressureResult.setText(new StringBuilder(String.valueOf(weatherResult.getMain().getPressure())).append(" hpa").toString());
                        tvHumidtyResult.setText(new StringBuilder(String.valueOf(weatherResult.getMain().getHumidity())).append(" %").toString());
                        crpv.setPercent(weatherResult.getMain().getHumidity());

                        tvSunrise.setText(General.convertUnixToHour(weatherResult.getSys().getSunrise()));
                        tvSunset.setText(General.convertUnixToHour(weatherResult.getSys().getSunset()));

                        int sunriseHour,sunriseMin,sunsetHour,sunsetMin;
                        String sunriseHourS,sunsetHourS;

                        sunriseHourS= General.convertUnixToHour(weatherResult.getSys().getSunrise());
                        sunsetHourS= General.convertUnixToHour(weatherResult.getSys().getSunset());

                        String[] parts = sunriseHourS.split(":");
                        String part1 = parts[0];
                        String part2 = parts[1];

                        String[] partsSunset = sunsetHourS.split(":");
                        String part3 = partsSunset[0];
                        String part4 = partsSunset[1];

                        sunriseHour=Integer.parseInt(part1);
                        sunriseMin=Integer.parseInt(part2);
                        sunsetHour=Integer.parseInt(part3);
                        sunsetMin=Integer.parseInt(part4);

                        ssv.setSunriseTime(new Time(sunriseHour,sunriseMin));
                        ssv.setSunsetTime(new Time(sunsetHour,sunsetMin));
                        ssv.animate().setDuration(20000);
                        ssv.animate().setStartDelay(30000);
                        ssv.setLabelTextSize(0);
                        ssv.startAnimate();


                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(MainActivity.this, throwable.getMessage(), Toast.LENGTH_LONG).show();
                    }
                })

        );
    }

    private void getForecastWeatherInfo() {

        compositeDisposable.add(service.getForecastWeatherByLatLng(
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

    private void getForecastWeatherInfoByCityName(String cityName) {

        compositeDisposable.add(service.getForecastWeatherCityName(cityName,
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


        WeatherForecastAdapter adapter=new WeatherForecastAdapter(this,weatherForecastResult);
        rv5Days.setAdapter(adapter);

    }

    private String degToName(double deg) {

        String directions[] = {"North", "North East", "East", "South East", "South", "South West", "West", "North West", "North"};
        return directions[ (int)Math.round((  ((double)deg % 360) / 45)) ];
    }

    private void getLocationPermission() {
        String[] permission = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(MainActivity.this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            if (ContextCompat.checkSelfPermission(MainActivity.this.getApplicationContext(),
                    COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
                getDeviceLocation();
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, permission, LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, permission, LOCATION_PERMISSION_REQUEST_CODE);
        }

        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        Date date = new Date();
        final String time= dateFormat.format(date);
        tvLastUpdate.setText("Last Update "+time);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        mLocationPermissionGranted = false;

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionGranted = false;
                            return;
                        }
                    }
                    mLocationPermissionGranted = true;
                    //get current location
                    getDeviceLocation();
                }
            }
        }
    }

    private void getDeviceLocation() {
        buildLocationRequest();
        buildLocationCallback();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());


        try {

            if (mLocationPermissionGranted) {

                final Task location = fusedLocationProviderClient.getLastLocation();

                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Location currentLocation = (Location) task.getResult();

                            if (location != null) {

                                General.currentLocation = new Location(currentLocation);
                                Log.d("Lokasyon",String.valueOf(currentLocation.getLatitude())+"----"+String.valueOf(currentLocation.getLongitude()));

                                getWeather();
                                getForecastWeatherInfo();

                            }

                        } else {
                            Toast.makeText(MainActivity.this, "Location Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }

        } catch (SecurityException e) {

        }

    }

    private void buildLocationRequest() {
        locationRequest=new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setSmallestDisplacement(10.0f);
    }

    private void buildLocationCallback() {

        locationCallback=new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult){
                super.onLocationResult(locationResult);
                General.currentLocation=locationResult.getLastLocation();

                Log.d("Location",locationResult.getLastLocation().getLatitude()+"-" +locationResult.getLastLocation().getLongitude());
            }
        };
    }

    private void forPermissionOld() {
        /*  //For permission
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {

                        if (report.areAllPermissionsGranted()) {

                            buildLocationRequest();
                            buildLocationCallback();

                            if (ActivityCompat.checkSelfPermission(MainActivity.this,
                                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                                    ActivityCompat.checkSelfPermission(MainActivity.this,
                                            Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                return;
                            }
                            fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(MainActivity.this);
                            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        Snackbar.make(coordinator,"Permission Denied",Snackbar.LENGTH_LONG).show();
                    }
                }).check();

        buildLocationRequest();
        buildLocationCallback();*/
        //getWeather();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEvent(MessageEvent event) {
        Log.d("chosenCity",event.getCityName());
        Log.d("chosenCity",String.valueOf(event.getCityIndex()));

        if(event.getCityName().equals("+")){

        }else {
            getWeatherByName(event.getCityName());
            getForecastWeatherInfoByCityName(event.getCityName());
        }
    }

}
