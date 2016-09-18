package com.kodekinopoisknasaev;


import java.util.HashMap;

public class DataClasses {

    static public class City extends HashMap<String, String> {

        public static final String cityName = "cityName";
        public static final String cityID = "cityID";

        public City (String name, String id) {
            super();
            super.put(cityName, name);
            super.put(cityID, id);
        }
    }

    static public class Film extends HashMap<String,String>{
        public static final String filmNameRU = "filmNameRU";
        public static final String filmNameEN = "filmNameEN";
        public static final String filmID = "filmID";
        public static final String filmGenre = "filmGenre";
        public static final String filmRating = "filmRating";

        public Film (String nameRU, String nameEN, String id, String genre, String rating) {
            super();
            super.put(filmNameRU, nameRU);
            super.put(filmNameEN, nameEN);
            super.put(filmID, id);
            super.put(filmGenre, genre);
            super.put(filmRating, rating);
        }
    }

    static public class Genre extends HashMap<String, String> {

        public static final String genreName = "genreName";
        public static final String genreID = "cityID";

        public Genre (String name, String id) {
            super();
            super.put(genreName, name);
            super.put(genreID, id);
        }
    }

    static public class Cinema extends HashMap<String, String> {
        public static final String cinemaName = "cinemaName";
        public static final String cinemaID = "cinemaID";
        public static final String cinemaAddress = "cinemaAddress";
        public static final String cinemaLon = "cinemaLon";
        public static final String cinemaLat = "cinemaLat";

        public Cinema(String name, String id, String address, String lon, String lat) {
            super();
            super.put(cinemaName, name);
            super.put(cinemaID, id);
            super.put(cinemaAddress, address);
            super.put(cinemaLat, lat);
            super.put(cinemaLon, lon);
        }
    }
}