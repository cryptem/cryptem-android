package io.cryptem.app.ui.base

import android.view.MenuItem
import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding
import kodebase.view.KodebaseFragment

abstract class BaseFragment<VM : BaseVM, B : ViewDataBinding>(@LayoutRes layoutRes : Int) : KodebaseFragment<VM, B>(layoutRes) {


}