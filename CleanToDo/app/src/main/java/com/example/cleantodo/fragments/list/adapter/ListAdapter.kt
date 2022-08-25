package com.example.cleantodo.fragments.list.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.cleantodo.data.models.ToDoData
import com.example.cleantodo.databinding.RowLayoutBinding

class ListAdapter: RecyclerView.Adapter<ListAdapter.MyViewHolder>() {

    var dataList = emptyList<ToDoData>()
        get() = field
        set(value) {
            val toDoDiffUtil = ToDoDiffUtil(field, value)
            val toDoDiffResult = DiffUtil.calculateDiff(toDoDiffUtil)
            field = value
            toDoDiffResult.dispatchUpdatesTo(this)
            //notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        //val view = LayoutInflater.from(parent.context).inflate(R.layout.row_layout, parent, false)
        //return MyViewHolder(view)
        return MyViewHolder.from(parent)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val currentItem = dataList[position]

        holder.bind(currentItem)

    }

    /*class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        var row_background: ConstraintLayout = itemView.findViewById(R.id.row_background)
        var priority_indicator: CardView = itemView.findViewById(R.id.priority_indicator)
        var title_txt: TextView = itemView.findViewById(R.id.title_txt)
        var description_txt: TextView = itemView.findViewById(R.id.description_txt)

        var mItem: ToDoData? = null

    }*/

    class MyViewHolder(private val binding: RowLayoutBinding): RecyclerView.ViewHolder(binding.root){

        fun bind(toDoData: ToDoData){
            binding.toDoData = toDoData
            binding.executePendingBindings()
        }

        companion object{

            fun from(parent: ViewGroup): MyViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = RowLayoutBinding.inflate(layoutInflater, parent, false)
                return MyViewHolder(binding)
            }

        }

    }

    fun setData(toDoData: List<ToDoData>){
        val toDoDiffUtil = ToDoDiffUtil(dataList, toDoData)
        val toDoDiffResult = DiffUtil.calculateDiff(toDoDiffUtil)
        this.dataList = toDoData
        toDoDiffResult.dispatchUpdatesTo(this)
        //notifyDataSetChanged()
    }

}