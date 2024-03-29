package io.cryptem.app.model

class HashedCache<K, T>(
    private val timeoutMinutes: Int,
    private val funLoadItem: (suspend (K) -> T?),
    private val keyMapFun: (T) -> K
) {
    private val updatedMap = HashMap<K, Long>()
    private val map = HashMap<K, T>()

    suspend fun get(key: K, forceLoad: Boolean = false): T? {
        if (forceLoad) {
            map.remove(key)
            updatedMap.remove(key)
        }

        return if (isExpired(key)) {
            funLoadItem.invoke(key).also { item ->
                item?.let {
                    addToMap(it)
                }
            }
        } else {
            map[key]
        }
    }

    fun peek(key: K): T?{
        return map[key]
    }

    private fun addToMap(value: T) {
        val key = keyMapFun.invoke(value)
        map[key] = value
        updatedMap[key] = System.currentTimeMillis()
    }

    fun clear() {
        updatedMap.clear()
        map.clear()
    }

    private fun isExpired(key: K): Boolean {
        return updatedMap[key]?.let {
            System.currentTimeMillis() - it > (timeoutMinutes * 60000)
        } ?: true
    }

    fun put(it: T) {
        addToMap(it)
    }
}