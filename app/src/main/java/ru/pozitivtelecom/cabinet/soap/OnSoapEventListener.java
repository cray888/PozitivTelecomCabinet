package ru.pozitivtelecom.cabinet.soap;

import java.util.EventListener;

public interface OnSoapEventListener extends EventListener {
    void onChangeState(int state, String message);
    void onComplete(String Result);
    void onError(String Result);
}
