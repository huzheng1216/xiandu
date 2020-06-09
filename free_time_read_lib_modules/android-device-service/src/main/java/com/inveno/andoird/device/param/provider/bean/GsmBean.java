package com.inveno.andoird.device.param.provider.bean;

/**
 * @author yongji.wang
 * @date 2020/6/4 21:00
 * @更新说明：
 * @更新时间：
 * @Version：1.0.0
 */
public class GsmBean {

    public int lac = 0;
    public int cell_id = 0;
    public String mnc;
    public String mcc;
    public String nmnc;
    public String nmcc;

    public int getLac() {
        return lac;
    }

    public void setLac(int lac) {
        this.lac = lac;
    }

    public int getCell_id() {
        return cell_id;
    }

    public void setCell_id(int cell_id) {
        this.cell_id = cell_id;
    }

    public String getMnc() {
        return mnc;
    }

    public void setMnc(String mnc) {
        this.mnc = mnc;
    }

    public String getMcc() {
        return mcc;
    }

    public void setMcc(String mcc) {
        this.mcc = mcc;
    }

    public String getNmnc() {
        return nmnc;
    }

    public void setNmnc(String nmnc) {
        this.nmnc = nmnc;
    }

    public String getNmcc() {
        return nmcc;
    }

    public void setNmcc(String nmcc) {
        this.nmcc = nmcc;
    }
}
