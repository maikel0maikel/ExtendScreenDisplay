package com.sinohb.extendscreen.display;


public interface BaseView<T extends BasePresenter> {
    void setPresenter(T presenter);
}
