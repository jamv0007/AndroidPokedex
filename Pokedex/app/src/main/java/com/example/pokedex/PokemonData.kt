package com.example.pokedex

import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.pokedex.api.PokeApiService
import com.example.pokedex.databinding.ActivityPokemonDataBinding
import com.example.pokedex.models.Pokemon
import com.example.pokedex.models.PokemonAllData
import com.jjoe64.graphview.GridLabelRenderer
import com.jjoe64.graphview.ValueDependentColor
import com.jjoe64.graphview.helper.StaticLabelsFormatter
import com.jjoe64.graphview.series.BarGraphSeries
import com.jjoe64.graphview.series.DataPoint
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class PokemonData : AppCompatActivity() {

    private lateinit var binding: ActivityPokemonDataBinding
    private lateinit var pokemon: Pokemon
    private lateinit var retrofit: Retrofit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPokemonDataBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setTitle(resources.getString(R.string.pokedata))

        val name: String = intent.getStringExtra("NAME")!!
        val url: String = intent.getStringExtra("URL")!!

        pokemon = Pokemon(name,url);

        binding.pokeName.text = pokemon.name
        Glide.with(applicationContext).load("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/" + pokemon.getNumber() + ".png").centerCrop().diskCacheStrategy(
            DiskCacheStrategy.ALL).into(binding.pokeImageData)

        retrofit = Retrofit.Builder().baseUrl("https://pokeapi.co/api/v2/").addConverterFactory(
            GsonConverterFactory.create()).build()

        getAPIData()
    }

    private fun getAPIData(){
        val pokeApiService: PokeApiService = retrofit.create(PokeApiService::class.java)
        val call: Call<PokemonAllData> = pokeApiService.getPokemonInfo(pokemon.getNumber())
        call.enqueue(object: Callback<PokemonAllData>{
            override fun onResponse(call: Call<PokemonAllData>, response: Response<PokemonAllData>) {
                if(response.isSuccessful){
                    val response = response.body()
                    var type: String = ""
                    for(i in response!!.types) {
                        type += i.type.name
                        type += "/"
                    }
                    binding.pokeType.text = "Tipo: " + type
                    var ability = ""
                    var hidden = ""

                    for (i in response.abilities){
                        if(!i.isHidden) {
                            ability += i.ability.name
                            ability += "/"
                        }else{
                            hidden += i.ability.name
                            hidden += "/"
                        }
                    }


                    binding.pokeAbility.text = "Habilidades: " + ability
                    binding.pokeHiddenAbility.text = "Habilidades ocultas: " + hidden

                    var grafData: BarGraphSeries<DataPoint> = BarGraphSeries()
                    var ind = 0

                    for(i in response.stats) {
                        grafData.appendData(DataPoint(ind.toDouble(), i.baseStat.toDouble()),true,6)
                        println(i.baseStat)

                        ind++
                    }

                    var colors: ArrayList<Int> = arrayListOf(Color.rgb(0.549f,0.811f,0.498f),Color.rgb(0.823f,0.121f,0.235f),Color.rgb(1f,0.54f,0f),Color.rgb(0.745f,0.576f,0.83f),Color.rgb(0.596f,0.592f,0.662f),Color.rgb(0f,0.309f,0.596f))
                    var current = -1
                    grafData.setValueDependentColor { data ->

                        if(current < 5) {
                            current++
                            colors[current]
                        }else{
                            0
                        }

                    }


                    grafData.setDrawValuesOnTop(true)
                    grafData.setValuesOnTopColor(Color.BLACK)
                    binding.stats.getGridLabelRenderer().setGridStyle( GridLabelRenderer.GridStyle.NONE )


                    val staticLabelsFormatter = StaticLabelsFormatter(binding.stats)
                    var labels = arrayOf("HP","Atq","Def","Sp.Atq","Def.Sp","Vel")
                    staticLabelsFormatter.setHorizontalLabels(labels)
                    binding.stats.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter)

                    grafData.title = labels[0]
                    grafData.color = colors[0]
                    binding.stats.addSeries(grafData)

                    for(i in 1 until 6){
                        var graf: BarGraphSeries<DataPoint> = BarGraphSeries()
                        graf.title = labels[i]
                        graf.color = colors[i]
                        binding.stats.addSeries(graf)
                    }
                    binding.stats.legendRenderer.isVisible = true
                    binding.stats.legendRenderer.backgroundColor = Color.argb(1,1,1,1)
                    binding.stats.getViewport().setMinX(-1.0);
                    binding.stats.getViewport().setMaxX(6.0);
                    binding.stats.getViewport().setMinY(1.0);
                    binding.stats.getViewport().setMaxY(255.0);
                    binding.stats.gridLabelRenderer.isVerticalLabelsVisible = false
                    binding.stats.gridLabelRenderer.isHorizontalLabelsVisible = false

                    binding.stats.getViewport().setYAxisBoundsManual(true);
                    binding.stats.getViewport().setXAxisBoundsManual(true);

                    //grafData.spacing = 10
                    println(ind)
                }else{
                    println("No se han podido obtener los datos")
                }
            }

            override fun onFailure(call: Call<PokemonAllData>, t: Throwable) {
                println("No se han podido obtener los datos")
            }

        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        return super.onCreateOptionsMenu(menu)
    }
}