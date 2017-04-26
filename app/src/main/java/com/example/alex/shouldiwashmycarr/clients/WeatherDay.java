package com.example.alex.shouldiwashmycarr.clients;

import java.util.List;

/**
 * Created by alex on 26/04/2017.
 */

public class WeatherDay {
    private String dt_txt;
    private List<Weather> weather;


    public String getDt_txt() {
        return dt_txt;
    }

    public void setDt_txt(String dt_txt) {
        this.dt_txt = dt_txt;
    }

    public List<Weather> getWeather() {
        return weather;
    }

    public void setWeather(List<Weather> weather) {
        this.weather = weather;
    }
}
