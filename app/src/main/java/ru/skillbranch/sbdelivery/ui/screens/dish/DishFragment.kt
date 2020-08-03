package ru.skillbranch.sbdelivery.ui.screens.dish

import android.graphics.Paint
import android.os.Bundle
import android.widget.TextView
import androidx.core.text.buildSpannedString
import androidx.core.text.inSpans
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import kotlinx.android.synthetic.main.fragment_dish.*
import org.w3c.dom.Text
import ru.skillbranch.sbdelivery.R
import ru.skillbranch.sbdelivery.data.repositories.MockDishRepository
import ru.skillbranch.sbdelivery.extensions.attrValue
import ru.skillbranch.sbdelivery.extensions.dpToPx
import ru.skillbranch.sbdelivery.extensions.format
import ru.skillbranch.sbdelivery.ui.base.BaseFragment
import ru.skillbranch.sbdelivery.ui.base.Binding
import ru.skillbranch.sbdelivery.ui.base.ToolbarBuilder
import ru.skillbranch.sbdelivery.ui.custom.HorizontalNumberPicker
import ru.skillbranch.sbdelivery.ui.custom.spans.LeftDrawableSpan
import ru.skillbranch.sbdelivery.ui.delegates.RenderProp
import ru.skillbranch.sbdelivery.viewmodels.base.IViewModelState
import ru.skillbranch.sbdelivery.viewmodels.base.ViewModelFactory
import ru.skillbranch.sbdelivery.viewmodels.dish.DishState
import ru.skillbranch.sbdelivery.viewmodels.dish.DishViewModel

class DishFragment : BaseFragment<DishViewModel>() {

    //private val args: DishFragmentArgs by navArgs()

    private val args = MockDishRepository.findDish("5ed8da011f071c00465b2026").value!!

    override val viewModel: DishViewModel by viewModels {
        ViewModelFactory(
            owner = this,
            params = args.id
        )
    }

    override val layout = R.layout.fragment_dish

    override val binding by lazy { DishBinding() }

    override val prepareToolbar: (ToolbarBuilder.() -> Unit)? = {
        this.setTitle(args.name)
    }

    private val reviewsAdapter = ReviewsAdapter()

    private val colorSurface by lazy { root.attrValue(R.attr.colorSurface) }
    private val gap by lazy { root.dpToPx(8) }
    private val starIcon by lazy {
        root.getDrawable(R.drawable.ic_star_24dp_fill)!!.apply {
            setTint(colorSurface)
        }
    }

    override fun setupViews() {

        if (args.poster != null) {
            Glide.with(root)
                .load(args.poster)
                //.listener(object : RequestListener<Drawable> {
                //    override fun onLoadFailed(
                //        e: GlideException?,
                //        model: Any?,
                //        target: Target<Drawable>?,
                //        isFirstResource: Boolean
                //    ): Boolean = false
                //
                //    override fun onResourceReady(
                //        resource: Drawable?,
                //        model: Any?,
                //        target: Target<Drawable>?,
                //        dataSource: DataSource?,
                //        isFirstResource: Boolean
                //    ): Boolean {
                //        posterShimmer.stop()
                //        return false
                //    }
                //})
                //.placeholder(posterShimmer)
                .transform(CenterCrop())
                .into(iv_poster)
        }

        tv_sale.isVisible = args.oldPrice != null
        btn_like.setOnClickListener { viewModel.handleLike() }
        tv_title.text = args.name
        tv_description.text = args.description
        tv_old_price.text = "${args.oldPrice} ₽"
        tv_old_price.paintFlags = tv_old_price.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        tv_old_price.isVisible = args.oldPrice != null

        tv_price.text = "${args.price} ₽"
        picker.setCounterListener(object : HorizontalNumberPicker.OnValueChangeListener {
            override fun onValueChange(view: HorizontalNumberPicker, newValue: Int) {
                viewModel.handleAmount(newValue)
            }
        })

        btn_add.setOnClickListener { viewModel.handleAdd() }

        //TODO
        //tv_rating.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_star_24dp_fill_insets, 0,0,0)
        //tv_rating.text = "${args.rating?.format()}/5"

        tv_rating.setText(buildSpannedString {
            inSpans(LeftDrawableSpan(starIcon, gap, colorSurface)) {
                append("${args.rating?.format()}/5")
            }
        }, TextView.BufferType.SPANNABLE)

        btn_add_review.setOnClickListener {
            //val action = DishFragmentDirections.PageDishToPageReview()
            //viewModel.navigate(NavigationCommand.To(action.id, action.arguments))
        }

        with(rv_reviews) {
            adapter = reviewsAdapter
            layoutManager = LinearLayoutManager(context)
        }
        viewModel.observeList(viewLifecycleOwner) { reviewsAdapter.submitList(it) }
    }

    inner class DishBinding : Binding() {

        var amount: Int by RenderProp(1) {
            picker.setValue(it)
        }

        var isLike: Boolean by RenderProp(false) {
            btn_like.isChecked = it
        }

        override val afterInflated = {

        }

        override fun bind(data: IViewModelState) {
            data as DishState
            amount = data.amount
            isLike = data.isLike
        }

        override fun saveUi(outState: Bundle) {
            super.saveUi(outState)
        }

        override fun restoreUi(savedState: Bundle?) {
            super.restoreUi(savedState)
        }
    }
}