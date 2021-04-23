package io.cryptem.app.model

class ListCache<T>(private val timeoutMinutes : Long, private val funLoad : suspend () -> List<T>){
    private var updated : Long = 0
    private var value : List<T>? = null

    get() {
        return if (!isExpired()){
            field
        } else {
            value = null
            null
        }
    }

    suspend fun get(force : Boolean = false) : List<T>{
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
}