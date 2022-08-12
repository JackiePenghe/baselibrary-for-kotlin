package com.sscl.basesample.adapter

import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.sscl.basesample.R
import com.sscl.basesample.databinding.ComJackiepengheBaselibraryAdapterFileListRecyclerViewBinding
import java.io.File

class FileListRecyclerViewAdapter :BaseQuickAdapter<File, FileListRecyclerViewAdapter.FileListRecyclerViewAdapterDataBindingViewHolder>(
    R.layout.com_jackiepenghe_baselibrary_adapter_file_list_recycler_view){

    class FileListRecyclerViewAdapterDataBindingViewHolder(view: View) :BaseDataBindingHolder<ComJackiepengheBaselibraryAdapterFileListRecyclerViewBinding>(view)

    override fun convert(holder: FileListRecyclerViewAdapterDataBindingViewHolder, item: File) {
        holder.dataBinding?.text?.text = item.name
    }
}