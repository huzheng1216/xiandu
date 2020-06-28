package com.inveno.datareport.bean;

public class DataReportBean extends DrBaseBean{

    public int event_id;
    public int page_id;
    public long event_time;
    public int type;
    public String cpack;
    public long server_time;
    public long stay_time;
    public long leave_time;

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

    public void reset(){
        event_id = 0;
        page_id = 0;
        event_time = 0;
        type = 0;
        cpack = "";
        server_time = 0;
        stay_time = 0;
        leave_time = 0;
        report_time = 0L;
        tk = "";
    }

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
                ", product_id='" + product_id + '\'' +
                ", uid='" + uid + '\'' +
                ", pid='" + pid + '\'' +
                ", app_ver='" + app_ver + '\'' +
                ", api_ver='" + api_ver + '\'' +
                ", report_time=" + report_time +
                ", network='" + network + '\'' +
                ", upack='" + upack + '\'' +
                ", tk='" + tk + '\'' +
                ", ip='" + ip + '\'' +
                ", location='" + location + '\'' +
                ", aid='" + aid + '\'' +
                ", brand='" + brand + '\'' +
                ", model='" + model + '\'' +
                ", osv='" + osv + '\'' +
                ", platform='" + platform + '\'' +
                ", language='" + language + '\'' +
                ", mcc='" + mcc + '\'' +
                ", mnc='" + mnc + '\'' +
                ", referrer='" + referrer + '\'' +
                '}';
    }
}
