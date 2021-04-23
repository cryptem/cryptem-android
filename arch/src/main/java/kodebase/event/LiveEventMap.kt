package kodebase.event

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import java.util.*
import kotlin.reflect.KClass

/**
 * Handcrafted by Stepan Sonsky
 * Apache 2.0 License
 * https://github.com/kodebase-android
 */

class LiveEventMap {

    private val events = HashMap<KClass<out LiveEvent>, SingleLiveEvent<out LiveEvent>>()

    fun <T : LiveEvent> observe(lifecycleOwner: LifecycleOwner, eventClass: KClass<T>, eventObserver: Observer<T>) {
        var liveEvent: SingleLiveEvent<T>? = events[eventClass] as SingleLiveEvent<T>?
        if (liveEvent == null) {
            liveEvent = initUiEvent(eventClass)
        }
        liveEvent.observe(lifecycleOwner, eventObserver)
    }


    fun <T : LiveEvent> publish(event: T) {
        var liveEvent: SingleLiveEvent<T>? = events[event::class] as SingleLiveEvent<T>?
        if (liveEvent == null) {
            liveEvent = initUiEvent(event::class)
        }
        liveEvent.postValue(event)
    }

    private fun <T : LiveEvent> initUiEvent(eventClass: KClass<out T>): SingleLiveEvent<T> {
        val liveEvent = SingleLiveEvent<T>()
        events[eventClass] = liveEvent
        return liveEvent
    }

}
