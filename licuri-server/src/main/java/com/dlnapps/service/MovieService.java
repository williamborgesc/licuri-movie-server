package com.dlnapps.service;

import static com.dlnapps.config.ConfigurationLoader.getDefaultDirectory;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dlnapps.dao.Dao;
import com.dlnapps.dao.MovieFilter;
import com.dlnapps.webservice.client.yts.YtsWebserviceClient;
import com.dlnapps.xml.model.yts.Movie;

@Service
public class MovieService {

    private static final String FILE_NAME_PATTERN = "([A-z\\.]{1,})([0-9]{0,4})?";

    private YtsWebserviceClient ytsWebserviceClient;

    private Dao dao;

    private static final String[] KNOWN_FILE_EXTENSIONS = { "avi", "mkv", "mp4", "wmv" };

    public void updateMoviesOnDB() {

	synchronizeFromFileSystem();

	synchronizeFromDatabase();
    }

    private void synchronizeFromDatabase() {
	for (Movie movie : dao.getAll()) {

	    // verificar se existe o arquivo

	    if ((movie.getFile_path() == null ) || !(new File(movie.getFile_path()).exists())) {

		System.err.printf("Arquivo do filme %s no diretório %s não encontrado. Removendo da lista\n", movie.getTitle(), movie.getFile_path());

		dao.removeMovie(movie);
	    }
	}
    }

    private void synchronizeFromFileSystem() {
	Collection<File> files = FileUtils.listFiles(new File(getDefaultDirectory()), KNOWN_FILE_EXTENSIONS, true);

	List<Movie> movies;
	Movie movie;
	String movieTitle;
	int year;
	MovieFilter filter;

	for (File file : files) {

	    try {

		movieTitle = getMovieTitle(file.getName());
		
		year = getMovieYear(file.getName());

		// Localizando filme na base de dados

		filter = MovieFilter.builder().title(movieTitle).year(year).build();

		movies = dao.find(filter);

		// Não achou filme na base
		if (movies.size() == 0) {

		    // Procurando o nome e o ano no yts
		    movie = ytsWebserviceClient.search(movieTitle, year);

		    if (movie != null) {

			filter = MovieFilter.builder().title(movie.getTitle()).year(movie.getYear()).build();

			// Procurando na base com o nome do yts
			movies = dao.find(filter);

			if (movies.size() == 0) {

			    dao.insertMovie(movie, file.getAbsolutePath());

			}

		    } else {

			movie = new Movie();

			movie.setTitle(movieTitle.isEmpty()? movieTitle : file.getName());
			movie.setYear(year);

			dao.insertMovie(movie, file.getAbsolutePath());
		    }

		} else {
		    dao.updateFilePath(file.getAbsolutePath(), movies);
		}

	    } catch (Exception e) {
		// TODO tratar
		e.printStackTrace();
	    }
	}
    }

    public String getMovieTitle(String fileName) {

	Pattern p = Pattern.compile(FILE_NAME_PATTERN);

	Matcher matcher = p.matcher(fileName);

	if (matcher.find()) {

	    return matcher.group(1).replaceAll("[^A-z]", " ").trim();

	}

	return fileName;
    }

    private Integer getMovieYear(String fileName) {

	Pattern p = Pattern.compile(FILE_NAME_PATTERN);

	Matcher matcher = p.matcher(fileName);

	if (matcher.find()) {

	    return Integer.valueOf(matcher.group(2));

	}

	return 1000;

    }

    @Autowired
    public void setYtsWebserviceClient(YtsWebserviceClient ytsWebserviceClient) {
	this.ytsWebserviceClient = ytsWebserviceClient;
    }

    @Autowired
    public void setDao(Dao dao) {
	this.dao = dao;
    }

}
