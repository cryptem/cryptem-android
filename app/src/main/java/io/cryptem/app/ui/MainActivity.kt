package io.cryptem.app.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import dagger.hilt.android.AndroidEntryPoint
import io.cryptem.app.R
import io.cryptem.app.databinding.ActivityMainBinding
import io.cryptem.app.model.HomeScreen
import kodebase.view.KodebaseActivity
import java.util.*
import kotlin.collections.HashSet

@AndroidEntryPoint
class MainActivity : KodebaseActivity<MainActivityVM, ActivityMainBinding>(R.layout.activity_main) {
    override val viewModel: MainActivityVM by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.navBar.setupWithNavController(navController())

        setSupportActionBar(binding.toolbar)

        val navGraph = navController().navInflater.inflate(R.navigation.nav_graph)
        navGraph.startDestination = viewModel.getHomeScreen().id

        navController().graph = navGraph

        val appBarConfiguration = AppBarConfiguration(HashSet<Int>().apply {
            add(R.id.marketFragment)
            add(R.id.portfolioFragment)
            add(R.id.mapFragment)
            add(R.id.payFragment)
            add(R.id.requestFragment)
        })

        NavigationUI.setupActionBarWithNavController(this, navController(), appBarConfiguration)
    }
}