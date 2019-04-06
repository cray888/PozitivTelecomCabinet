package ru.pozitivtelecom.cabinet.soap;

public interface OnSoapEventListener {
    void onChangeState(int state, String message);
    void onComplete(String Result);
    void onError(String Result);
}
