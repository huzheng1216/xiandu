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
public class Rule_list {

    private String ad_configid;
    private List<String> advertPos;
    private int h5_strategyType;
    private int os;
    private List<List<Rule>> rule;
    private String scenario;
    public void setAd_configid(String ad_configid) {
         this.ad_configid = ad_configid;
     }
     public String getAd_configid() {
         return ad_configid;
     }

    public void setAdvertPos(List<String> advertPos) {
         this.advertPos = advertPos;
     }
     public List<String> getAdvertPos() {
         return advertPos;
     }

    public void setH5_strategyType(int h5_strategyType) {
         this.h5_strategyType = h5_strategyType;
     }
     public int getH5_strategyType() {
         return h5_strategyType;
     }

    public void setOs(int os) {
         this.os = os;
     }
     public int getOs() {
         return os;
     }

    public void setRule(List<List<Rule>> rule) {
         this.rule = rule;
     }
     public List<List<Rule>> getRule() {
         return rule;
     }

    public void setScenario(String scenario) {
         this.scenario = scenario;
     }
     public String getScenario() {
         return scenario;
     }

}