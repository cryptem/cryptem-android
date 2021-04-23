package kodebase.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.annotation.ColorRes
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import kodebase.BR
import kodebase.R
import kodebase.event.LiveEvent
import kodebase.event.NavigationEvent
import kodebase.extensions.navigate
import kodebase.viewmodel.KodebaseViewModel
import kotlin.reflect.KClass

/**
 * Handcrafted by Stepan Sonsky
 * Apache 2.0 License
 * https://github.com/kodebase-android
 */

abstract class KodebaseActivity<out VM : KodebaseViewModel, B : ViewDataBinding>(@LayoutRes var layoutId: Int) :
    AppCompatActivity() {

    abstract val viewModel: VM
    lateinit var binding: B

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycle.addObserver(viewModel)
        binding = DataBindingUtil.setContentView(this, layoutId)
        binding.lifecycleOwner = this
        binding.setVariable(BR.lifecycle, this)
        binding.setVariable(BR.vm, viewModel)

        observeEvent(NavigationEvent::class) { event ->
            navController().navigate(event)
        }
    }

    protected fun <T : LiveEvent> observeEvent(eventClass: KClass<T>, eventObserver: Observer<T>) {
        viewModel.observe(this, eventClass, eventObserver)
    }

    override fun onDestroy() {
        lifecycle.removeObserver(viewModel)
        super.onDestroy()
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

    protected fun navigate(
        @IdRes resId: Int,
        args: Bundle? = null,
        navOptions: NavOptions? = null
    ) {
        navController().navigate(resId, args, navOptions)
    }

    protected fun navigate(directions: NavDirections, navOptions: NavOptions? = null) {
        navController().navigate(directions, navOptions)
    }

    protected fun navController(): NavController {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        return navHostFragment.navController
    }

    protected fun getColorCompat(@ColorRes id: Int) : Int{
        return ResourcesCompat.getColor(resources, id, null)
    }

    override fun onSupportNavigateUp(): Boolean {
        hideKeyboard()
        return navController().navigateUp()
    }

    protected fun hideKeyboard(){
        val imm: InputMethodManager =getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }
}
