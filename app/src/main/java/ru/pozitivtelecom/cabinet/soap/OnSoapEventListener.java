package ru.pozitivtelecom.cabinet.soap;

import java.util.EventListener;

public interface OnSoapEventListener extends EventListener {

    public void onChangeState(int state, String message);
    public void onComplite(String Result);
    public void onError(String Result);

}
