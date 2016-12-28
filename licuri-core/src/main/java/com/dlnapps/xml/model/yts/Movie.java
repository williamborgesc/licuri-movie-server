package com.dlnapps.xml.model.yts;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Movie {

    @XmlElement
    private String id;

    @XmlElement
    private String url;

    @XmlElement
    private String imdb_code;

    @XmlElement
    private String title;

    @XmlElement
    private String title_long;

    @XmlElement
    private Integer year;

    @XmlElement
    private double rating;

    @XmlElement
    private String runtime;

    @XmlElement(name = "genres")
    private List<String> genres;

    @XmlElement
    private String language;

    @XmlElement
    private String mpa_rating;

    @XmlElement
    private String small_cover_image;

    @XmlElement
    private String medium_cover_image;

    @XmlElement
    private String state;

    @XmlElementWrapper(name = "torrents")
    @XmlElement(name = "torrent")
    private List<Torrent> torrents;

    @XmlElement
    private String date_uploaded;

    @XmlElement
    private String date_uploaded_unix;

    @XmlElement
    private int rt_critics_score;

    @XmlElement
    private int rt_audience_score;

    @XmlTransient
    private String file_path;

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getUrl() {
	return url;
    }

    public void setUrl(String url) {
	this.url = url;
    }

    public String getImdb_code() {
	return imdb_code;
    }

    public void setImdb_code(String imdb_code) {
	this.imdb_code = imdb_code;
    }

    public String getTitle() {
	if (title != null) {

	    return title.replaceAll("[^A-z1-9] ", " ");

	}

	return title;
    }

    public void setTitle(String title) {
	this.title = title;
    }

    public String getTitle_long() {
	return title_long;
    }

    public void setTitle_long(String title_long) {
	this.title_long = title_long;
    }

    public Integer getYear() {
	return year;
    }

    public void setYear(Integer year) {
	this.year = year;
    }

    public double getRating() {
	return rating;
    }

    public void setRating(double rating) {
	this.rating = rating;
    }

    public String getRuntime() {
	return runtime;
    }

    public void setRuntime(String runtime) {
	this.runtime = runtime;
    }

    public List<String> getGenres() {

	if (genres == null) {

	    return new ArrayList<String>();
	}
	return genres;
    }

    public void setGenres(List<String> genres) {
	this.genres = genres;
    }

    public String getLanguage() {
	
	if(language == null){
	    
	    return "Other";
	}
	
	return language;
    }

    public void setLanguage(String language) {
	this.language = language;
    }

    public String getMpa_rating() {
	return mpa_rating;
    }

    public void setMpa_rating(String mpa_rating) {
	this.mpa_rating = mpa_rating;
    }

    public String getSmall_cover_image() {
	return small_cover_image;
    }

    public void setSmall_cover_image(String small_cover_image) {
	this.small_cover_image = small_cover_image;
    }

    public String getMedium_cover_image() {
	return medium_cover_image;
    }

    public void setMedium_cover_image(String medium_cover_image) {
	this.medium_cover_image = medium_cover_image;
    }

    public String getState() {
	return state;
    }

    public void setState(String state) {
	this.state = state;
    }

    public List<Torrent> getTorrents() {
	return torrents;
    }

    public void setTorrents(List<Torrent> torrents) {
	this.torrents = torrents;
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

    public int getRt_critics_score() {
	return rt_critics_score;
    }

    public void setRt_critics_score(int rt_critics_score) {
	this.rt_critics_score = rt_critics_score;
    }

    public int getRt_audience_score() {
	return rt_audience_score;
    }

    public void setRt_audience_score(int rt_audience_score) {
	this.rt_audience_score = rt_audience_score;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((imdb_code == null) ? 0 : imdb_code.hashCode());
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
	if (!(obj instanceof Movie)) {
	    return false;
	}
	Movie other = (Movie) obj;
	if (imdb_code == null) {
	    if (other.imdb_code != null) {
		return false;
	    }
	} else if (!imdb_code.equals(other.imdb_code)) {
	    return false;
	}
	return true;
    }

    @Override
    public String toString() {
	return "Movie [title=" + title + "]";
    }

    public String getFile_path() {
	return file_path;
    }

    public void setFile_path(String file_path) {
	this.file_path = file_path;
    }

}