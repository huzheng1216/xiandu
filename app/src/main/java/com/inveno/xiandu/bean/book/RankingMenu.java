package com.inveno.xiandu.bean.book;

/**
 * @author yongji.wang
 * @date 2020/6/17 13:40
 * @更新说明：
 * @更新时间：
 * @Version：1.0.0
 */
public class RankingMenu extends Menu{

    private int ranking_id;
    private String ranking_name;

    public RankingMenu(){

    }

    public RankingMenu(int ranking_id, String ranking_name) {
        this.ranking_id = ranking_id;
        this.ranking_name = ranking_name;
    }

    public int getRanking_id() {
        return ranking_id;
    }

    public void setRanking_id(int ranking_id) {
        this.ranking_id = ranking_id;
    }

    public String getRanking_name() {
        return ranking_name;
    }

    public void setRanking_name(String ranking_name) {
        this.ranking_name = ranking_name;
    }
}
