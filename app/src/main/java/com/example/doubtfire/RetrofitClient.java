package com.example.doubtfire;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static ApiInterface apiInterface;
    private static final String BASE_URL = "http://api.mathjs.org/";

    public static ApiInterface getClient() {
        if (apiInterface == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
//                    .client(getHttpClient())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            apiInterface = retrofit.create(ApiInterface.class);
        }
        return apiInterface;
    }
//    private static OkHttpClient getHttpClient() {
//        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
//        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
//        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
////        httpClient.connectTimeout(30, TimeUnit.SECONDS);
////        httpClient.readTimeout(30, TimeUnit.SECONDS);
////        httpClient.writeTimeout(30, TimeUnit.SECONDS);
//        httpClient.addInterceptor(logging);
//        httpClient.addInterceptor(new Interceptor() {
//            @Override
//            public Response intercept(Chain chain) throws IOException {
//                Request request = chain.request();
//                Request.Builder builder = request.newBuilder();
//
//
//                //for auth tokens
//                //String authToken = App.getInstance().getSessionManager().getX_AUTH_TOKEN();
//                //if (!StringUtils.isEmpty(authToken)) {
//                //    builder.addHeader("X-AUTH-TOKEN", authToken);
//                //}
//                request = builder.build();
//                return chain.proceed(request);
//            }
//        });
//        return httpClient.build();
//    }
}
