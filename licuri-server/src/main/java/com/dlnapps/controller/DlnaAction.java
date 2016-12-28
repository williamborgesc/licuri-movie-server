package com.dlnapps.controller;

import static com.dlnapps.config.ConfigurationLoader.getServerPort;
import static java.lang.String.format;
import static java.net.URLEncoder.encode;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.dlnapps.subtitles.LegendasTV;
import com.dlnapps.subtitles.LegendasTV.Subtitle;
import com.dlnapps.util.SubtitleChooser;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.Method;
import fi.iki.elonen.NanoHTTPD.Response;

@Controller
@RequestMapping("/dlna")
public class DlnaAction extends AbstractHttpAction {

    public Response serve(String uri, Method method, Map<String, String> header, Map<String, String> parameters, Map<String, String> files) {

	String fileName = parameters.get("fileName");

	String movieName = parameters.get("movieName");

	File file = new File(fileName);
	String mimeType = "video/mpeg";

	getSubtitle(fileName, movieName);

	return serveFile(uri, header, file, mimeType, fileName);
    }

    private String getSubtitle(String fileName, String movieName) {

	try {

	    File file = new File(getSrtName(fileName));

	    if (!file.exists()) {

		System.err.println("Baixando legenda:" + movieName);

		downloadSubtitle(file.getParent(), file.getName(), movieName);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}

	return null;
    }

    private void downloadSubtitle(String parentFolder, String fileName, String movieName) throws IllegalStateException, FileNotFoundException, IOException {

	List<Subtitle> subtitles = LegendasTV.search(movieName, "Português-BR");

	Subtitle choosenSubtitle = SubtitleChooser.choose(fileName, subtitles);

	LegendasTV.download(choosenSubtitle, parentFolder);

	Collection<File> files = FileUtils.listFiles(new File(parentFolder), new String[] { "srt" }, true);

	File subtitle = SubtitleChooser.chooseFile(fileName, files);

	// Se a legenda escolhida não tiver o mesmo nome do arquivo
	if (!fileName.equals(subtitle.getName())) {

	    System.out.printf("### RENOMEOU %s para %s\n", subtitle.getName(), fileName);
	    subtitle.renameTo(new File(parentFolder, fileName));
	}

    }

    // Announce that the file server accepts partial content requests
    private Response createResponse(Response.Status status, String mimeType, InputStream message) {
	return createResponse(status, mimeType, (Object) message);
    }

    // Announce that the file server accepts partial content requests
    private Response createResponse(Response.Status status, String mimeType, String message) {
	return createResponse(status, mimeType, (Object) message);
    }

    private Response createResponse(Response.Status status, String mimeType, Object message) {

	Response res = null;

	if (message instanceof InputStream) {

	    res = new Response(status, mimeType, (InputStream) message);

	} else if (message instanceof String) {

	    res = new Response(status, mimeType, (String) message);

	} else {
	    return new Response(message.toString());
	}

	res.addHeader("Accept-Ranges", "bytes");
	res.addHeader("TransferMode.DLNA.ORG", "Streaming");
	res.addHeader("ContentFeatures.DLNA.ORG", "DLNA.ORG_PN=MPEG;DLNA.ORG_OP=01;DLNA.ORG_FLAGS=01500000000000000000000000000000");

	return res;
    }

    /**
     * Serves file from homeDir and its' subdirectories (only). Uses only URI,
     * ignores all headers and HTTP parameters.
     * 
     * @param fileName
     */
    private Response serveFile(String uri, Map<String, String> header, File file, String mime, String fileName) {
	Response res;
	try {
	    // Calculate etag
	    String etag = Integer.toHexString((file.getAbsolutePath() + file.lastModified() + "" + file.length()).hashCode());

	    // Support (simple) skipping:
	    long startFrom = 0;
	    long endAt = -1;
	    String range = header.get("range");
	    if (range != null) {
		if (range.startsWith("bytes=")) {
		    range = range.substring("bytes=".length());
		    int minus = range.indexOf('-');
		    try {
			if (minus > 0) {
			    startFrom = Long.parseLong(range.substring(0, minus));
			    endAt = Long.parseLong(range.substring(minus + 1));
			}
		    } catch (NumberFormatException ignored) {
		    }
		}
	    }

	    // Change return code and add Content-Range header when skipping is
	    // requested
	    long fileLen = file.length();
	    if (range != null && startFrom >= 0) {
		if (startFrom >= fileLen) {
		    res = createResponse(Response.Status.RANGE_NOT_SATISFIABLE, NanoHTTPD.MIME_PLAINTEXT, "");
		    res.addHeader("Content-Range", "bytes 0-0/" + fileLen);
		    res.addHeader("ETag", etag);
		} else {
		    if (endAt < 0) {
			endAt = fileLen - 1;
		    }
		    long newLen = endAt - startFrom + 1;
		    if (newLen < 0) {
			newLen = 0;
		    }

		    final long dataLen = newLen;
		    FileInputStream fis = new FileInputStream(file) {
			@Override
			public int available() throws IOException {
			    return (int) dataLen;
			}
		    };
		    fis.skip(startFrom);

		    res = createResponse(Response.Status.PARTIAL_CONTENT, mime, fis);
		    res.addHeader("Content-Length", "" + dataLen);
		    res.addHeader("Content-Range", "bytes " + startFrom + "-" + endAt + "/" + fileLen);
		    res.addHeader("ETag", etag);
		}

	    } else {
		if (etag.equals(header.get("if-none-match")))
		    res = createResponse(Response.Status.NOT_MODIFIED, mime, "");
		else {
		    res = createResponse(Response.Status.OK, mime, new FileInputStream(file));
		    res.addHeader("Content-Length", "" + fileLen);
		    res.addHeader("ETag", etag);

		}
	    }
	    res.addHeader("CaptionInfo.sec", createCaptionInfoHeader(fileName));
	} catch (IOException ioe) {
	    res = createResponse(Response.Status.FORBIDDEN, NanoHTTPD.MIME_PLAINTEXT, "FORBIDDEN: Reading file failed.");
	}

	return res;
    }

    private String createCaptionInfoHeader(String fileName) throws UnsupportedEncodingException, UnknownHostException {

	String srtName = getSrtName(fileName);

	return format("http://%s:%s/srt?fileName=%s", InetAddress.getLocalHost().getHostAddress(), getServerPort(), encode(srtName, "UTF-8"));

    }

    private String getSrtName(String fileName) {
	return fileName.substring(0, fileName.lastIndexOf(".")) + ".srt";
    }

}
