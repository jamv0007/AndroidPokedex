package com.example.pokedex

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.GridView
import android.widget.SearchView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pokedex.adapters.PokeGridAdapter
import com.example.pokedex.api.PokeApiService
import com.example.pokedex.databinding.ActivityMainBinding
import com.example.pokedex.models.Pokemon
import com.example.pokedex.models.PokemonAsk
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity(),Callback<PokemonAsk> {

    private var retrofit: Retrofit? = null
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: PokeGridAdapter

    private var offset: Int = 0
    private var load: Boolean = true
    private var downLoadAll: Boolean = false
    private var allPokemon: ArrayList<Pokemon> = arrayListOf()
    private var searchText: String = ""
    private val pokemonDataResponder = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        if(result.resultCode == 2){

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        offset = 0
        load = true
        retrofit = Retrofit.Builder().baseUrl("https://pokeapi.co/api/v2/").addConverterFactory(GsonConverterFactory.create()).build()
        adapter = PokeGridAdapter(applicationContext) { pos, pokemon ->
            accessPokemonData(
                pos,
                pokemon
            )
        }
        initRecyclerView()
        getAPIData()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.menu,menu)
        var search: MenuItem = menu!!.findItem(R.id.app_bar_search)
        var searchView: SearchView = search.actionView as SearchView
        searchView.queryHint = "Buscar pokemon"
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {

                searchText = p0!!

                if(!downLoadAll){
                    downloadAllPokemon()
                }

                if(downLoadAll && searchText.isNotEmpty()){
                    //Filtrar por busqueda
                    val filter: ArrayList<Pokemon> = ArrayList( allPokemon.filter { pokemon -> pokemon.name.contains(searchText) })
                    if(!adapter.isChange){
                        adapter.change()

                    }
                    adapter.addElements(filter)
                    adapter.notifyDataSetChanged()

                }else if(searchText.isEmpty()){
                    adapter.change()
                    adapter.notifyDataSetChanged()
                }

                return true
            }

        })

        return super.onCreateOptionsMenu(menu)

    }

    private fun accessPokemonData(pos: Int, pokemon: Pokemon){
        val intent = Intent(applicationContext,PokemonData::class.java)
        intent.putExtra("NAME",pokemon.name)
        intent.putExtra("URL",pokemon.url)
        pokemonDataResponder.launch(intent)
    }

    private fun downloadAllPokemon(){
        var pApi: PokeApiService = retrofit!!.create(PokeApiService::class.java)
        var call: Call<PokemonAsk> = pApi.getPokemonList(100000,0)
        call.enqueue(object: Callback<PokemonAsk>{
            override fun onResponse(call: Call<PokemonAsk>, response: Response<PokemonAsk>) {
                if(response.isSuccessful){
                    var response: PokemonAsk = response.body()!!
                    var list: ArrayList<Pokemon> = response.results
                    allPokemon.addAll(list)
                    downLoadAll = true
                }else{
                    var dialog: AlertDialog.Builder = AlertDialog.Builder(applicationContext)
                    dialog.setMessage("Hay errores al obtener los datos")
                    dialog.setCancelable(false)
                    dialog.setPositiveButton("Aceptar",DialogInterface.OnClickListener{ dialogInterface, i ->

                    })
                    dialog.show()
                }
            }

            override fun onFailure(call: Call<PokemonAsk>, t: Throwable) {
                var dialog: AlertDialog.Builder = AlertDialog.Builder(applicationContext)
                dialog.setMessage("No hay conexion")
                dialog.setCancelable(false)
                dialog.setPositiveButton("Aceptar",DialogInterface.OnClickListener{ dialogInterface, i ->

                })
                dialog.show()
            }

        })
    }

    private fun getAPIData(){
        var pApi: PokeApiService = retrofit!!.create(PokeApiService::class.java)
        var call: Call<PokemonAsk> = pApi.getPokemonList(20,offset)

        call.enqueue(this)
    }

    private fun initRecyclerView(){
        val manager = GridLayoutManager(this,3)
        val decoracion = DividerItemDecoration(this,manager.orientation)

        binding.PokeGrid.adapter = adapter
        binding.PokeGrid.layoutManager = manager

        binding.PokeGrid.addOnScrollListener(object: RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if(dy > 0){
                    val visibleItemShow = manager.childCount
                    val lastItem = manager.findFirstVisibleItemPosition()
                    val totalItems = manager.itemCount

                    if(load){
                        if(visibleItemShow + lastItem >= totalItems){
                            load = false
                            offset += 20
                            getAPIData()
                        }
                    }
                }

            }
        })

    }

    override fun onResponse(call: Call<PokemonAsk>, response: Response<PokemonAsk>) {
        load = true
        if(response.isSuccessful){
            var response: PokemonAsk = response.body()!!
            var list: ArrayList<Pokemon> = response.results
            adapter.addElements(list)
            adapter.notifyDataSetChanged()
        }else{
            var dialog: AlertDialog.Builder = AlertDialog.Builder(this)
            dialog.setMessage("Hay errores al obtener los datos")
            dialog.setCancelable(false)
            dialog.setPositiveButton("Aceptar",DialogInterface.OnClickListener{ dialogInterface, i ->  

            })
            dialog.show()
        }
    }

    override fun onFailure(call: Call<PokemonAsk>, t: Throwable) {
        load = true
        var dialog: AlertDialog.Builder = AlertDialog.Builder(this)
        dialog.setMessage("No hay conexion")
        dialog.setCancelable(false)
        dialog.setPositiveButton("Aceptar",DialogInterface.OnClickListener{ dialogInterface, i ->

        })
        dialog.show()
    }
}