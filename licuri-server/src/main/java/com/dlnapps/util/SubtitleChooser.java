package com.dlnapps.util;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.dlnapps.subtitles.LegendasTV.Subtitle;

public class SubtitleChooser {

    private static final String FILE_NAME_PATTERN = "(.*?)([0-9]{4}).*([0-9]{4})?";

    public static Subtitle choose(String fileName, List<Subtitle> subtitles) {

	Subtitle similar = null;
	int maxPoints = 0;

	String movieTitle = getMovieTitle(fileName);
	int movieYear = getMovieYear(fileName);
	String releaseType = getReleaseType(fileName);
	String quality = getQuality(fileName);
	boolean isFromYifi = fileName.toUpperCase().contains("YIFY");

	for (Subtitle subtitle : subtitles) {

	    int points = 0;

	    if (subtitle.isHighlight()) {

		points++;

	    }

	    if (movieTitle.toUpperCase().equals(getMovieTitle(subtitle.getFileName()).toUpperCase())) {

		points = points + 5;

	    }

	    if (movieYear == getMovieYear(subtitle.getFileName())) {

		points++;

	    }

	    if (releaseType.toUpperCase().equals(getReleaseType(subtitle.getFileName()))) {

		points++;

	    }

	    if (quality.equals(getQuality(subtitle.getFileName()))) {

		points++;

	    }

	    if (isFromYifi == subtitle.getFileName().toUpperCase().contains("YIFY")) {

		points = points + 2;

	    }

	    if (points > maxPoints) {

		maxPoints = points;
		similar = subtitle;
	    }
	}

	return similar;
    }

    public static File chooseFile(String fileName, Collection<File> subtitles) {

	File similar = null;
	int maxPoints = 0;

	String movieTitle = getMovieTitle(fileName);
	int movieYear = getMovieYear(fileName);
	String releaseType = getReleaseType(fileName);
	String quality = getQuality(fileName);
	boolean isFromYifi = fileName.toUpperCase().contains("YIFY");

	for (File subtitle : subtitles) {

	    int points = 0;

	    if (movieTitle.toUpperCase().equals(getMovieTitle(subtitle.getName()).toUpperCase())) {

		points = points + 5;

	    }

	    if (movieYear == getMovieYear(subtitle.getName())) {

		points++;

	    }

	    if (releaseType.toUpperCase().equals(getReleaseType(subtitle.getName()))) {

		points++;

	    }

	    if (quality.equals(getQuality(subtitle.getName()))) {

		points++;

	    }

	    if (isFromYifi == subtitle.getName().toUpperCase().contains("YIFY")) {

		points = points + 2;

	    }

	    if (points > maxPoints) {

		maxPoints = points;
		similar = subtitle;
	    }
	}

	return similar;
    }

    private static String getQuality(String fileName) {

	if (fileName.toUpperCase().contains("720P")) {

	    return "720P";

	} else if (fileName.toUpperCase().contains("1080P")) {

	    return "1080P";

	} else if (fileName.toUpperCase().contains("HD")) {

	    return "HD";
	}

	return "";

    }

    private static String getReleaseType(String fileName) {

	if (fileName.toUpperCase().contains("BLURAY")) {

	    return "BLURAY";

	} else if (fileName.toUpperCase().contains("BRRIP")) {

	    return "BRRIP";

	} else if (fileName.toUpperCase().contains("BDRIP")) {

	    return "BDRIP";

	} else if (fileName.toUpperCase().contains("DVDRIP")) {

	    return "DVDRIP";

	} else if (fileName.toUpperCase().contains("HDTV")) {

	    return "HDTV";

	} else if (fileName.toUpperCase().contains("SDTV")) {

	    return "SDTV";

	} else if (fileName.toUpperCase().contains("TVRIP")) {

	    return "TVRIP";

	} else if (fileName.toUpperCase().contains("VHSRIP")) {

	    return "VHSRIP";

	} else if (fileName.toUpperCase().contains("WP")) {

	    return "WP";

	} else if (fileName.toUpperCase().contains("TS ")) {

	    return "TS ";

	} else if (fileName.toUpperCase().contains("CAM")) {

	    return "CAM";
	}

	return "";
    }

    private static String getMovieTitle(String fileName) {

	Pattern p = Pattern.compile(FILE_NAME_PATTERN);

	Matcher matcher = p.matcher(fileName);

	if (matcher.find()) {

	    return matcher.group(1).replaceAll("[^A-z]", " ");

	}

	return fileName;
    }

    private static Integer getMovieYear(String fileName) {

	Pattern p = Pattern.compile(FILE_NAME_PATTERN);

	Matcher matcher = p.matcher(fileName);

	if (matcher.find()) {

	    return Integer.valueOf(matcher.group(2));

	}

	return 1000;

    }

}
