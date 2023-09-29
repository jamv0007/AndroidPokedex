package com.example.pokedex.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pokedex.R
import com.example.pokedex.models.Pokemon

class PokeGridAdapter(var context: Context,val onClick:(Int,Pokemon) -> Unit): RecyclerView.Adapter<ViewHolderPokeGrid>() {

    var list: ArrayList<Pokemon> = arrayListOf()
    var saveList: ArrayList<Pokemon> = arrayListOf()
    var isChange: Boolean = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderPokeGrid {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolderPokeGrid(layoutInflater.inflate(R.layout.gridcell,parent,false), context)
    }

    override fun onBindViewHolder(holder: ViewHolderPokeGrid, position: Int) {
        val element = list[position]
        holder.render(position,element,onClick)
    }

    override fun getItemCount(): Int {
       return list.count()
    }

    fun addElements(list: ArrayList<Pokemon>){

        if(isChange){
            this.list.clear()
            this.list.addAll(list)
        }else {
            this.list.addAll(list)
        }
    }

    fun changeValue(): Boolean{
        return isChange
    }

    fun change(){
        isChange = !isChange
        if(isChange){
            saveList.clear()
            saveList.addAll(this.list)
        }else{
            this.list.clear()
            this.list.addAll(saveList)
        }
    }
}