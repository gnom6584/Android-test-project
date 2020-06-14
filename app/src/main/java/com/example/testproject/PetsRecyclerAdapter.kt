package com.example.testproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.testproject.databinding.PetCardBinding
import kotlinx.android.synthetic.main.pet_card.view.*

const val CARD_LIKES_IMAGE_URL = "https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcTjf8r9bj3Ojcx7CkmJ62zOEaSEd0oiiMl2SAufewJLxXBFs0Gh&usqp=CAU"

class PetsRecyclerAdapter(val dataSet : MutableList<PetModel>): RecyclerView.Adapter<PetsRecyclerAdapter.ViewHolder>(){
    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        var dataSample : PetModel? = null
        val binding by lazy { DataBindingUtil.bind<PetCardBinding>(itemView) }
    }

    val removeListeners = mutableListOf<()->Unit>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(PetCardBinding.inflate(inflater, parent, false).root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder.binding!!){
            holder.dataSample = dataSet[position]
            name.text = dataSet[position].name
            likesText.text = dataSet[position].likes.toString()
            Glide // знаю что не эффективно каждый раз грузить, но и пофиг
                .with(holder.itemView.context)
                .load(dataSet[position].url)
                .into(image)
            Glide
                .with(holder.itemView.context)
                .load(CARD_LIKES_IMAGE_URL)
                .into(likesImage)
        }
    }

    fun removeItem(viewHolder: RecyclerView.ViewHolder){
        removeListeners.forEach {
            it()
        }
        dataSet.removeAt(viewHolder.adapterPosition)
        notifyItemRemoved(viewHolder.adapterPosition)
    }

    override fun getItemCount() = dataSet.size
}
