package com.dlnapps.xml.model;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class DeviceList {

    @XmlElement(name = "devices")
    private List<Device> devices;

    public List<Device> getDevices() {
	return devices;
    }

    public void setDevices(List<Device> devices) {
	this.devices = devices;
    }

}
