package com.inveno.xiandu.view.main.welfare

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SimpleAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.inveno.xiandu.R
import com.inveno.xiandu.R.layout.activity_sign_in
import com.inveno.xiandu.bean.coin.CompeleteMissionData
import com.inveno.xiandu.bean.coin.MissionData
import com.inveno.xiandu.bean.coin.MissionDataList
import com.inveno.xiandu.invenohttp.instancecontext.APIContext
import com.inveno.xiandu.utils.SPUtils
import com.inveno.xiandu.utils.Toaster
import com.inveno.xiandu.view.TitleBarBaseActivity
import com.inveno.xiandu.view.adapter.MissionAdapter
import com.inveno.xiandu.view.adapter.SignInMissionAdapter
import com.inveno.xiandu.view.adapter.SignInTopAdapter
import kotlinx.android.synthetic.main.activity_sign_in.*

/**
 * @author yongji.wang
 * @date 2020/7/14
 * @更新说明：
 * @更新时间：2020/7/14
 * @Version：1.0.0
 */
@Suppress("UNREACHABLE_CODE")
class SignInActivity : TitleBarBaseActivity() {

    private var signInTopAdapter: SignInTopAdapter? = null
    private val signInStrArray: Array<String> = arrayOf("第一天", "第二天", "第三天", "第四天", "第五天", "第六天", "第七天")
    private var topDataList = arrayListOf<SignInTopData>()
    private var bottomDataList = arrayListOf<MissionData>()
    private var finishPosition: Int = 0

    var signInMissionAdapter = SignInMissionAdapter(this, bottomDataList)
    override fun layoutID(): Int {
        return activity_sign_in
    }

    override fun getCenterText(): String {
        return "签到"
    }

    override fun initView() {
        super.initView()
        initSignInTopView()
        getSignInTopData()
        getSignInBottomData()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBar(R.color.white, true)
    }

    //进入页面显示空的签到数据
    fun initSignInTopView() {
        //获取存储的签到金币数
        val coinNum = SPUtils.getInformain("coinNum", 0)
        for (index in 0..signInStrArray.size - 1) {
            val signInTopData = SignInTopData()
            signInTopData.coinNum = coinNum
            signInTopData.isSign = false
            signInTopData.dayStr = signInStrArray[index]
            topDataList.add(signInTopData)
        }
        signInTopAdapter = SignInTopAdapter(this, topDataList)
        sign_in_top.setSelector(ColorDrawable(Color.TRANSPARENT));
        sign_in_top.adapter = signInTopAdapter
    }

    fun getSignInTopData() {
        topDataList.clear()
        val typeIds = intArrayOf(1)
        APIContext.coinApi().getMission(typeIds)
                .onSuccess { data: MissionDataList? ->
                    if (data != null) {
                        val coinNum = data.mission_list[0].gold_num
                        val mission_id = data.mission_list[0].mission_id
                        //将金币进行本地存储，用于下次显示
                        SPUtils.setInformain("coinNum", coinNum)
                        finishMission(mission_id, coinNum)
                    }
                }
                .onFail { code, message -> }
                .execute()
    }

    fun getSignInBottomData() {
        val typeIds = intArrayOf(2)
        APIContext.coinApi().getMission(typeIds)
                .onSuccess { data: MissionDataList? ->
                    if (data != null) {
                        if (data.mission_list.size > 0) {
                            no_coin_detail.visibility = View.GONE
                        } else {
                            no_coin_detail.visibility = View.VISIBLE
                        }
                        bottomDataList = data.mission_list as ArrayList<MissionData>
                        val linearLayoutManager = LinearLayoutManager(this)
                        sign_detail_recycleview.layoutManager = linearLayoutManager
                        signInMissionAdapter.setsData(data.mission_list)
                        sign_detail_recycleview.adapter = signInMissionAdapter
                        signInMissionAdapter.setOnitemClickListener(SignInMissionAdapter.OnItemClickListener { position: Int ->
                            finishPosition = position
                            finishMission(data.mission_list.get(position).mission_id)
                        })
                    } else {
                        no_coin_detail.visibility = View.VISIBLE
                    }
                }
                .onFail { code, message -> }
                .execute()
    }

    fun finishMission(mission_id: Int, coinNum: Int = 0) {
        APIContext.coinApi().completeMission(mission_id.toString())
                .onSuccess { compeleteData: CompeleteMissionData? ->
                    //传入金币>0 则是签到请求
                    if (coinNum > 0) {
                        topDataList.clear()
                        if (compeleteData != null) {
                            val dayNum = compeleteData.continueTimes
                            for (index in 0..signInStrArray.size - 1) {
                                val signInTopData = SignInTopData()
                                signInTopData.coinNum = coinNum
                                signInTopData.isSign = index < dayNum
                                signInTopData.dayStr = signInStrArray[index]
                                topDataList.add(signInTopData)
                            }
                        } else {
                            for (index in 0..signInStrArray.size - 1) {
                                val signInTopData = SignInTopData()
                                signInTopData.coinNum = coinNum
                                signInTopData.isSign = false
                                signInTopData.dayStr = signInStrArray[index]
                                topDataList.add(signInTopData)
                            }
                        }

                        sign_in_bt.visibility = View.GONE
                        //给gridview传值显示
                        signInTopAdapter?.setData(topDataList)
                    } else {
                        bottomDataList[finishPosition].code = 6

                        signInMissionAdapter.setsData(bottomDataList)
                    }
                    if (compeleteData != null) {
                        if (compeleteData.gold > 0) {
                            Toaster.showToastCenterShort(this, "+%s金币".format(compeleteData.gold))
                        }
                    }
                }
                .onFail { code, message ->
//                                sign_in_bt.visibility = View.VISIBLE
                }
                .execute()
    }

    class SignInTopData {
        var dayStr: String? = ""
        var isSign: Boolean? = false
        var coinNum: Int? = 0
    }
}