package com.hubishub.animalvoice.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.hubishub.animalvoice.R
import com.hubishub.animalvoice.model.Animal

class AnimalAdapter(
    private val animals: List<Animal>,
    private var selectedId: Int,
    private val onAnimalClick: (Animal) -> Unit
) : RecyclerView.Adapter<AnimalAdapter.AnimalViewHolder>() {

    fun updateSelection(animalId: Int) {
        val old = animals.indexOfFirst { it.id == selectedId }
        val new = animals.indexOfFirst { it.id == animalId }
        selectedId = animalId
        if (old >= 0) notifyItemChanged(old)
        if (new >= 0) notifyItemChanged(new)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimalViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_animal, parent, false)
        return AnimalViewHolder(view)
    }

    override fun onBindViewHolder(holder: AnimalViewHolder, position: Int) {
        holder.bind(animals[position], animals[position].id == selectedId)
    }

    override fun getItemCount() = animals.size

    inner class AnimalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val card: CardView = itemView.findViewById(R.id.card_animal)
        private val emoji: TextView = itemView.findViewById(R.id.tv_animal_emoji)
        private val name: TextView = itemView.findViewById(R.id.tv_animal_name)
        private val icon: ImageView = itemView.findViewById(R.id.iv_animal_icon)
        private val selectedIndicator: View = itemView.findViewById(R.id.view_selected_indicator)

        fun bind(animal: Animal, isSelected: Boolean) {
            emoji.text = animal.emoji
            name.text = animal.name

            if (isSelected) {
                card.setCardBackgroundColor(itemView.context.getColor(R.color.animal_selected_bg))
                card.cardElevation = 12f
                selectedIndicator.visibility = View.VISIBLE
            } else {
                card.setCardBackgroundColor(itemView.context.getColor(R.color.animal_card_bg))
                card.cardElevation = 4f
                selectedIndicator.visibility = View.INVISIBLE
            }

            itemView.setOnClickListener {
                onAnimalClick(animal)
            }
        }
    }
}
