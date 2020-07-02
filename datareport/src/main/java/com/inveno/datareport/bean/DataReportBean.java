package com.inveno.datareport.bean;

public class DataReportBean {

    public Integer event_id;
    public Integer page_id;
    public Long event_time;
    public Integer type;
    public String cpack;
    public Long server_time;
    public Long stay_time;
    public Long leave_time;
    public String upack="";
    public String tk="";
    public String ip="";
    public Long content_id;
    public String request_time;

    public Integer getEvent_id() {
        return event_id;
    }

    public void setEvent_id(Integer event_id) {
        this.event_id = event_id;
    }

    public Integer getPage_id() {
        return page_id;
    }

    public void setPage_id(Integer page_id) {
        this.page_id = page_id;
    }

    public Long getEvent_time() {
        return event_time;
    }

    public void setEvent_time(Long event_time) {
        this.event_time = event_time;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getCpack() {
        return cpack;
    }

    public void setCpack(String cpack) {
        this.cpack = cpack;
    }

    public Long getServer_time() {
        return server_time;
    }

    public void setServer_time(Long server_time) {
        this.server_time = server_time;
    }

    public Long getStay_time() {
        return stay_time;
    }

    public void setStay_time(Long stay_time) {
        this.stay_time = stay_time;
    }

    public Long getLeave_time() {
        return leave_time;
    }

    public void setLeave_time(Long leave_time) {
        this.leave_time = leave_time;
    }

    public String getUpack() {
        return upack;
    }

    public void setUpack(String upack) {
        this.upack = upack;
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

    public Long getContent_id() {
        return content_id;
    }

    public void setContent_id(Long content_id) {
        this.content_id = content_id;
    }

    public String getRequest_time() {
        return request_time;
    }

    public void setRequest_time(String request_time) {
        this.request_time = request_time;
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
                ", tk='" + tk + '\'' +
                ", ip='" + ip + '\'' +
                ", content_id=" + content_id +
                ", request_time=" + request_time +
                '}';
    }
}
