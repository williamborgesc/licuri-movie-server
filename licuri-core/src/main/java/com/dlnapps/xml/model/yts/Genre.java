package com.dlnapps.xml.model.yts;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Genre {

    @XmlElement
    private String genres;

    public String getGenres() {
	return genres;
    }

    public void setGenres(String genres) {
	this.genres = genres;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((genres == null) ? 0 : genres.hashCode());
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
	if (!(obj instanceof Genre)) {
	    return false;
	}
	Genre other = (Genre) obj;
	if (genres == null) {
	    if (other.genres != null) {
		return false;
	    }
	} else if (!genres.equals(other.genres)) {
	    return false;
	}
	return true;
    }

    @Override
    public String toString() {
	return "Genre [genres=" + genres + "]";
    }
    
    
}
