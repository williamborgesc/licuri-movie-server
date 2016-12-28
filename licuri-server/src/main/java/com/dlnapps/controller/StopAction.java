package com.dlnapps.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.dlnapps.service.DlnaService;
import com.dlnapps.xml.model.Device;

import fi.iki.elonen.NanoHTTPD.Method;
import fi.iki.elonen.NanoHTTPD.Response;
import fi.iki.elonen.NanoHTTPD.Response.Status;

@Controller
@RequestMapping("/dlna/stop")
public class StopAction extends AbstractHttpAction {

    @Autowired
    private DlnaService dlnaService;

    @Override
    public Response serve(String uri, Method method, Map<String, String> header, Map<String, String> parameters, Map<String, String> files) {

	try {

	    String uuid = parameters.get("uuid");


	    dlnaService.stop(new Device(uuid));

	    return new Response("Stop Called Sucessifully!!!");

	} catch (Exception e) {
	    e.printStackTrace();
	    return new Response(Status.INTERNAL_ERROR, "text/html", e.getMessage());
	}

    }
}
