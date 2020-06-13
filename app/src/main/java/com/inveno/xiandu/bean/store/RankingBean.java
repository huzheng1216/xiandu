package com.inveno.xiandu.bean.store;

import com.inveno.xiandu.bean.BaseDataBean;

/**
 * @author yongji.wang
 * @date 2020/6/13 11:23
 * @更新说明：
 * @更新时间：
 * @Version：1.0.0
 */
public class RankingBean extends BaseDataBean {
    private String rankBookPic;

    private String rankBookname;

    private String rankBookType;

    private int rankingNum;

    public String getRankBookPic() {
        return rankBookPic;
    }

    public void setRankBookPic(String rankBookPic) {
        this.rankBookPic = rankBookPic;
    }

    public String getRankBookname() {
        return rankBookname;
    }

    public void setRankBookname(String rankBookname) {
        this.rankBookname = rankBookname;
    }

    public String getRankBookType() {
        return rankBookType;
    }

    public int getRankingNum() {
        return rankingNum;
    }

    public void setRankingNum(int rankingNum) {
        this.rankingNum = rankingNum;
    }

    public void setRankBookType(String rankBookType) {
        this.rankBookType = rankBookType;
    }
}
