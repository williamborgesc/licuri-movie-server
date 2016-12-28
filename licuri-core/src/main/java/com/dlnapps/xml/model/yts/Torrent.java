package com.dlnapps.xml.model.yts;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Torrent {

    @XmlElement
    private String url;

    @XmlElement
    private String hash;

    @XmlElement
    private String quality;

    @XmlElement
    private String seeds;

    @XmlElement
    private String peers;

    @XmlElement
    private String size;

    @XmlElement
    private String size_bytes;

    @XmlElement
    private String date_uploaded;

    @XmlElement
    private String date_uploaded_unix;
    
    public Torrent() {
    }

    public Torrent(String url) {
	this.url = url;
    }


    public String getUrl() {
	return url;
    }

    public void setUrl(String url) {
	this.url = url;
    }

    public String getHash() {
	return hash;
    }

    public void setHash(String hash) {
	this.hash = hash;
    }

    public String getQuality() {
	return quality;
    }

    public void setQuality(String quality) {
	this.quality = quality;
    }

    public String getSeeds() {
	return seeds;
    }

    public void setSeeds(String seeds) {
	this.seeds = seeds;
    }

    public String getPeers() {
	return peers;
    }

    public void setPeers(String peers) {
	this.peers = peers;
    }

    public String getSize() {
	return size;
    }

    public void setSize(String size) {
	this.size = size;
    }

    public String getSize_bytes() {
	return size_bytes;
    }

    public void setSize_bytes(String size_bytes) {
	this.size_bytes = size_bytes;
    }

    public String getDate_uploaded() {
	return date_uploaded;
    }

    public void setDate_uploaded(String date_uploaded) {
	this.date_uploaded = date_uploaded;
    }

    public String getDate_uploaded_unix() {
	return date_uploaded_unix;
    }

    public void setDate_uploaded_unix(String date_uploaded_unix) {
	this.date_uploaded_unix = date_uploaded_unix;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((hash == null) ? 0 : hash.hashCode());
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
	if (!(obj instanceof Torrent)) {
	    return false;
	}
	Torrent other = (Torrent) obj;
	if (hash == null) {
	    if (other.hash != null) {
		return false;
	    }
	} else if (!hash.equals(other.hash)) {
	    return false;
	}
	return true;
    }

    @Override
    public String toString() {
	return "Torrent [torrent=" + url + "]";
    }

}
