package com.parallel.framework;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@JsonPropertyOrder({"processer","type", "costTime", "eventList","start","end","request","response"})
public class FullLogEntity {

    private String type;

    private List<String> eventList = new ArrayList<>();

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private Date start;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private Date end;
    private long costTime;
    private Object request;
    private Object response;
    private String processer;

    public void addEvent(String event) {
        eventList.add(event);
    }



    public long getCostTime() {
        if (end != null && start != null) {
            costTime = end.getTime() - start.getTime();
        }
        return costTime;
    }

    public List<String> getEventList() {
        return eventList;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getProcesser() {
        return processer;
    }

    public void setProcesser(String processer) {
        this.processer = processer;
    }

    public Object getRequest() {
        return request;
    }

    public void setRequest(Object request) {
        this.request = request;
    }

    public Object getResponse() {
        return response;
    }

    public void setResponse(Object response) {
        this.response = response;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }
}
