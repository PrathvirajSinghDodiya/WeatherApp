package com.example.weatherapp;

public class WeatherModel {
    String time;
    String temp;
    String icon;
    String windSpeed;
    int day_night;

    public int getDay_night() {
        return day_night;
    }

    public void setDay_night(int day_night) {
        this.day_night = day_night;
    }

    public WeatherModel(String time, String temp, String icon, String windSpeed, int day_night) {
        this.time = time;
        this.temp = temp;
        this.icon = icon;
        this.windSpeed = windSpeed;
        this.day_night=day_night;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(String windSpeed) {
        this.windSpeed = windSpeed;
    }
}
