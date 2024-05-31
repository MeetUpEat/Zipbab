package com.bestapp.rice.data.network

import com.bestapp.rice.data.BuildConfig
import okhttp3.OkHttpClient
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.Retrofit

object RetrofitClient {
    private val okHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor())
            .build()
    }

    private val moshi = Moshi.Builder()
        // add -> addLast :
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val retrofitKakaoMap = Retrofit.Builder()
            .baseUrl(BuildConfig.KAKAO_MAP_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

    private val retrofitKakaoNotification = Retrofit.Builder()
            .baseUrl(BuildConfig.KAKAO_NOTIFY_BASE_URL)
            // .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

    val searchAddress: SearchLocationService =
        retrofitKakaoMap.create(SearchLocationService::class.java)

    // TODO: 카카오 Notification API 호출에 필요한 Repository로 변경해야함
//    val notifyService: AppSettingRepository by lazy {
//        retrofitKakaoNotification.create(AppSettingRepository::class.java)
//    }
}