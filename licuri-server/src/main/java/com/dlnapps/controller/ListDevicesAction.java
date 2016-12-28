package com.dlnapps.controller;

import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.dlnapps.service.DlnaService;
import com.dlnapps.xml.model.DeviceList;

import fi.iki.elonen.NanoHTTPD.Method;
import fi.iki.elonen.NanoHTTPD.Response;
import fi.iki.elonen.NanoHTTPD.Response.Status;

@Controller
@RequestMapping("/dlna/list-devices")
public class ListDevicesAction extends AbstractHttpAction {

    @Autowired
    private DlnaService dlnaService;

    @Override
    public Response serve(String uri, Method method, Map<String, String> header, Map<String, String> parameters, Map<String, String> files) {

	try {

	    JAXBContext jaxbContext = JAXBContext.newInstance(DeviceList.class);
	    Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

	    jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);	

	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    
	    DeviceList deviceList = new DeviceList();
	    
	    deviceList.setDevices(dlnaService.listDevices());

	    jaxbMarshaller.marshal(deviceList, baos);

	    return new Response(Status.OK, "text/xml", baos.toString());

	} catch (JAXBException e) {
	    e.printStackTrace();
	}

	return null;
    }

}
