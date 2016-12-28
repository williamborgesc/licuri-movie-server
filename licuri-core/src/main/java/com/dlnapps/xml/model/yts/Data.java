package com.dlnapps.xml.model.yts;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Data {

    @XmlElement
    private int movie_count;

    @XmlElement
    private int limit;

    @XmlElement
    private int page_number;

    @XmlElement(name="movies")
    private List<Movie> movies;

    public int getMovie_count() {
	return movie_count;
    }

    public void setMovie_count(int movie_count) {
	this.movie_count = movie_count;
    }

    public int getLimit() {
	return limit;
    }

    public void setLimit(int limit) {
	this.limit = limit;
    }

    public int getPage_number() {
	return page_number;
    }

    public void setPage_number(int page_number) {
	this.page_number = page_number;
    }

    public List<Movie> getMovies() {
	return movies;
    }

    public void setMovies(List<Movie> movies) {
	this.movies = movies;
    }
}
