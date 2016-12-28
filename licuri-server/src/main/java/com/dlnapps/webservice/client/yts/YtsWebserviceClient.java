package com.dlnapps.webservice.client.yts;

import static com.dlnapps.config.ConfigurationLoader.getYtsListUrlBase;
import static com.dlnapps.dlna_core.model.Quality.ALL;
import static java.text.MessageFormat.format;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.springframework.stereotype.Service;

import com.dlnapps.config.ConfigurationLoader;
import com.dlnapps.dlna_core.model.Quality;
import com.dlnapps.http.client.HttpClient;
import com.dlnapps.xml.model.yts.Movie;
import com.dlnapps.xml.model.yts.Xml;

@Service
public class YtsWebserviceClient {

    public List<Movie> getMoviesList() {

	return getMoviesList(ALL, 1);
    }

    public List<Movie> getMoviesList(Quality quality) {

	return getMoviesList(quality, 1);
    }

    public List<Movie> getMoviesList(int page) {

	return getMoviesList(ALL, page);
    }

    public Movie search(String movieName, int year) {

	Xml xml = search(movieName);

	if ((xml.getData().getMovie_count() == 1 && !xml.getData().getMovies().get(0).getYear().equals(year)) || xml.getData().getMovie_count() == 0) {
	    return null;
	}

	for (Movie movie : xml.getData().getMovies()) {

	    if (movie.getYear().equals(year)) {

		return movie;
	    }
	}

	return null;

    }

    public Xml search(String searchTerm) {

	String url = null;
	
	try {

	    url = format(ConfigurationLoader.getYtsSearchUrlBase(), URLEncoder.encode(searchTerm, "UTF-8"));

	} catch (UnsupportedEncodingException e) {
	    e.printStackTrace();
	}

	return request(url);
    }

    public List<Movie> getMoviesList(Quality quality, int pageNumber) {

	String url = format(getYtsListUrlBase(), quality.getValue(), pageNumber);

	return request(url).getData().getMovies();

    }

    private Xml request(String url) {

	try {

	    HttpGet get = new HttpGet(url);

	    // add header
	    get.setHeader("Accept", "application/xml");

	    HttpResponse response = HttpClient.executeGet(get);

	    if (response.getStatusLine().getStatusCode() > 307) {
		throw new RuntimeException("Erro ao buscar vídeos no YTS  : HTTP error code : " + response.getStatusLine().getStatusCode());
	    }

	    JAXBContext jaxbContext = JAXBContext.newInstance(Xml.class);

	    Unmarshaller jaxbUnmarshaller;
	    jaxbUnmarshaller = jaxbContext.createUnmarshaller();
	    return (Xml) jaxbUnmarshaller.unmarshal(response.getEntity().getContent());

	} catch (JAXBException e) {
	    e.printStackTrace();
	} catch (MalformedURLException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	}
	return null;
    }

    
}
