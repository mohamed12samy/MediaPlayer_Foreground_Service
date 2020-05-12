package com.example.mediaplayer;

public interface Iinterface  {

    void clickListen(int position);
    void updateTitlesUI(int position,String Duration);
    void updateButtonUI(boolean playing);
    void setMax(int m);
    void setProgres(int progres);
    void updateDuration(int progress, int position);

}
