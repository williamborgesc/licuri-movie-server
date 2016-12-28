package com.dlnapps.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.Method;
import fi.iki.elonen.NanoHTTPD.Response;

@Controller
@RequestMapping("/srt")
public class SrtAction extends AbstractHttpAction {

    public Response serve(String uri, Method method, Map<String, String> header, Map<String, String> parameters, Map<String, String> files) {


	String fileName = parameters.get("fileName");

	File file = new File(fileName);

	String mimeType = "text/srt";

	System.out.println("LEGENDA SOLICITADA: " + fileName);

	return serveFile(uri, header, file, mimeType);
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
	res.addHeader("ContentFeatures.DLNA.ORG", "DLNA.ORG_PN=SRT;DLNA.ORG_OP=01;DLNA.ORG_FLAGS=01500000000000000000000000000000");

	return res;
    }

    /**
     * Serves file from homeDir and its' subdirectories (only). Uses only URI,
     * ignores all headers and HTTP parameters.
     */
    private Response serveFile(String uri, Map<String, String> header, File file, String mime) {
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
	} catch (IOException ioe) {
	    res = createResponse(Response.Status.FORBIDDEN, NanoHTTPD.MIME_PLAINTEXT, "FORBIDDEN: Reading file failed.");
	}

	return res;
    }

}
