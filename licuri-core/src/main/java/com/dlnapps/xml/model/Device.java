package com.dlnapps.xml.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.fourthline.cling.model.meta.Icon;
import org.fourthline.cling.model.meta.RemoteDevice;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Device {

    @XmlElement
    private String nome;

    @XmlElement
    private String uuid;

    @XmlElement
    private String iconUri;

    public Device() {
    }

    public Device(RemoteDevice remoteDevice) {
	this.nome = remoteDevice.getDetails().getFriendlyName();
	this.uuid = remoteDevice.getIdentity().getUdn().getIdentifierString();
	this.iconUri = getIcon(remoteDevice);

    }

    public Device(String uuid) {
	this.uuid = uuid;
    }

    private String getIcon(RemoteDevice remoteDevice) {

	for (Icon icon : remoteDevice.getIcons()) {

	    return remoteDevice.normalizeURI(icon.getUri()).toString();

	}

	return null;
    }

    public String getNome() {
	return nome;
    }

    public void setNome(String nome) {
	this.nome = nome;
    }

    public String getUuid() {
	return uuid;
    }

    public void setUuid(String uuid) {
	this.uuid = uuid;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
	return result;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj) {
	    return true;
	}
	if (obj == null) {
	    return false;
	}
	if (!(obj instanceof Device)) {
	    return false;
	}
	Device other = (Device) obj;
	if (uuid == null) {
	    if (other.uuid != null) {
		return false;
	    }
	} else if (!uuid.equals(other.uuid)) {
	    return false;
	}
	return true;
    }

    public String getIconUri() {
	return iconUri;
    }

    public void setIconUri(String iconUri) {
	this.iconUri = iconUri;
    }

    @Override
    public String toString() {
        return this.nome;
    }
}
