package com.camgist.gceresults.resultlist.details.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.camgist.gceresults.databinding.SingleSubjectDetailsBinding
import com.camgist.gceresults.resultlist.details.algorithms.Records

class StudentRecordAdapter(private val records : List<Records>) : RecyclerView.Adapter<StudentRecordAdapter.ViewHolder>() {



    class ViewHolder(val binding: SingleSubjectDetailsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(record: Records) {
            binding.tvSubjectName.text = record.subjectName
            binding.tvGrade.text = record.grade
            binding.tvPoint.text = record.point.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = SingleSubjectDetailsBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val record = records[position]
        holder.bind(record)
    }

    override fun getItemCount(): Int {
        return records.size
    }
}