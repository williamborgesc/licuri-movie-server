package com.dlnapps.controller;

import java.io.InputStream;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.dlnapps.http.client.HttpClient;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.Method;
import fi.iki.elonen.NanoHTTPD.Response;

@Controller
@RequestMapping("/cover")
public class CoverAction extends AbstractHttpAction {

    public Response serve(String uri, Method method, Map<String, String> header, Map<String, String> parameters, Map<String, String> files) {

	String coverUri = parameters.get("coverUri");

	String mimeType = "image/jpeg";

	try {

	    if (validateUrl(coverUri)) {

		HttpGet get = new HttpGet(coverUri);

		// add header
		get.setHeader("Accept", mimeType);

		HttpResponse response = HttpClient.executeGet(get);

		return createResponse(Response.Status.OK, mimeType, response.getEntity().getContent());
	    }

	} catch (Exception e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

	return createResponse(Response.Status.INTERNAL_ERROR, NanoHTTPD.MIME_PLAINTEXT, "FORBIDDEN: Reading file failed.");

    }

    public boolean validateUrl(String url) {

	String regex = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";

	Pattern pattern = Pattern.compile(regex);

	Matcher matcher = pattern.matcher(url);

	return matcher.find();

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

}
