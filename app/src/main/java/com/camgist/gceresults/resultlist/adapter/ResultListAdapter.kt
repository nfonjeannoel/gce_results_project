package com.camgist.gceresults.resultlist.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.camgist.gceresults.databinding.ListResultItemBinding
import com.camgist.gceresults.network.ResultData

class ResultListAdapter(val listener: ResultClickListener)
    : RecyclerView.Adapter<ResultListAdapter.ViewHolder>() {

    var results = listOf<ResultData>()

    class ViewHolder(val binding : ListResultItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(curResult: ResultData, position: Int) {
            binding.tvCenterInfo.text = "Center: ${curResult.center_number}"
            binding.tvStudentName.text = curResult.student_name
            binding.tvStudentSchool.text = curResult.center_name
            binding.tvNumber.text = (position + 1).toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListResultItemBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val curResult = results[position]
        holder.bind(curResult, position)
        holder.itemView.setOnClickListener {
            listener.onClick(curResult)
        }
    }

    override fun getItemCount(): Int {
        return results.size
    }


}

class ResultClickListener(val clickListener : (result : ResultData) -> Unit){
    fun onClick(result: ResultData) = clickListener(result)
}








