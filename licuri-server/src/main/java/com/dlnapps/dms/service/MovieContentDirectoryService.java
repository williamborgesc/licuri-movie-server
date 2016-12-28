package com.dlnapps.dms.service;

import static com.dlnapps.config.ConfigurationLoader.getServerPort;
import static java.lang.String.format;
import static java.net.InetAddress.getLocalHost;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.fourthline.cling.support.contentdirectory.AbstractContentDirectoryService;
import org.fourthline.cling.support.contentdirectory.ContentDirectoryErrorCode;
import org.fourthline.cling.support.contentdirectory.ContentDirectoryException;
import org.fourthline.cling.support.contentdirectory.DIDLParser;
import org.fourthline.cling.support.model.BrowseFlag;
import org.fourthline.cling.support.model.BrowseResult;
import org.fourthline.cling.support.model.DIDLAttribute;
import org.fourthline.cling.support.model.DIDLContent;
import org.fourthline.cling.support.model.DIDLObject.Property.SEC.CAPTIONINFO;
import org.fourthline.cling.support.model.DIDLObject.Property.SEC.TYPE;
import org.fourthline.cling.support.model.DIDLObject.Property.UPNP.ALBUM_ART_URI;
import org.fourthline.cling.support.model.DIDLObject.Property.UPNP.ICON;
import org.fourthline.cling.support.model.Protocol;
import org.fourthline.cling.support.model.ProtocolInfo;
import org.fourthline.cling.support.model.Res;
import org.fourthline.cling.support.model.SortCriterion;
import org.fourthline.cling.support.model.container.StorageFolder;
import org.fourthline.cling.support.model.item.Movie;
import org.seamless.util.MimeType;

import com.dlnapps.dao.Dao;
import com.dlnapps.dao.MovieFilter;
import com.dlnapps.dao.MovieFilter.Builder;
import com.dlnapps.main.MainApplication;
import com.dlnapps.service.MovieService;

public class MovieContentDirectoryService extends AbstractContentDirectoryService {

    private Map<String, String> BROWSE_TYPES = null;

    private Dao dao;
    private MovieService movieService;

    public MovieContentDirectoryService() {

	BROWSE_TYPES = new TreeMap<String, String>();

	BROWSE_TYPES.put("1", "All");
	BROWSE_TYPES.put("2", "Rating");
	BROWSE_TYPES.put("3", "Genre");
	BROWSE_TYPES.put("4", "Language");

	dao = MainApplication.getInstance().getApplicationContext().getBean(Dao.class);
	movieService = MainApplication.getInstance().getApplicationContext().getBean(MovieService.class);
	
	movieService.updateMoviesOnDB();

    }

    @Override
    public BrowseResult browse(String objectID, BrowseFlag browseFlag, String filter, long firstResult, long maxResults, SortCriterion[] orderby) throws ContentDirectoryException {

	try {

	    DIDLContent didl;

	    if (objectID.equals("0") || objectID.equals("15")) {

		didl = browse();

	    } else if (objectID.equals("1")) {

		didl = browseAllMovies(objectID);

	    } else if (objectID.equals("2")) {

		didl = rating();

	    } else if (objectID.equals("3")) {

		didl = genres();

	    } else if (objectID.equals("4")) {

		didl = languages();

	    } else {

		didl = browse(objectID);
	    }

	    String result = new DIDLParser().generate(didl);

	    return new BrowseResult(result, didl.getCount(), didl.getCount());

	} catch (Exception ex) {
	    ex.printStackTrace();
	    throw new ContentDirectoryException(ContentDirectoryErrorCode.CANNOT_PROCESS, ex.toString());
	}
    }

    private DIDLContent rating() {
	DIDLContent didl = new DIDLContent();

	for (String rating : dao.getAllRatings()) {

	    StorageFolder folder = new StorageFolder();

	    folder.setTitle(rating);
	    folder.setId("rating:" + rating);
	    folder.setParentID("2");

	    didl.addContainer(folder);
	}
	return didl;
    }

    private DIDLContent genres() {

	DIDLContent didl = new DIDLContent();

	for (String genre : dao.getAllGenres()) {

	    StorageFolder folder = new StorageFolder();

	    folder.setTitle(genre);
	    folder.setId("genre:" + genre);
	    folder.setParentID("3");

	    didl.addContainer(folder);
	}
	return didl;

    }

    private DIDLContent languages() {
	DIDLContent didl = new DIDLContent();

	for (String language : dao.getAllLanguages()) {

	    StorageFolder folder = new StorageFolder();

	    folder.setTitle(language);
	    folder.setId("language:" + language);
	    folder.setParentID("4");

	    didl.addContainer(folder);
	}
	return didl;
    }

