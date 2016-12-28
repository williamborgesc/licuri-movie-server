package com.dlnapps.xml.model.yts;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "xml")
@XmlAccessorType(XmlAccessType.FIELD)
public class MovieList {

    @XmlElementWrapper(name = "data")
    @XmlElement
    private List<Movie> movies;

    public List<Movie> getMovies() {
	return movies;
    }

    public void setMovies(List<Movie> movies) {
	this.movies = movies;
    }

}
