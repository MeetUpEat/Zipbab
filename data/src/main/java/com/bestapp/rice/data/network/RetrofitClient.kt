package com.bestapp.rice.data.network

import com.bestapp.rice.data.BuildConfig
import com.bestapp.rice.data.repository.AppSettingRepository
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
        .add(KotlinJsonAdapterFactory())
        .build()

    private val retrofitKakaoMap by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.KAKAO_MAP_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    private val retrofitKakaoNotification by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.KAKAO_NOTIFY_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

//    // TODO: 카카오 맵 API 호출에 필요한 Datasource로 변경해야함
//    val mapService: AppSettingRepository by lazy {
//        retrofitKakaoMap.create(AppSettingDatasource::class.java)
//    }
//
//    // TODO: 카카오 Notification API 호출에 필요한 Datasource로 변경해야함
//    val notifyService: AppSettingRepository by lazy {
//        retrofitKakaoNotification.create(AppSettingDatasource::class.java)
//    }
}