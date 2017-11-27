package ru.pozitivtelecom.cabinet.soap;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import ru.pozitivtelecom.cabinet.models.UsersCabinetDataModel;

public class JsonClass {
    static public Map<String,Object> json2map(String jsonstring)
    {
        Gson gson2 = new Gson();
        Map<String,Object> map = new HashMap<String,Object>();
        map = (Map<String,Object>) gson2.fromJson(jsonstring, map.getClass());
        return map;
    }

    /*static public UsersCabinetDataModel getDataFromJson(String json)
    {
        return new Gson().fromJson(json, UsersCabinetDataModel.class);
    }*/
}
