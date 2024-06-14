//TODO(hilt관련 불필요한 파일삭제)
//package com.bestapp.rice.data.notification.setup
//
//import com.bestapp.rice.data.BuildConfig
//import okhttp3.Interceptor
//import okhttp3.Request
//import okhttp3.Response
//
//class NoTiInterceptor : Interceptor {
//    override fun intercept(chain: Interceptor.Chain): Response {
//        val newRequest: Request = chain.request().newBuilder()
//            .addHeader(
//                "Authorization",
//                "KakaoAK ${BuildConfig.KAKAO_ADMIN_KEY}"
//            ).build()
//
//        return chain.proceed(newRequest)
//    }
//}