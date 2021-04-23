package io.cryptem.app.ui

import dagger.hilt.android.lifecycle.HiltViewModel
import io.cryptem.app.model.HomeScreen
import io.cryptem.app.model.SharedPrefsRepository
import io.cryptem.app.ui.base.BaseVM
import javax.inject.Inject

@HiltViewModel
class MainActivityVM @Inject constructor(val prefs : SharedPrefsRepository) : BaseVM() {

    fun getHomeScreen() : HomeScreen{
        return prefs.getHomeScreen()
    }
}