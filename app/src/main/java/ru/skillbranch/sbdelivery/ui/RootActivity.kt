package ru.skillbranch.sbdelivery.ui

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.core.view.GravityCompat
import androidx.navigation.*
import androidx.navigation.ui.*
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_root.*
import ru.skillbranch.sbdelivery.R
import ru.skillbranch.sbdelivery.extensions.setBackgroundDrawable
import ru.skillbranch.sbdelivery.ui.base.BaseActivity
import ru.skillbranch.sbdelivery.viewmodels.RootState
import ru.skillbranch.sbdelivery.viewmodels.RootViewModel
import ru.skillbranch.sbdelivery.viewmodels.base.IViewModelState
import ru.skillbranch.sbdelivery.viewmodels.base.NavigationCommand
import ru.skillbranch.sbdelivery.viewmodels.base.Notify


class RootActivity : BaseActivity<RootViewModel>() {
    override val layout = R.layout.activity_root
    public override val viewModel: RootViewModel by viewModels()
    private val topDestinations = setOf<Int>(
        //R.id.nav_today,
        //R.id.nav_statistic,
        //R.id.nav_calculator,
        //R.id.nav_calendar
    )

    override fun onBackPressed() =
        when {
            drawer_layout.isDrawerOpen(GravityCompat.START) ->
                drawer_layout.closeDrawer(GravityCompat.START)
            //TODO IMPROVEMENT: hack для того, чтобы не переходить на экран с регистрацией
            // после регистрации нужно этот экран вычищать из backstack
            // перенести в калбэк onDestinationChanged?
            nav_host_fragment?.childFragmentManager?.backStackEntryCount == 1 -> finish()

            else -> super.onBackPressed()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)

        nav_view.setupWithNavController(navController)
        val config = AppBarConfiguration(topDestinations, drawer_layout)
        //toolbar.setupWithNavController(navController, config)
        setupActionBarWithNavController(navController, config)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        //nav_view.setNavigationItemSelectedListener {
        //    //if click on bottom navigtion view -> navigate to destination by item id
        //    viewModel.navigate(NavigationCommand.To(it.itemId))
        //    true
        //}

        //navController.addOnDestinationChangedListener { controller, destination, arguments ->
        //    //if destination change set select bottom navigation item
        //    nav_view.selectDestination(destination)

        //    val privateDestination = arguments?.get("private_destination") as Int?

        //    if (destination.id == R.id.nav_auth) nav_view.selectItem(privateDestination)

        //    if (isAuth && destination.id == R.id.nav_auth) {
        //        controller.popBackStack()
        //        privateDestination?.let { controller.navigate(it) }
        //    }
        //}

        //navController.addOnDestinationChangedListener { _, destination, arguments ->
        //    //TODO IMPROVEMENT: move to viewModel? Or R.class shouldn't to be in viewModel?
        //    if (viewModel.isUserDataExists() && destination.id == R.id.nav_firstlaunch) {
        //        viewModel.navigate(NavigationCommand.To(
        //            R.id.nav_today,
        //            arguments,
        //            navOptions { popUpTo(R.id.nav_today) {} }
        //        ))
        //    }
        //}
    }

    //TODO IMPROVEMENT: dirty hack?
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                if (topDestinations.contains(navController.currentDestination?.id)) {
                    drawer_layout.openDrawer(GravityCompat.START)
                    return true
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun subscribeOnState(state: IViewModelState) {
        state as RootState
        //TODO IMPLEMENT: do something with state
    }

    override fun renderNotification(notify: Notify) {
        val snackbar = Snackbar.make(container, notify.message, Snackbar.LENGTH_LONG)
            .setBackgroundDrawable(R.drawable.bg_snackbar)
        //snackbar.anchorView = findViewById<Bottombar>(R.id.bottombar) ?: nav_view

        when (notify) {
            is Notify.TextMessage -> {
                /* nothing */
            }

            is Notify.ActionMessage -> {
                with(snackbar) {
                    setActionTextColor(context.resources.getColor(R.color.color_accent_dark))
                    setAction(notify.actionLabel) { notify.actionHandler() }
                }
            }

            is Notify.ErrorMessage -> {
                with(snackbar) {
                    setBackgroundTint(context.resources.getColor(R.color.design_default_color_error))
                    setTextColor(context.resources.getColor(android.R.color.white))
                    setActionTextColor(context.resources.getColor(android.R.color.white))
                    setAction(notify.errLabel) { notify.errHandler?.invoke() }
                }
            }
        }

        snackbar.show()
    }
}
