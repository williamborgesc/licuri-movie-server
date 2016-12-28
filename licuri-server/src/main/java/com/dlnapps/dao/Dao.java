package com.dlnapps.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

import com.dlnapps.xml.model.yts.Movie;

@Repository
public class Dao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
	this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void insertMovies(List<Movie> movies) {

	String sql = "INSERT INTO MOVIE (IMDB_CODE, TITLE, LONG_TITLE, RATING, YEAR, LANGUAGE, RT_CRITICS_SCORE, RT_AUDIENCE_SCORE)"
		+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

	List<Object[]> parameters = new ArrayList<Object[]>();

	for (Movie movie : movies) {

	    insertGenre(movie.getGenres());

	    parameters.add(new Object[] { movie.getImdb_code(), movie.getTitle(), movie.getTitle_long(), movie.getRating(), movie.getYear(),
		    movie.getLanguage(), movie.getRt_critics_score(), movie.getRt_audience_score(), true, false });

	}

	if (parameters.size() > 0) {
	    jdbcTemplate.batchUpdate(sql, parameters);
	}

	addGenresToMovies(movies);

    }

    public void insertMovie(Movie movie) {

	insertMovie(movie, null);
    }

    public void insertMovie(Movie movie, String filePath) {

	insertGenre(movie.getGenres());

	String sql = "INSERT INTO MOVIE (IMDB_CODE, TITLE, LONG_TITLE, RATING, YEAR, LANGUAGE, RT_CRITICS_SCORE, RT_AUDIENCE_SCORE, COVER_IMAGE_URL, FILE_PATH)"
		+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

	Object[] parameters = { movie.getImdb_code(), movie.getTitle(), movie.getTitle_long(), movie.getRating(), movie.getYear(), movie.getLanguage(),
		movie.getRt_critics_score(), movie.getRt_audience_score(), movie.getMedium_cover_image(), filePath };

	jdbcTemplate.update(sql, parameters);

	addGenreToMovie(movie);
    }

    public List<Movie> getAll() {
	
	return find(MovieFilter.builder().build());
    }

    public void updateFilePath(String filePath, Movie movie) {

	updateFilePath(filePath, Arrays.asList(movie));
    }

    public void updateFilePath(String filePath, List<Movie> movies) {

	String sql = "UPDATE movie SET file_path = ? WHERE imdb_code = ?";

	List<Object[]> parameters = new ArrayList<Object[]>();

	for (Movie movie : movies) {

	    parameters.add(new Object[] { filePath, movie.getImdb_code() });

	}
	if (parameters.size() > 0) {
	    jdbcTemplate.batchUpdate(sql, parameters);
	}

    }

    public void removeMovie(Movie movie) {

	String sql = "DELETE FROM movie WHERE nvl(imdb_code, id) = ?";

	jdbcTemplate.update(sql, movie.getImdb_code());

    }

    private void addGenresToMovies(List<Movie> movies) {
	for (Movie movie : movies) {
	    addGenreToMovie(movie);
	}
    }

    private void addGenreToMovie(Movie movie) {

	String sql = "INSERT INTO MOVIE_GENRE(FK_MOVIE,  FK_GENRE) values ((SELECT ID FROM MOVIE WHERE IMDB_CODE = ?), (SELECT ID FROM GENRE WHERE NAME = ?))";

	List<Object[]> parameters = new ArrayList<Object[]>();

	for (String genre : movie.getGenres()) {

	    parameters.add(new Object[] { movie.getImdb_code(), genre });
	}

	if (parameters.size() > 0) {
	    jdbcTemplate.batchUpdate(sql, parameters);
	}

    }

    private void insertGenre(List<String> genres) {
	String sql = "INSERT INTO  GENRE (NAME) VALUES (?)";

	List<Object[]> parameters = new ArrayList<Object[]>();

	for (String genre : genres) {
	    if (!genreExists(genre)) {
		parameters.add(new Object[] { genre });
	    }
	}

	if (parameters.size() > 0) {
	    jdbcTemplate.batchUpdate(sql, parameters);
	}

    }

    private boolean genreExists(String genreName) {

	String sql = "SELECT COUNT(*) FROM GENRE WHERE NAME = ? ";

	int total = jdbcTemplate.queryForInt(sql, genreName);

	return total == 1;
    }

    private List<String> getGenres(Movie movie) {

	StringBuilder sql = new StringBuilder();

	sql.append("SELECT g.name ");
	sql.append(" FROM movie m ");
	sql.append(" INNER JOIN movie_genre mg ");
	sql.append(" 	ON mg.fk_movie = m.id ");
	sql.append(" INNER JOIN genre g ");
	sql.append(" 	ON mg.fk_genre = g.id ");
	sql.append(" WHERE m.imdb_code = ? ");

	ParameterizedBeanPropertyRowMapper<String> genreRowMapper = new ParameterizedBeanPropertyRowMapper<String>() {

	    @Override
	    public String mapRow(ResultSet rs, int rowNumber) throws SQLException {

		return rs.getString("name");
	    }

	};
	return jdbcTemplate.query(sql.toString(), genreRowMapper, movie.getImdb_code());
    }

    public void shutdown() throws SQLException {

	Statement st = jdbcTemplate.getDataSource().getConnection().createStatement();

	st.execute("SHUTDOWN");

	jdbcTemplate.getDataSource().getConnection().close();
    }

    public List<Movie> find(MovieFilter filter) {

	StringBuilder sql = new StringBuilder();

	sql.append("SELECT nvl(m.imdb_code, m.id) imdb_code,");
	sql.append("       m.title, ");
	sql.append("       m.long_title, ");
	sql.append("       m.rating, ");
	sql.append("       m.year,  ");
	sql.append("       m.language,  ");
	sql.append("       m.rt_critics_score, ");
	sql.append("       m.rt_audience_score, ");
	sql.append("       m.cover_image_url, ");
	sql.append("       m.file_path ");
	sql.append(" FROM movie m ");
	sql.append(" WHERE 1 = 1 ");

	if (StringUtils.isNotBlank(filter.getTitle())) {
	    sql.append(" AND upper(m.title) like ? ");
	}

	if (filter.getYear() != null) {
	    sql.append(" AND m.year = ? ");
	}

	if (StringUtils.isNotBlank(filter.getGenre())) {
	    sql.append(" AND exists (select * from movie_genre mg inner join genre g on mg.fk_genre = g.id where g.name = ? and mg.fk_movie = m.id) ");
	}

	if (StringUtils.isNotBlank(filter.getLanguage())) {
	    sql.append(" AND m.language = ? ");
	}

	if (StringUtils.isNotBlank(filter.getRating())) {
	    sql.append(" AND m.rating = ? ");
	}

	sql.append(" order by m.title ");

	List<SqlParameterValue> parameters = new ArrayList<SqlParameterValue>();

	if (StringUtils.isNotBlank(filter.getTitle())) {
	    parameters.add(new SqlParameterValue(Types.VARCHAR, "%" + filter.getTitle().toUpperCase().trim().replaceAll("[^A-z] ", " ") + "%"));
	}

	if (filter.getYear() != null) {
	    parameters.add(new SqlParameterValue(Types.NUMERIC, filter.getYear()));
	}

	if (StringUtils.isNotBlank(filter.getGenre())) {
	    parameters.add(new SqlParameterValue(Types.VARCHAR, filter.getGenre()));
	}

	if (StringUtils.isNotBlank(filter.getLanguage())) {
	    parameters.add(new SqlParameterValue(Types.VARCHAR, filter.getLanguage()));
	}

	if (StringUtils.isNotBlank(filter.getRating())) {
	    parameters.add(new SqlParameterValue(Types.VARCHAR, filter.getRating()));
	}

	List<Movie> movies = jdbcTemplate.query(sql.toString(), new ParameterizedBeanPropertyRowMapper<Movie>() {

	    @Override
	    public Movie mapRow(ResultSet rs, int rowNumber) throws SQLException {

		Movie movie = new Movie();

		movie.setImdb_code(rs.getString("imdb_code"));
		movie.setTitle(rs.getString("title"));
		movie.setTitle_long(rs.getString("long_title"));
		movie.setRating(rs.getDouble("rating"));
		movie.setYear(rs.getInt("year"));
		movie.setLanguage(rs.getString("language"));
		movie.setRt_critics_score(rs.getInt("rt_critics_score"));
		movie.setRt_audience_score(rs.getInt("rt_audience_score"));
		movie.setMedium_cover_image(rs.getString("cover_image_url"));
		movie.setFile_path(rs.getString("file_path"));

		movie.setGenres(getGenres(movie));

		return movie;
	    }

	}, parameters.toArray());

	return movies;
    }

    public List<String> getAllGenres() {

	return jdbcTemplate.queryForList("SELECT name FROM genre ", String.class);

    }

    public List<String> getAllRatings() {

	return jdbcTemplate.queryForList("SELECT distinct rating FROM movie ", String.class);

    }

    public List<String> getAllLanguages() {

	return jdbcTemplate.queryForList("SELECT distinct language FROM movie ", String.class);

    }

}
