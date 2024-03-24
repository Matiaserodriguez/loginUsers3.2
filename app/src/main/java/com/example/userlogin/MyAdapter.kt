package com.example.userlogin

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.example.userlogin.databinding.ListItemBinding

class MyAdapter(private val context: Context, val arrayList: ArrayList<Course>) : BaseAdapter() {
    override fun getCount(): Int {
        return arrayList.size
    }

    override fun getItem(position: Int): Int {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        val binding = ListItemBinding.inflate(LayoutInflater.from((parent?.context ?: "") as Context?), parent, false)
        var convertView = convertView
        convertView = binding.root

        binding.textView.text = arrayList[position].courseName
        binding.price.text = "$${arrayList[position].price}"
        binding.description.text = arrayList[position].description

        binding.itemButton.setOnClickListener {
            if (binding.description.visibility == View.VISIBLE) {
                binding.description.visibility = View.GONE
            } else {
                binding.description.visibility = View.VISIBLE
            }
        }

        binding.checkBox.setOnCheckedChangeListener(null)  // Clear listener to avoid unwanted change events
        binding.checkBox.isChecked = arrayList[position].isChecked

        binding.checkBox.setOnCheckedChangeListener { _, isChecked ->
            arrayList[position].isChecked = isChecked
        }

        return convertView
    }
}