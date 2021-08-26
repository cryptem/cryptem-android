package io.cryptem.app.model

class Cache<T>(private val timeoutMinutes : Int, private val funLoad : suspend () -> T){
    private var updated : Long = 0
    private var value : T? = null
    get() {
        return if (!isExpired()){
            field
        } else {
            value = null
            null
        }
    }

    suspend fun get(force : Boolean = false) : T{
        if (force){
            clear()
        }
        return value ?: funLoad.invoke().also {
            value = it
            updated = System.currentTimeMillis()
        }
    }

    fun clear(){
        value = null
        updated = 0
    }

    private fun isExpired() : Boolean{
        return System.currentTimeMillis() - updated > (timeoutMinutes * 60000)
    }

    fun hasData() : Boolean{
        return value != null
    }
}