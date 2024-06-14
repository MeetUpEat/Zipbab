//TODO(hilt관련 불필요한 파일삭제)
//package com.bestapp.zipbab.data.network
//
//import com.bestapp.zipbab.data.BuildConfig
//import okhttp3.Interceptor
//import okhttp3.Request
//import okhttp3.Response
//
//class AuthInterceptor : Interceptor {
//    override fun intercept(chain: Interceptor.Chain): Response {
//        val newRequest: Request = chain.request().newBuilder()
//            .addHeader(
//                "Authorization",
//                "KakaoAK ${BuildConfig.KAKAO_REST_API_KEY}"
//            ).build()
//
//        return chain.proceed(newRequest)
//    }
//}