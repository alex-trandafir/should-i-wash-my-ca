package com.example.alex.shouldiwashmycarr.clients;

import java.util.List;

/**
 * Created by alex on 26/04/2017.
 */

public class WeatherData {
    private String cod;
    private City city;
    private List<WeatherDay> list;


    public String getCod() {
        return cod;
    }

    public void setCod(String cod) {
        this.cod = cod;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public List<WeatherDay> getList() {
        return list;
    }

    public void setList(List<WeatherDay> list) {
        this.list = list;
    }
}
