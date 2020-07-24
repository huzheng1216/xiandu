package com.inveno.xiandu.invenohttp.instancecontext;

import com.inveno.android.basics.service.app.context.InstanceContext;
import com.inveno.xiandu.invenohttp.api.book.BookbrackApi;
import com.inveno.xiandu.invenohttp.api.book.GetBookCityAPi;
import com.inveno.xiandu.invenohttp.api.book.ReadTrackApi;
import com.inveno.xiandu.invenohttp.api.book.SearchBookApi;
import com.inveno.xiandu.invenohttp.api.coin.CoinApi;
import com.inveno.xiandu.invenohttp.api.coin.ConsumerCoinAPI;
import com.inveno.xiandu.invenohttp.api.coin.EarnCoinAPI;
import com.inveno.xiandu.invenohttp.api.coin.QueryCoinAPI;
import com.inveno.xiandu.invenohttp.api.coin.QueryCoinDetailAPI;
import com.inveno.xiandu.invenohttp.api.user.GetUserAPI;
import com.inveno.xiandu.invenohttp.api.user.LoginAPI;
import com.inveno.xiandu.invenohttp.api.user.UpdataUserAPI;
import com.inveno.xiandu.invenohttp.api.user.VaricationCodeAPI;
import com.inveno.xiandu.invenohttp.api.welfare.WelfareApi;

/**
 * @author yongji.wang
 * @date 2020/6/8 17:13
 * @更新说明：
 * @更新时间：
 * @Version：1.0.0
 */
public class APIContext {

    public static VaricationCodeAPI varicationCode() {
        return InstanceContext.get().getInstance(VaricationCodeAPI.class);
    }

    public static LoginAPI loginAPI() {
        return InstanceContext.get().getInstance(LoginAPI.class);
    }

    public static UpdataUserAPI updataUserAPI() {
        return InstanceContext.get().getInstance(UpdataUserAPI.class);
    }

    public static GetUserAPI getUserAPI() {
        return InstanceContext.get().getInstance(GetUserAPI.class);
    }

    public static CoinApi coinApi(){
        return InstanceContext.get().getInstance(CoinApi.class);
    }
    public static EarnCoinAPI earnCoinAPI() {
        return InstanceContext.get().getInstance(EarnCoinAPI.class);
    }

    public static ConsumerCoinAPI consumerCoinAPI() {
        return InstanceContext.get().getInstance(ConsumerCoinAPI.class);
    }

    public static QueryCoinAPI queryCoinAPI() {
        return InstanceContext.get().getInstance(QueryCoinAPI.class);
    }

    public static QueryCoinDetailAPI queryCoinDetailAPI() {
        return InstanceContext.get().getInstance(QueryCoinDetailAPI.class);
    }

    public static GetBookCityAPi getBookCityAPi() {
        return InstanceContext.get().getInstance(GetBookCityAPi.class);
    }

    public static BookbrackApi bookbrackApi() {
        return InstanceContext.get().getInstance(BookbrackApi.class);
    }

    public static ReadTrackApi readTrackApi() {
        return InstanceContext.get().getInstance(ReadTrackApi.class);
    }

    public static SearchBookApi SearchBookApi() {
        return InstanceContext.get().getInstance(SearchBookApi.class);
    }

    public static WelfareApi getWelfareApi() {
        return InstanceContext.get().getInstance(WelfareApi.class);
    }
}
