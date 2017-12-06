package ru.pozitivtelecom.cabinet.soap;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class JsonClass {
    static public Map<String,Object> json2map(String jsonstring)
    {
        Gson gson2 = new Gson();
        Map<String,Object> map = new HashMap<>();
        map = (Map<String,Object>) gson2.fromJson(jsonstring, map.getClass());
        return map;
    }
}
