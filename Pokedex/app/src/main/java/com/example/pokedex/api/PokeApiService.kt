package com.example.pokedex.api

import com.example.pokedex.models.PokemonAllData
import com.example.pokedex.models.PokemonAsk
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PokeApiService {

    @GET("pokemon")
    fun getPokemonList(@Query("limit") limit: Int,@Query("offset") offset: Int): Call<PokemonAsk>

    @GET("pokemon/{id}")
    fun getPokemonInfo(@Path("id") id: Int): Call<PokemonAllData>

}