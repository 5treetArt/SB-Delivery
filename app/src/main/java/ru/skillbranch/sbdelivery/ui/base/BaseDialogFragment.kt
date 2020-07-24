package ru.skillbranch.sbdelivery.ui.base

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import ru.skillbranch.sbdelivery.ui.RootActivity
import ru.skillbranch.sbdelivery.viewmodels.base.BaseViewModel
import ru.skillbranch.sbdelivery.viewmodels.base.IViewModelState

abstract class BaseDialogFragment<T : BaseViewModel<out IViewModelState>> : DialogFragment() {
    val root: RootActivity
        get() = activity as RootActivity
    open val binding: Binding? = null
    protected abstract val viewModel: T
    protected open val layout: Int? = null
    // Save your custom view at the class level
    private var customView: View? = null

    //set listeners, tuning views. In dialog this method can be empty
    open fun setupViews() {}

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Simply return the already inflated custom view
        return customView ?: super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //TODO IMPROVEMENT: for rotating? retainInstance = true
    }

    open val buildDialog: AlertDialog.Builder.() -> AlertDialog.Builder = { this }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Pass null as the parent view because its going in the dialog layout
        val builder = AlertDialog.Builder(root)

        customView = layout?.let { root.layoutInflater.inflate(layout!!, null) }?.also {
            builder.setView(it)
        }
        // Create Alert Dialog with your custom view
        return builder.buildDialog()
            .create()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //restore state
        viewModel.restoreState()
        binding?.restoreUi(savedInstanceState)

        //owner it is viewsuper.restore(savedState)
        viewModel.observeState(viewLifecycleOwner) { binding?.bind(it) }
        //bind default values if viewmodel not loaded data
        if (binding?.isInflated == false) binding?.onFinishInflate()

        viewModel.observeNotifications(viewLifecycleOwner) { root.renderNotification(it) }
        viewModel.observeNavigation(viewLifecycleOwner) { root.viewModel.navigate(it) }

        setupViews()
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        binding?.rebind()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        viewModel.saveState()
        binding?.saveUi(outState)
        super.onSaveInstanceState(outState)
    }
}