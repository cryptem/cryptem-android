package io.cryptem.app.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import dagger.hilt.android.AndroidEntryPoint
import io.cryptem.app.R
import io.cryptem.app.databinding.ActivityMainBinding
import io.cryptem.app.ui.poieditor.PoiEditorFragmentArgs
import kodebase.view.KodebaseActivity

@AndroidEntryPoint
class MainActivity : KodebaseActivity<MainActivityVM, ActivityMainBinding>(R.layout.activity_main) {
    override val viewModel: MainActivityVM by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.navBar.setupWithNavController(navController())

        setSupportActionBar(binding.toolbar)

        val navGraph = navController().navInflater.inflate(R.navigation.nav_graph)
        navGraph.setStartDestination(viewModel.getHomeScreen().id)

        navController().graph = navGraph

        val appBarConfiguration = AppBarConfiguration(HashSet<Int>().apply {
            add(R.id.marketFragment)
            add(R.id.portfolioFragment)
            add(R.id.mapFragment)
            add(R.id.payFragment)
            add(R.id.requestFragment)
        })

        NavigationUI.setupActionBarWithNavController(this, navController(), appBarConfiguration)
        handleIntent(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_about -> {
                navigate(R.id.aboutFragment)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        val data = intent?.clipData?.getItemAt(0)?.text.toString()
        if (data.contains("maps.app.goo.gl")) {
            val lines = data.split("\n")
            val name = lines[0]
            val url = lines.find { it.startsWith("https://") }
            if (navController().currentDestination?.id == R.id.poiEditorFragment) {
                navController().popBackStack()
            }
            navigate(R.id.poiEditorFragment, PoiEditorFragmentArgs(name, url).toBundle())
        }
    }
}