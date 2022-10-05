package com.vichsu.group2firebase

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.vichsu.group2firebase.databinding.RequestItemBinding

class requestAdapter(val onClickListener: OnClickListener) : ListAdapter<String, requestAdapter.AViewHolder>(object :DiffUtil.ItemCallback<String>(){
    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }

    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }

}) {

    class OnClickListener(val clickListener: (email: String,boolean:Boolean) -> Unit) {
        fun onClick(email: String,boolean: Boolean) = clickListener(email,boolean)
    }

    inner class AViewHolder(val binding:RequestItemBinding):RecyclerView.ViewHolder(binding.root){

        fun bind(email: String){
            binding.requestEmail.text = email + "休想加你為好友"
            binding.buttonYes.setOnClickListener {
                onClickListener.onClick(email,true)
            }
            binding.buttonNo.setOnClickListener {
                onClickListener.onClick(email,false)
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AViewHolder {
        val view = RequestItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return AViewHolder(view)
    }

    override fun onBindViewHolder(holder: AViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

}