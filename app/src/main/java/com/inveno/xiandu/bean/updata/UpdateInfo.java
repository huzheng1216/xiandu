package com.inveno.xiandu.bean.updata;

import java.io.Serializable;

/**
 * Created by zxl02 on 2018/12/13.
 */

public class UpdateInfo implements Serializable
{
    //是否需要升级  0不升级  1升级
    private int upgrade;
    //升级说明
    private String instruction;
    //下载地址
    private String link;
    //升级类型  1，强制升级，2非强制升级
    private int type;
    //升级版本
    private String version;
    //最小兼容版本
    private String compatible_version;

    public int getUpgrade() {
        return upgrade;
    }

    public void setUpgrade(int upgrade) {
        this.upgrade = upgrade;
    }

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getCompatible_version() {
        return compatible_version;
    }

    public void setCompatible_version(String compatible_version) {
        this.compatible_version = compatible_version;
    }
}
