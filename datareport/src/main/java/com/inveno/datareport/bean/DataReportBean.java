package com.inveno.datareport.bean;

public class DataReportBean {

    public int event_id;
    public int page_id;
    public long event_time;
    public int type;
    public String cpack;
    public long server_time;
    public long stay_time;
    public long leave_time;
    public String upack;
    public Long report_time;
    public String tk;
    public String ip;
    public long content_id;

    public int getEvent_id() {
        return event_id;
    }

    public void setEvent_id(int event_id) {
        this.event_id = event_id;
    }

    public int getPage_id() {
        return page_id;
    }

    public void setPage_id(int page_id) {
        this.page_id = page_id;
    }

    public long getEvent_time() {
        return event_time;
    }

    public void setEvent_time(long event_time) {
        this.event_time = event_time;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getCpack() {
        return cpack;
    }

    public void setCpack(String cpack) {
        this.cpack = cpack;
    }

    public long getServer_time() {
        return server_time;
    }

    public void setServer_time(long server_time) {
        this.server_time = server_time;
    }

    public long getStay_time() {
        return stay_time;
    }

    public void setStay_time(long stay_time) {
        this.stay_time = stay_time;
    }

    public long getLeave_time() {
        return leave_time;
    }

    public void setLeave_time(long leave_time) {
        this.leave_time = leave_time;
    }

    public String getUpack() {
        return upack;
    }

    public void setUpack(String upack) {
        this.upack = upack;
    }

    public Long getReport_time() {
        return report_time;
    }

    public void setReport_time(Long report_time) {
        this.report_time = report_time;
    }

    public String getTk() {
        return tk;
    }

    public void setTk(String tk) {
        this.tk = tk;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public long getContent_id() {
        return content_id;
    }

    public void setContent_id(long content_id) {
        this.content_id = content_id;
    }

    //    public void reset(){
//        event_id = 0;
//        page_id = 0;
//        event_time = 0;
//        type = 0;
//        cpack = "";
//        server_time = 0;
//        stay_time = 0;
//        leave_time = 0;
//        report_time = 0L;
//        tk = "";
//    }


    @Override
    public String toString() {
        return "DataReportBean{" +
                "event_id=" + event_id +
                ", page_id=" + page_id +
                ", event_time=" + event_time +
                ", type=" + type +
                ", cpack='" + cpack + '\'' +
                ", server_time=" + server_time +
                ", stay_time=" + stay_time +
                ", leave_time=" + leave_time +
                ", upack='" + upack + '\'' +
                ", report_time=" + report_time +
                ", tk='" + tk + '\'' +
                ", ip='" + ip + '\'' +
                ", content_id='" + content_id + '\'' +
                '}';
    }
}
