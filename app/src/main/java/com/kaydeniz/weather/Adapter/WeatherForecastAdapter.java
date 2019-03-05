package com.kaydeniz.weather.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kaydeniz.weather.Model.WeatherForecastResult;
import com.kaydeniz.weather.OWM;
import com.kaydeniz.weather.R;
import com.squareup.picasso.Picasso;

public class WeatherForecastAdapter extends RecyclerView.Adapter<WeatherForecastAdapter.MyViewHolder> {

    Context context;
    WeatherForecastResult weatherForecastResult;

    public WeatherForecastAdapter(Context context, WeatherForecastResult weatherForecastResult) {
        this.context = context;
        this.weatherForecastResult = weatherForecastResult;
    }

    @NonNull
    @Override
    public WeatherForecastAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView= LayoutInflater.from(context).inflate(R.layout.item_weather_forecast,parent,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherForecastAdapter.MyViewHolder holder, int position) {

        Picasso.get().load(new StringBuilder("https://openweathermap.org/img/w/").
                append(weatherForecastResult.list.get(position).weather.get(0).getIcon()).append(".png").toString()).into(holder.weatherImage);

        holder.tvDate.setText(new StringBuilder(OWM.convertUnixToDate(weatherForecastResult.list.get(position).dt)));
        holder.tvDescp.setText(new StringBuilder(weatherForecastResult.list.get(position).weather.get(0).getDescription()));
        holder.tvTemp.setText(new StringBuilder(String.valueOf(weatherForecastResult.list.get(position).main.getTemp())).append("°C"));



    }

    @Override
    public int getItemCount() {
        return weatherForecastResult.list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvDate,tvTemp,tvDescp;
        ImageView weatherImage;

        public MyViewHolder(View itemView) {
            super(itemView);

            tvDate=itemView.findViewById(R.id.tvDate);
            tvTemp=itemView.findViewById(R.id.tvTemp);
            tvDescp=itemView.findViewById(R.id.tvDescp);
            weatherImage=itemView.findViewById(R.id.weatherImage);

        }
    }
}
