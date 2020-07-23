package com.inveno.xiandu.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.inveno.xiandu.R
import com.inveno.xiandu.bean.welfare.ActivityBean
import com.inveno.xiandu.utils.GlideUtils
import com.inveno.xiandu.view.main.welfare.SignInActivity

/**
 * @author yongji.wang
 * @date 2020/7/14
 * @更新说明：
 * @更新时间：2020/7/14
 * @Version：1.0.0
 */
class ActivityListAdapter(var context: Context, var mDatas: List<ActivityBean>) : BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val gridView: View
        if (convertView == null) {
            gridView = inflater.inflate(R.layout.item_activity_list_grid, null)
            val activityImg = gridView.findViewById<ImageView>(R.id.activity_img)
            val activityName = gridView.findViewById<TextView>(R.id.activity_name)
            activityName.text = mDatas[position].activity_name
            GlideUtils.LoadImage(context, mDatas[position].activity_img, R.drawable.book_defaul_img, activityImg)

        } else {
            gridView = convertView
        }

        return gridView
    }

    override fun getItem(position: Int): Any? {
        return null
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getCount(): Int {
        return mDatas.size
    }

    fun setData(datas: List<ActivityBean>) {
        mDatas = datas
        notifyDataSetChanged()
    }

}