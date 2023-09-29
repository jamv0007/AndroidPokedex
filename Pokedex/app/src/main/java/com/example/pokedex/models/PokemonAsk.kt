package com.example.pokedex.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class PokemonAsk(
    @Expose @SerializedName("count") val count: Int,
    @Expose @SerializedName("next") val next: String,
    @Expose @SerializedName("previous") val previous: String,
    @Expose @SerializedName("results") val results: ArrayList<Pokemon>,


)