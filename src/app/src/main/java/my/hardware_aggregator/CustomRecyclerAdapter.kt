package my.hardware_aggregator

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

import java.util.*
import kotlin.collections.ArrayList

import com.squareup.picasso.Picasso

import my.hardware_aggregator.data.models.Product

class CustomRecyclerAdapter (
    private var v: ArrayList<Product>,
    private var values: ArrayList<Product>,
    private val context: Context ) : RecyclerView.Adapter<CustomRecyclerAdapter.MyViewHolder>(), Filterable {

    override fun getItemCount() = v.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.activity_result_elements, parent, false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.textTitle?.text = v[position].title

        if (holder.textTitle?.text != "") {
        holder.textTitle?.setOnClickListener {
            val urlRedirect = Uri.parse(v[position].url)

            val data = Intent(Intent.ACTION_VIEW, urlRedirect)
            context.startActivity(data)
        } }

        holder.textDescription?.text = v[position].description
        holder.textCost?.text = v[position].cost.toString()

        val imageUrl = v[position].image_url

        if (imageUrl != null) {
            if (imageUrl.isEmpty()) {
                holder.productImage?.setImageResource(R.drawable.placeholder)
            } else {
                Picasso.get().load(imageUrl).into(holder.productImage)
            }
        }

    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textTitle: TextView? = null
        var textDescription: TextView? = null
        var textCost: TextView? = null
        var productImage: ImageView? = null

        init {
            textTitle = itemView.findViewById(R.id.text_view_title)
            textDescription = itemView.findViewById(R.id.text_view_description)
            textCost = itemView.findViewById(R.id.text_view_cost)
            productImage = itemView.findViewById(R.id.product_image)
        }
    }

    private fun redefineCopy() {
        values = v
    }

    @SuppressLint("NotifyDataSetChanged")
    fun reverse() {
        v.reverse()
        redefineCopy()

        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setNewValues(newValues: ArrayList<Product>) {
        v = newValues
        redefineCopy()

        notifyDataSetChanged()
    }

    override fun getFilter(): Filter {

        return object : Filter() {

            override fun performFiltering(p0: CharSequence?): FilterResults {
                val key: String = p0.toString()

                v = if (key.isEmpty()) {
                    values
                } else {
                    val newValues = ArrayList<Product>()

                    for (value in v) {
                        if (value.title?.lowercase(Locale.ROOT)?.contains(key.lowercase(Locale.ROOT)) == true) {
                            newValues.add(value)
                        }
                    }

                    newValues
                }

                val filterResults = FilterResults()
                filterResults.values = v

                return filterResults

            }

            @SuppressLint("NotifyDataSetChanged")
            override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
                @Suppress("UNCHECKED_CAST")
                v = p1?.values as ArrayList<Product>
                notifyDataSetChanged()
            }

        }
    }

}