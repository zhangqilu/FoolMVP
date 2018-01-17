package com.ljj.foolmvp.core;

import android.support.annotation.NonNull;

/**
 * Created by lijunjie on 2017/12/27.
 *
 * Presenter的接口类，主要有三个生命周期和presenter唯一标识
 */

public interface BasePresenter {

    /**
     *Presenter的入口，可做初始化操作
     */
    void onCreate();

    /**
     * 绑定BaseView
     * @param view
     */
    void attachView(@NonNull BaseView view);

    /**
     * 销毁Presenter
     */
    void onDestory();

    /**
     * 获得Presenter唯一标识
     * @return
     */
    int getPresenterId();
}
