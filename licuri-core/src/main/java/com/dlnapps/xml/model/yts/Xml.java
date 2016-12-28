package com.dlnapps.xml.model.yts;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Xml {

    @XmlElement
    private String status;

    @XmlElement
    private String status_message;

    @XmlElement
    private Data data;

    public String getStatus() {
	return status;
    }

    public void setStatus(String status) {
	this.status = status;
    }

    public String getStatus_message() {
	return status_message;
    }

    public void setStatus_message(String status_message) {
	this.status_message = status_message;
    }

    public Data getData() {
	return data;
    }

    public void setData(Data data) {
	this.data = data;
    }

}
