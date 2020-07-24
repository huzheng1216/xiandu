package com.inveno.xiandu.view.main.welfare

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.inveno.xiandu.R
import com.inveno.xiandu.bean.BaseDataBean
import com.inveno.xiandu.bean.welfare.RechangeBeanList
import com.inveno.xiandu.invenohttp.instancecontext.APIContext
import com.inveno.xiandu.view.TitleBarBaseActivity
import com.inveno.xiandu.view.adapter.RecordDetailAdapter
import com.inveno.xiandu.view.custom.MRecycleScrollListener
import kotlinx.android.synthetic.main.activity_top_up_record.*

/**
 * @author yongji.wang
 * @date 2020/7/23
 * @更新说明：
 * @更新时间：2020/7/23
 * @Version：1.0.0
 */
class TopUpRecordActivity : TitleBarBaseActivity() {

    var recordDetailAdapter: RecordDetailAdapter? = null
    var recordList = arrayListOf<BaseDataBean>()
    var pageNum: Int = 0

    override fun layoutID(): Int {
        return R.layout.activity_top_up_record
    }

    override fun getCenterText(): String {
        return "充话费记录"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBar(R.color.white, true)
    }

    override fun initView() {
        super.initView()
        recordDetailAdapter = RecordDetailAdapter(this, this, recordList)
        val dataLayoutManager = LinearLayoutManager(this)
        top_up_detail_recycleview.setLayoutManager(dataLayoutManager)
        top_up_detail_recycleview.adapter = recordDetailAdapter
        //上拉加载
        top_up_detail_recycleview.addOnScrollListener(object : MRecycleScrollListener() {
            override fun onLoadMore() {
                getRecord(pageNum)
            }

            override fun onVisibleItem(first: Int, last: Int) {}
        })
        getRecord(pageNum)
    }

    fun getRecord(page: Int) {
        APIContext.getWelfareApi().getRechangeInfo(page)
                ?.onSuccess { data: RechangeBeanList ->
                    if (page == 0) {
                        if (data.recharge_list.size < 1) {
                            no_record.visibility = View.VISIBLE
                            return@onSuccess
                        } else {
                            no_record.visibility = View.GONE
                        }
                    } else {
                        no_record.visibility = View.GONE
                    }
                    if (data.recharge_list.size < 20) {
                        recordDetailAdapter?.setFooterText("没有更多数据")
                    } else {
                        recordDetailAdapter?.setFooterText("正在努力加载...")
                    }
                    pageNum = data.page + 1;
                    recordList.addAll(data.recharge_list)
                    recordDetailAdapter?.setData(recordList)
                }
                ?.onFail { code, message ->
                    if (page == 0) {
                        no_record.visibility = View.VISIBLE
                    } else {
                        no_record.visibility = View.GONE
                    }
                }
                ?.execute()
    }
}