package com.sidera.meetsfood.utils;

import android.util.Log;

import java.io.IOException;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;


/**
 * Created by elisa.schir on 19/10/2016.
 */
public class UserAgentInterceptor implements Interceptor {

    private final String userAgent;

    public UserAgentInterceptor(String userAgent) {
        this.userAgent = userAgent;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();

        Request originRequest = chain.request();
        Request.Builder requestBuilder = originRequest.newBuilder()
                .addHeader("User-Agent", userAgent);

        Request requestWithUserAgent = requestBuilder.method(original.method(), original.body()).build();

        Log.e("USER-agent",requestWithUserAgent.header("User-Agent"));
        return chain.proceed(requestWithUserAgent);
    }
}