    private DIDLContent browse() {

	DIDLContent didl = new DIDLContent();

	for (Entry<String, String> browseType : BROWSE_TYPES.entrySet()) {

	    StorageFolder folder = new StorageFolder();

	    folder.setTitle(browseType.getValue());
	    folder.setId(browseType.getKey());
	    folder.setParentID("0");

	    didl.addContainer(folder);
	}
	return didl;

    }

    private DIDLContent browse(String objectId) {

	String[] parts = objectId.split(":");

	String filterType = parts[0];

	String filterValue = parts[1];

	Builder builder = MovieFilter.builder();

	switch (filterType) {
	case "genre":

	    builder.genre(filterValue);

	    break;
	case "language":

	    builder.language(filterValue);

	    break;
	case "rating":

	    builder.rating(filterValue);

	    break;

	default:
	    break;
	}

	return createDidlContent(objectId, dao.find(builder.build()));
    }

    private DIDLContent browseAllMovies(String parentId) {

	return createDidlContent(parentId, dao.getAll());
    }

    private DIDLContent createDidlContent(String parentId, List<com.dlnapps.xml.model.yts.Movie> movies) {

	DIDLContent didl = new DIDLContent();

	int id = 1;

	for (com.dlnapps.xml.model.yts.Movie ytsMovie : movies) {
	    try {

		Movie movie = new Movie();

		movie.setTitle(ytsMovie.getTitle());
		movie.setDescription(ytsMovie.getTitle_long());
		movie.setLanguage(ytsMovie.getLanguage());
		movie.setId(String.valueOf(id++));
		movie.setParentID(parentId);

		List<Res> resources = new ArrayList<Res>();

		ProtocolInfo protocolInfo = new ProtocolInfo(new MimeType("video", "mpeg"));

		String urlFileName = ytsMovie.getFile_path();

		String srtName = urlFileName.substring(0, urlFileName.lastIndexOf(".")) + ".srt";

		String srtUrl = format("http://%s:%s/srt?fileName=%s", getLocalHost().getHostAddress(), getServerPort(), encode(srtName));

		String movieUrl = format("http://%s:%s/dlna?fileName=%s&movieName=%s", getLocalHost().getHostAddress(), getServerPort(), encode(urlFileName), encode(ytsMovie.getTitle()));
		
		Res movieResource = new Res(protocolInfo, new File(urlFileName).length(), movieUrl);

		Res srtResource = new Res(); 
		Res captionResource = new Res();
		Res coverResource = new Res();

		protocolInfo = new ProtocolInfo(new MimeType("text", "srt"));

		srtResource.setProtocolInfo(protocolInfo);
		srtResource.setValue(srtUrl);

		captionResource.setProtocolInfo(protocolInfo);
		captionResource.setValue(srtUrl);

		ProtocolInfo info = new ProtocolInfo(Protocol.HTTP_GET, "", "image/jpeg",
			"DLNA.ORG_PN=JPEG_TN;DLNA.ORG_OP=00;DLNA.ORG_CI=1;DLNA.ORG_FLAGS=00D00000000000000000000000000000");

		coverResource.setProtocolInfo(info);

		String coverUrl = String.format("http://%s:%s/cover?coverUri=%s", getLocalHost().getHostAddress(), getServerPort(), ytsMovie.getMedium_cover_image());
		coverResource.setValue(coverUrl);

		resources.add(movieResource);
		resources.add(captionResource);
		resources.add(srtResource);
		resources.add(coverResource);

		movie.setResources(resources);

		CAPTIONINFO captionInfo = null;
		captionInfo = new CAPTIONINFO(new URI(srtUrl));

		captionInfo.addAttribute(new TYPE(new DIDLAttribute("http://www.sec.co.kr/", "sec", "srt")));

		movie.addProperty(captionInfo);

		if (ytsMovie.getMedium_cover_image() != null) {

		    URI uri = new URI(coverUrl);

		    ICON icon = new ICON(uri);

		    ALBUM_ART_URI albumArtUri = new ALBUM_ART_URI(uri);

		    movie.addProperty(icon);
		    movie.addProperty(albumArtUri);
		}

		didl.addItem(movie);

	    } catch (URISyntaxException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    } catch (UnknownHostException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    } catch (UnsupportedEncodingException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    } catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	}
	return didl;

    }

    @Override
    public BrowseResult search(String containerId, String searchCriteria, String filter, long firstResult, long maxResults, SortCriterion[] orderBy)
	    throws ContentDirectoryException {
	return super.search(containerId, searchCriteria, filter, firstResult, maxResults, orderBy);
    }

    private String encode(String string) throws UnsupportedEncodingException {
	return URLEncoder.encode(string, "UTF-8");
    }
}
