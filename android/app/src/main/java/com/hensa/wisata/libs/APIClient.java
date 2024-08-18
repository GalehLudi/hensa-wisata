package com.hensa.wisata.libs;

import android.content.Context;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIClient {
    private final Retrofit retrofit;
    private final Context context;

    public APIClient(Context context) {
        this.context = context;

        retrofit = new Retrofit.Builder()
                .baseUrl(API.API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client())
                .build();
    }

    public APIService getService() {
        return retrofit.create(APIService.class);
    }

    private OkHttpClient client() {
        return new OkHttpClient.Builder()
                .addInterceptor(new AuthInterceptor(this.context))
                .build();
    }

    private static class AuthInterceptor implements Interceptor {
        private final Context context;

        public AuthInterceptor(Context context) {
            this.context = context;
        }

        @NonNull
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request.Builder builder = chain.request().newBuilder();
            builder.addHeader("Content-Type", "application/json");
            builder.addHeader("Authorization", "Bearer " + new SessionManager(this.context).getToken());
            return chain.proceed(builder.build());
        }
    }
}
