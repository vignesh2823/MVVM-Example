package com.example.mvvmtask.Adapter

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.exampleproject.responsemodel.TrendingResponseItem
import com.example.myapplication.R
import java.util.*
import kotlin.collections.ArrayList

class HomeAdapter(
    val context: Context,
    val category: List<TrendingResponseItem>,
    var filterCategory: List<TrendingResponseItem>,
    val itemClick: (TrendingResponseItem) -> Unit
) :
    RecyclerView.Adapter<HomeAdapter.Holder>(), Filterable {

    inner class Holder(itemView: View, val itemClick: (TrendingResponseItem) -> Unit) :
        RecyclerView.ViewHolder(itemView) {
        val title = itemView.findViewById<TextView>(R.id.title)
        val author = itemView.findViewById<TextView>(R.id.author)
        val desc = itemView.findViewById<TextView>(R.id.description)
        val language = itemView.findViewById<TextView>(R.id.language)
        val languageColor = itemView.findViewById<ImageView>(R.id.language_color)
        val star = itemView.findViewById<TextView>(R.id.star)

        fun bindCategory(category: TrendingResponseItem, context: Context) {
            title.text = category.name
            author.text = category.author
            desc.text = category.description
            language.text = category.language
            star.text = category.stars.toString()
            languageColor.getBackground()
                .setColorFilter(Color.parseColor(category.languageColor), PorterDuff.Mode.SRC_ATOP);
            itemView.setOnClickListener { itemClick(category) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_view, parent, false);
        return Holder(view, itemClick);
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder?.bindCategory(filterCategory[position], context)
    }

    override fun getItemCount(): Int {
        return filterCategory.count()
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    filterCategory = category
                } else {
                    val resultList = ArrayList<TrendingResponseItem>()
                    for (row in category) {
                        if (row.name.toLowerCase(Locale.ROOT)
                                .contains(charSearch.toLowerCase(Locale.ROOT))
                        ) {
                            resultList.add(row)
                        }
                    }
                    filterCategory = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = filterCategory
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filterCategory = results?.values as ArrayList<TrendingResponseItem>
                notifyDataSetChanged()
            }

        }
    }
}