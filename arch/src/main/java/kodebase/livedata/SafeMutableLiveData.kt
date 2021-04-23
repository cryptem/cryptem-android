package kodebase.livedata

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

/**
 * Handcrafted by Stepan Sonsky
 * Apache 2.0 License
 * https://github.com/kodebase-android
 */

class SafeMutableLiveData<T : Any>(initValue: T) : MutableLiveData<T>() {

    init {
        value = initValue
    }

    override fun getValue(): T {
        return super.getValue()!!
    }

    override fun setValue(value: T) {
        super.setValue(value)
    }

    fun observe(owner: LifecycleOwner, body: (T) -> Unit) {
        observe(owner, Observer<T> { t -> body(t!!) })
    }

    override fun postValue(value: T) {
        super.postValue(value)
    }
}