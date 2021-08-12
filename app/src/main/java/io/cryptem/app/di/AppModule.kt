package io.cryptem.app.di

import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Vibrator
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
import io.cryptem.app.model.SharedPrefsRepository
import io.cryptem.app.model.api.MoshiDateAdapter
import io.cryptem.app.model.binance.BinanceApiDef
import io.cryptem.app.model.binance.BinanceInterceptor
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
    fun provideCoinGeckoApi(httpClient: OkHttpClient): CoinGeckoApiDef {
        return Retrofit.Builder()
            .client(httpClient)
            .baseUrl("https://api.coingecko.com/")
            .addConverterFactory(
                MoshiConverterFactory.create(
                    Moshi.Builder().add(KotlinJsonAdapterFactory())
                        .add(MoshiDateAdapter()).build()
                )
            )
            .build()
            .create(CoinGeckoApiDef::class.java)
    }

    @Provides
    @Singleton
    fun provideBinanceApi(loggingInterceptor : HttpLoggingInterceptor, binanceInterceptor: BinanceInterceptor): BinanceApiDef {
        val httpClient = OkHttpClient().newBuilder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(binanceInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()

        return Retrofit.Builder()
            .client(httpClient)
            .baseUrl("https://api.binance.com/")
            .addConverterFactory(
                MoshiConverterFactory.create(
                    Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
                )
            )
            .build()
            .create(BinanceApiDef::class.java)
    }

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().setLevel(if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE)
    }

    @Provides
    @Singleton
    fun provideBinanceInterceptor(prefs : SharedPrefsRepository): BinanceInterceptor {
        return BinanceInterceptor(prefs)
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

    @Provides
    @Singleton
    fun providesGeoceoder(@ApplicationContext context: Context): Geocoder {
        return Geocoder(context)
    }

    @Provides
    @Singleton
    fun providesClipboardManager(@ApplicationContext context: Context): ClipboardManager {
        return context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    }

    @Provides
    @Singleton
    fun providesPackageManager(@ApplicationContext context: Context): PackageManager {
        return context.packageManager
    }

    @Provides
    @Singleton
    fun provideVibrator(@ApplicationContext context: Context): Vibrator {
        return context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }
}