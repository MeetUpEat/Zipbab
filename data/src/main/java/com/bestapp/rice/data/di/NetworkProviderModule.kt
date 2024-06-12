package com.bestapp.rice.data.di

import com.bestapp.rice.data.BuildConfig
import com.bestapp.rice.data.network.SearchLocationService
import com.bestapp.rice.data.notification.setup.KaKaoService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object NetworkProviderModule {

    private const val KEY_NAME = "Authorization"
    private const val KEY = "KakaoAK"


    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class KakaoMapRetrofit

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class KakaoNotificationRetrofit

    @Provides
    @Singleton
    fun provideKakaoOkHttpClient(
        customInterceptor: CustomInterceptor
    ): OkHttpClient {
        val logInterceptor = HttpLoggingInterceptor()
        logInterceptor.level = HttpLoggingInterceptor.Level.BODY
        return OkHttpClient.Builder()
            .addInterceptor(logInterceptor)
            .addInterceptor(customInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()
    }

    @KakaoMapRetrofit
    @Provides
    @Singleton
    fun provideKakaoMapRetrofit(
        okHttpClient: OkHttpClient,
        moshi: Moshi
    ): SearchLocationService {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.KAKAO_MAP_BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(okHttpClient)
            .build().create(SearchLocationService::class.java)
    }


    @KakaoNotificationRetrofit
    @Provides
    @Singleton
    fun provideKaKaoNotifyRetrofit(
        okHttpClient: OkHttpClient,
        moshi: Moshi
    ): KaKaoService {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.KAKAO_NOTIFY_BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(okHttpClient)
            .build().create(KaKaoService::class.java)
    }



    class CustomInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val url = chain.request().url.toUri().toString()

            val (name: String, key: String) = when {
                url.contains(BuildConfig.KAKAO_MAP_BASE_URL) -> Pair(
                    KEY_NAME,
                    String.format("%d %d", KEY, BuildConfig.KAKAO_NATIVE_KEY)

                )

                url.contains(BuildConfig.KAKAO_NOTIFY_BASE_URL) -> Pair(
                    KEY_NAME,
                    String.format("%d %d", KEY, BuildConfig.KAKAO_REST_API_KEY)
                )

                else -> {
                    return chain.proceed(chain.request())
                }
            }

            return with(chain) {
                val newRequest = request().newBuilder()
                    .addHeader(name, key)
                    .build()
                proceed(newRequest)
            }
        }
    }
}

