package com.example.pokedex.adapters

import android.content.Context
import android.view.View
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.example.pokedex.databinding.ActivityMainBinding
import com.example.pokedex.databinding.GridcellBinding
import com.example.pokedex.models.Pokemon
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

class ViewHolderPokeGrid(view: View,context: Context): RecyclerView.ViewHolder(view) {

    var binding = GridcellBinding.bind(view)
    var context = context

    fun render(position: Int, pokemon: Pokemon,onClick:(Int,Pokemon) -> Unit){
        binding.PokeText.text = pokemon.name
        Glide.with(context).load("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/" + pokemon.getNumber() + ".png").centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL).into(binding.PokeImage)
        itemView.setOnClickListener{onClick(position,pokemon)}
    }

}