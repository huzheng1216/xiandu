package com.inveno.xiandu.view.adapter

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.inveno.xiandu.R
import com.inveno.xiandu.bean.coin.CompeleteMissionData
import com.inveno.xiandu.view.main.welfare.SignInActivity

/**
 * @author yongji.wang
 * @date 2020/7/14
 * @更新说明：
 * @更新时间：2020/7/14
 * @Version：1.0.0
 */
class SignInTopAdapter(var context: Context, var mDatas: ArrayList<SignInActivity.SignInTopData>) : BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val gridView: View
        if (convertView == null) {
            gridView = inflater.inflate(R.layout.item_sign_in_grid, null)
            val days = gridView.findViewById<TextView>(R.id.days)
            val coin_img = gridView.findViewById<ImageView>(R.id.coin_img)
            val coin_num = gridView.findViewById<TextView>(R.id.coin_num)
            days.setText(mDatas.get(position).dayStr)
            if (mDatas.get(position).coinNum != 0) {
                coin_num.setText("+" + mDatas.get(position).coinNum)
            }else{
                coin_num.setText("--")
            }
            if (mDatas.get(position).isSign!!) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    coin_img.setImageDrawable(context.getDrawable(R.drawable.welfare_coin))
                } else {
                    coin_img.setImageDrawable(context.resources.getDrawable(R.drawable.welfare_coin))
                }
                coin_num.setTextColor(Color.parseColor("#F56C6C"))
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    coin_img.setImageDrawable(context.getDrawable(R.drawable.sign_in_coin_nomorl))
                } else {
                    coin_img.setImageDrawable(context.resources.getDrawable(R.drawable.sign_in_coin_nomorl))
                }
                coin_num.setTextColor(Color.parseColor("#999999"))
            }

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

    fun setData(datas: ArrayList<SignInActivity.SignInTopData>) {
        mDatas = datas
        notifyDataSetChanged()
    }
}