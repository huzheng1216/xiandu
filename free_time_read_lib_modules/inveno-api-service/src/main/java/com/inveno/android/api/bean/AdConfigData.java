/**
  * Copyright 2020 bejson.com 
  */
package com.inveno.android.api.bean;
import java.util.List;

/**
 * Auto-generated: 2020-06-10 16:7:57
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class AdConfigData {

    private List<AdvertList> advertList;
    private String reportUrl;
    private List<Rule_list> rule_list;
    public void setAdvertList(List<AdvertList> advertList) {
         this.advertList = advertList;
     }
     public List<AdvertList> getAdvertList() {
         return advertList;
     }

    public void setReportUrl(String reportUrl) {
         this.reportUrl = reportUrl;
     }
     public String getReportUrl() {
         return reportUrl;
     }

    public void setRule_list(List<Rule_list> rule_list) {
         this.rule_list = rule_list;
     }
     public List<Rule_list> getRule_list() {
         return rule_list;
     }

}