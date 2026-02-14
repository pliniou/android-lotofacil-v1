package com.cebolao.lotofacil.di

import android.content.Context
import com.cebolao.lotofacil.BuildConfig
import com.cebolao.lotofacil.core.security.RateLimiter
import com.cebolao.lotofacil.data.network.RateLimiterInterceptor
import com.cebolao.lotofacil.data.network.ApiService
import com.cebolao.lotofacil.data.network.HerokuApiService
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.Cache
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val HEROKU_BASE_URL = "https://loteriascaixa-api.herokuapp.com/api/lotofacil/"
    private const val CAIXA_BASE_URL = "https://servicebus2.caixa.gov.br/portaldeloterias/api/lotofacil/"
    private const val CACHE_SIZE_BYTES = 50 * 1_024 * 1_024L // 50 MB

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    @Provides
    @Singleton
    fun provideHttpCache(@ApplicationContext context: Context): Cache {
        val cacheDir = File(context.cacheDir, "http_cache")
        return Cache(cacheDir, CACHE_SIZE_BYTES)
    }

    @Provides
    @Singleton
    fun provideRateLimiter(): RateLimiter = RateLimiter.createLenient()

    @Provides
    @Singleton
    fun provideOkHttpClient(
        cache: Cache,
        rateLimiter: RateLimiter
    ): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
            else HttpLoggingInterceptor.Level.NONE
        }
        
        // Custom trust manager might be needed for Caixa if they use gov certs not in standard truststore,
        // but for now we try standard. If SSL fails, we might need a custom SSLContext.
        
        return OkHttpClient.Builder()
            .cache(cache)
            .addInterceptor(RateLimiterInterceptor(rateLimiter))
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .header("Accept-Encoding", "gzip")
                    .build()
                chain.proceed(request)
            }
            .addNetworkInterceptor { chain ->
                val response = chain.proceed(chain.request())
                val hasExplicitCacheHeader = !response.header("Cache-Control").isNullOrBlank()
                if (chain.request().method == "GET" && !hasExplicitCacheHeader) {
                    response.newBuilder()
                        .header("Cache-Control", "public, max-age=300, stale-while-revalidate=600")
                        .build()
                } else {
                    response
                }
            }
            .addInterceptor(logging)
            .retryOnConnectionFailure(true)
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .callTimeout(20, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    @Named("HerokuRetrofit")
    fun provideHerokuRetrofit(okHttpClient: OkHttpClient, json: Json): Retrofit {
        val contentType = "application/json".toMediaType()
        return Retrofit.Builder()
            .baseUrl(HEROKU_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }

    @Provides
    @Singleton
    @Named("CaixaRetrofit")
    fun provideCaixaRetrofit(okHttpClient: OkHttpClient, json: Json): Retrofit {
        val contentType = "application/json".toMediaType()
        return Retrofit.Builder()
            .baseUrl(CAIXA_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }

    @Provides
    @Singleton
    @Named("HerokuApi")
    fun provideHerokuApiService(@Named("HerokuRetrofit") retrofit: Retrofit): HerokuApiService =
        retrofit.create(HerokuApiService::class.java)

    @Provides
    @Singleton
    @Named("CaixaApi")
    fun provideCaixaApiService(@Named("CaixaRetrofit") retrofit: Retrofit): ApiService =
        retrofit.create(ApiService::class.java)
        
    // Default provider for backward compatibility if needed, though we should migrate users
    @Provides
    @Singleton
    fun provideDefaultApiService(@Named("HerokuApi") apiService: HerokuApiService): HerokuApiService = apiService
}
