package com.inveno.datareport.bean;

public class ReadBean {


    public long startTime;

    public long endTime;

    public int pageId;
    public String upack;
    public String cpack;
    public int type;
    public long serverTime;
    public long contentId;

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public int getPageId() {
        return pageId;
    }

    public void setPageId(int pageId) {
        this.pageId = pageId;
    }

    public String getUpack() {
        return upack;
    }

    public void setUpack(String upack) {
        this.upack = upack;
    }

    public String getCpack() {
        return cpack;
    }

    public void setCpack(String cpack) {
        this.cpack = cpack;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getServerTime() {
        return serverTime;
    }

    public void setServerTime(long serverTime) {
        this.serverTime = serverTime;
    }

    public long getContentId() {
        return contentId;
    }

    public void setContentId(long contentId) {
        this.contentId = contentId;
    }
}
