package io.cryptem.app.model.binance

import io.cryptem.app.model.SharedPrefsRepository
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Response

class BinanceInterceptor(val prefs : SharedPrefsRepository) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val newRequest = request.newBuilder().apply {

            prefs.getBinanceSecretKey()?.let { secret ->
                val payload: String? = request.url.query
                payload?.let {
                    HmacUtil.hmac256(it, secret)?.let { signature ->
                        url(request.url.newBuilder().addQueryParameter("signature", signature).build())
                    }
                }
            }
            prefs.getBinanceApiKey()?.let {
                addHeader("X-MBX-APIKEY", it)
            }
        }.build()

        return chain.proceed(newRequest)
    }
}