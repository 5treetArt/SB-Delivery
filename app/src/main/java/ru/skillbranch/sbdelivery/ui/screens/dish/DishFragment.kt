package ru.skillbranch.sbdelivery.ui.screens.dish

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_dish.*
import ru.skillbranch.sbdelivery.R
import ru.skillbranch.sbdelivery.ui.base.BaseFragment
import ru.skillbranch.sbdelivery.ui.base.Binding
import ru.skillbranch.sbdelivery.viewmodels.base.IViewModelState
import ru.skillbranch.sbdelivery.viewmodels.base.ViewModelFactory
import ru.skillbranch.sbdelivery.viewmodels.dish.DishViewModel

class DishFragment : BaseFragment<DishViewModel>() {

    private val args: DishFragmentArgs by navArgs()

    override val viewModel: DishViewModel by viewModels {
        ViewModelFactory(
            owner = this,
            params = args.dishId
        )
    }

    override val layout = R.layout.fragment_dish

    override val binding by lazy { DishBinding() }

    private val reviewsAdapter = ReviewsAdapter()

    override fun setupViews() {
        with(rv_reviews) {
            adapter = reviewsAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    inner class DishBinding : Binding() {

        override val afterInflated: (() -> Unit)?
            get() = super.afterInflated

        override fun bind(data: IViewModelState) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun saveUi(outState: Bundle) {
            super.saveUi(outState)
        }

        override fun restoreUi(savedState: Bundle?) {
            super.restoreUi(savedState)
        }
    }
}