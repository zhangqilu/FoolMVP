package com.ljj.foolmvp.appcomm.network;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by lijunjie on 2017/12/21.
 */
public class HostType {
    /**
     * 多少种Host类型
     */
    public static final int TYPE_COUNT = 2;

    /**
     * wemeet的host
     */
    public static final int WEMEET_BASE_HOST = 1;

    public static final int WEMEET_WS_HOST = 2;

    public static final String BASE_HOST = "base_host";


    /**
     * 替代枚举的方案，使用IntDef保证类型安全
     */
    @IntDef({WEMEET_BASE_HOST})
    @Retention(RetentionPolicy.SOURCE)
    public @interface HostTypeChecker {

    }
}
