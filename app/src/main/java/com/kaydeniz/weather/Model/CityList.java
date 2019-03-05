package com.kaydeniz.weather.Model;

public class CityList {

    private int id;
    private String name;
    private String countr;
    private Coord coord;

    public CityList() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountr() {
        return countr;
    }

    public void setCountr(String countr) {
        this.countr = countr;
    }

    public Coord getCoord() {
        return coord;
    }

    public void setCoord(Coord coord) {
        this.coord = coord;
    }
}
