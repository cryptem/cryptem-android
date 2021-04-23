package kodebase.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import kodebase.BR
import kodebase.event.LiveEvent
import kodebase.event.NavigateUpEvent
import kodebase.event.NavigationEvent
import kodebase.extensions.navigate
import kodebase.viewmodel.KodebaseViewModel
import kotlin.reflect.KClass


/**
 * Handcrafted by Stepan Sonsky
 * Apache 2.0 License
 * https://github.com/kodebase-android
 */

abstract class KodebaseFragment<VM : KodebaseViewModel, B : ViewDataBinding>(
    @LayoutRes val layoutId: Int
) : Fragment() {

    abstract val viewModel: VM
    lateinit var binding : B
    open val collapsibleToolbar = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycle.addObserver(viewModel)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        binding.lifecycleOwner = this
        binding.setVariable(BR.lifecycle, this)
        binding.setVariable(BR.vm, viewModel)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observe(NavigationEvent::class) {
            navController().navigate(it)
        }
        observe(NavigateUpEvent::class){
            hideKeyboard()
            navController().navigateUp()
        }
    }

    override fun onDestroy() {
        lifecycle.removeObserver(viewModel)
        super.onDestroy()
    }

    protected fun <T : LiveEvent> observe(eventClass: KClass<T>, observer: Observer<T>){
        viewModel.observe(viewLifecycleOwner, eventClass, observer)
    }

    protected fun navController(): NavController {
        return NavHostFragment.findNavController(this)
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

    protected fun hideKeyboard(v : View? = null){
        v?.clearFocus()
        val imm: InputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow((v ?: binding.root).windowToken, 0)
    }

    protected fun showKeyboard(v : View? = null){
        v?.requestFocus()
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }


}
