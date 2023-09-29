package com.example.pokedex.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Pokemon (
    @Expose @SerializedName("name") val name: String,
    @Expose @SerializedName("url") val url: String

){
    fun getNumber(): Int{
        var splitText: List<String> = url.split("/")
        return splitText[splitText.size-2].toInt()
    }
}