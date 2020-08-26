package bih.in.tarkariapp.webService;

import bih.in.tarkariapp.utility.AppConstant;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient
{
    private static Retrofit retrofit;
    private static final String BASE_URL = AppConstant.BASE_URL;
    public static Retrofit getRetrofitInstance()

    {
        if(retrofit == null)
        {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit;
    }
}
