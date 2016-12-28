package com.dlnapps.dao;

public class MovieFilter {

    private String title;

    private Integer year;

    private String rating;

    private String language;

    private String genre;

    private MovieFilter() {
    }

    public static Builder builder() {

	return new Builder();
    }

    public static class Builder {

	private String title;
	private Integer year;
	private String rating;
	private String language;
	private String genre;

	public MovieFilter build() {

	    MovieFilter movieFilter = new MovieFilter();
	    movieFilter.title = this.title;
	    movieFilter.year = this.year;
	    movieFilter.rating = this.rating;
	    movieFilter.language = this.language;
	    movieFilter.genre = this.genre;

	    return movieFilter;

	}

	public Builder title(String title) {
	    this.title = title;
	    return this;
	}

	public Builder year(int year) {
	    this.year = year;
	    return this;
	}

	public Builder rating(String rating) {
	    this.rating = rating;
	    return this;
	}

	public Builder language(String language) {
	    this.language = language;
	    return this;
	}

	public Builder genre(String genre) {
	    this.genre = genre;
	    return this;
	}

    }

    public String getTitle() {
	return title;
    }

    public Integer getYear() {
	return year;
    }

    public String getRating() {
	return rating;
    }

    public String getLanguage() {
	return language;
    }

    public String getGenre() {
	return genre;
    }

}
