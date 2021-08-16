package io.cryptem.app.ui.base

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding
import kodebase.view.KodebaseFragment

abstract class BaseFragment<VM : BaseVM, B : ViewDataBinding>(@LayoutRes layoutRes: Int) :
    KodebaseFragment<VM, B>(layoutRes) {

    protected fun showUrl(url: String) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }

    protected fun runOrInstall(packageName: String){
        try {
            runApp(packageName)
        } catch (t : Throwable){
            installApp(packageName)
        }
    }

    protected fun runApp(packageName: String) {
        val intent = requireContext().packageManager.getLaunchIntentForPackage(packageName)
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        } else {
            throw ActivityNotFoundException()
        }
    }

    protected fun installApp(packageName: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.data = Uri.parse("market://details?id=$packageName")
        startActivity(intent)
    }

}