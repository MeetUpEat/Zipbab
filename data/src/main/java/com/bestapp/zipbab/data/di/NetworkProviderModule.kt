package com.bestapp.zipbab.data.di

import com.bestapp.zipbab.data.BuildConfig
import com.bestapp.zipbab.data.network.SearchLocationService
import com.bestapp.zipbab.data.notification.setup.GooGleRefreshService
import com.bestapp.zipbab.data.notification.setup.GooGleService
import com.bestapp.zipbab.data.notification.setup.KaKaoService
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
    private const val FCM_KEY = "Bearer"


    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class KakaoMapRetrofit

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class KakaoNotificationRetrofit

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class GoogleTokenProvider

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class GoogleRefreshTokenProvider

    @Provides
    @Singleton
    fun provideCustomInterceptor(): CustomInterceptor {
        return CustomInterceptor()
    }

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

    @GoogleTokenProvider
    @Provides
    @Singleton
    fun provideGooGleToken(
        okHttpClient: OkHttpClient,
        moshi: Moshi
    ) : GooGleService {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.GOOGLE_TOKEN_BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(okHttpClient)
            .build().create(GooGleService::class.java)
    }

    @GoogleRefreshTokenProvider
    @Provides
    @Singleton
    fun provideGooGleRefreshToken(
        okHttpClient: OkHttpClient,
        moshi: Moshi
    ) : GooGleRefreshService {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.GOOGLE_REFRESH_BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(okHttpClient)
            .build().create(GooGleRefreshService::class.java)
    }


    class CustomInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val url = chain.request().url.toUri().toString()

            val (name: String, key: String) = when {
                url.contains(BuildConfig.KAKAO_MAP_BASE_URL) -> Pair(
                    KEY_NAME,
                    String.format("%s %s", KEY, BuildConfig.KAKAO_REST_API_KEY)

                )

//                url.contains(BuildConfig.KAKAO_NOTIFY_BASE_URL) -> {
//
//                    Pair(KEY_NAME, String.format("%s %s", FCM_KEY, BuildConfig.KAKAO_ADMIN_KEY))
//                }

//                url.contains(BuildConfig.GOOGLE_TOKEN_BASE_URL) -> Pair(
//                    "",
//                    String.format("%s %s", "", "")
//                )

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

//    class TokenAuthenticator @Inject constructor (
//        @ApplicationContext private val context: Context
//    ) : Authenticator {
//        private val prefer by lazy { SharedPreference(context) }
//        private var accessToken : String? = ""
//        override fun authenticate(route: Route?, response: Response): Request? {
//            return runBlocking {
//                refreshToken()
//
//                val request = buildRequest(response.request.newBuilder())
//
//                // Return null to stop retrying once responseCount returns 3 or above.
//                if (responseCount(response) >= 3) {
//                    null
//                } else request
//            }
//        }
//
//        private fun refreshToken() {
//            accessToken = prefer.loadData()
//        }
//
//
//        private fun buildRequest(requestBuilder: Request.Builder): Request {
//            return requestBuilder
//                .header("Content-Type", "application/json")
//                .header(KEY_NAME, FCM_KEY + accessToken)
//                .build()
//        }
//
//        private fun responseCount(response: Response?): Int {
//            var result = 1
//            while (response?.priorResponse != null && result <= 3) {
//                result++
//            }
//            return result
//        }
//    }
}

