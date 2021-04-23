package io.cryptem.app.di

import android.content.Context
import androidx.room.Room
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.cryptem.app.BuildConfig
import io.cryptem.app.model.api.CryptemApiDef
import io.cryptem.app.model.coingecko.CoinGeckoApiDef
import io.cryptem.app.model.db.PortfolioDatabase
import io.cryptem.app.model.db.WalletDatabase
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideFusedLocationClient(@ApplicationContext context: Context): FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(context)
    }

    @Provides
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient().newBuilder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideApi(httpClient: OkHttpClient): CryptemApiDef {
        return Retrofit.Builder()
            .client(httpClient)
            .baseUrl(BuildConfig.API_URL)
            .addConverterFactory(
                MoshiConverterFactory.create(
                    Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
                )
            )
            .build()
            .create(CryptemApiDef::class.java)
    }

    @Provides
    @Singleton
    fun provideCoinGeckoApi(httpClient: OkHttpClient): CoinGeckoApiDef {
        return Retrofit.Builder()
            .client(httpClient)
            .baseUrl("https://api.coingecko.com/")
            .addConverterFactory(
                MoshiConverterFactory.create(
                    Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
                )
            )
            .build()
            .create(CoinGeckoApiDef::class.java)
    }

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().setLevel(if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE)
    }

    @Provides
    @Singleton
    fun providePortfolioDatabase(@ApplicationContext context: Context): PortfolioDatabase {
        return Room.databaseBuilder(
            context,
            PortfolioDatabase::class.java, "portfolio"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideWalletDatabase(@ApplicationContext context: Context): WalletDatabase {
        return Room.databaseBuilder(
            context,
            WalletDatabase::class.java, "wallet"
        ).fallbackToDestructiveMigration().build()
    }
}