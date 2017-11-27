package ru.pozitivtelecom.cabinet.models;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;

public class UsersCabinetDataModel {
    public String Token;
    public String PersonID;
    public String PersonFIO;
    public String PersonFIOShort;
    public List<UsersDataModel> Users;
}



