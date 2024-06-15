//TODO(hilt관련 불필요한 파일삭제)
//package com.bestapp.zipbab.data.network
//
//import com.bestapp.zipbab.data.BuildConfig
//import com.bestapp.zipbab.data.notification.setup.KaKaoService
//import com.bestapp.zipbab.data.notification.setup.NoTiInterceptor
//import okhttp3.OkHttpClient
//import com.squareup.moshi.Moshi
//import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
//import retrofit2.converter.moshi.MoshiConverterFactory
//import retrofit2.Retrofit
//
//object RetrofitClient {
//    private val okHttpClient by lazy {
//        OkHttpClient.Builder()
//            .addInterceptor(AuthInterceptor())
//            .build()
//    }
//
//    private val noTiOkHttpClient by lazy {
//        OkHttpClient.Builder()
//            .addInterceptor(NoTiInterceptor())
//            .build()
//    }
//
//    private val moshi = Moshi.Builder()
//        // add -> addLast :
//        .addLast(KotlinJsonAdapterFactory())
//        .build()
//
//    private val retrofitKakaoMap = Retrofit.Builder()
//            .baseUrl(BuildConfig.KAKAO_MAP_BASE_URL)
//            .client(okHttpClient)
//            .addConverterFactory(MoshiConverterFactory.create(moshi))
//            .build()
//
//    private val retrofitKakaoNotification = Retrofit.Builder()
//            .baseUrl(BuildConfig.KAKAO_NOTIFY_BASE_URL)
//            .client(noTiOkHttpClient)
//            .addConverterFactory(MoshiConverterFactory.create(moshi))
//            .build()
//
//    val searchLocationService: SearchLocationService =
//        retrofitKakaoMap.create(SearchLocationService::class.java)
//
//    val notifyService : KaKaoService =
//        retrofitKakaoNotification.create(KaKaoService::class.java)
//
//    // TODO: 카카오 Notification API 호출에 필요한 Repository로 변경해야함
////    val notifyService: AppSettingRepository by lazy {
////        retrofitKakaoNotification.create(AppSettingRepository::class.java)
////    }
//}