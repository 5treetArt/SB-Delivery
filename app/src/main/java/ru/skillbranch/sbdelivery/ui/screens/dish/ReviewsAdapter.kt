package ru.skillbranch.sbdelivery.ui.screens.dish

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_dish_review.*
import ru.skillbranch.sbdelivery.R
import ru.skillbranch.sbdelivery.data.models.ReviewItemData
import ru.skillbranch.sbdelivery.extensions.format

class ReviewsAdapter: ListAdapter<ReviewItemData, ReviewVH>(callback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewVH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_dish_review, parent, false)
        return ReviewVH(view)
    }

    override fun onBindViewHolder(holder: ReviewVH, position: Int) = holder.bind(getItem(position))
}

class ReviewVH(override val containerView: View) :
    RecyclerView.ViewHolder(containerView), LayoutContainer {

    fun bind(data: ReviewItemData) {
        tv_name_date.text = "${data.author}, ${data.date.format("dd.MM.yy")}"
        rating.rating = data.rating.toFloat()
        rating.numStars = data.rating
        rating.max = data.rating
        rating.invalidate()
        tv_review.text = data.text ?: ""
        tv_review.isVisible = data.text != null
    }
}

private val callback = object : DiffUtil.ItemCallback<ReviewItemData>() {
    override fun areItemsTheSame(oldItem: ReviewItemData, newItem: ReviewItemData) =
        oldItem.date == newItem.date

    override fun areContentsTheSame(oldItem: ReviewItemData, newItem: ReviewItemData) =
        oldItem == newItem
}