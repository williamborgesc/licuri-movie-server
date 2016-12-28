package com.dlnapps.controller;

import java.util.Map;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.web.bind.annotation.RequestMapping;

import com.dlnapps.main.MainApplication;

import fi.iki.elonen.NanoHTTPD;

public class DlnaHttpServer extends NanoHTTPD {

    public static final int PORT = 8000;

    public DlnaHttpServer() {
	super(PORT);
    }

    @Override
    public Response serve(String uri, Method method, Map<String, String> header, Map<String, String> parameters, Map<String, String> files) {

	try {

	    Class<?> classe;

	    classe = getAction(uri);

	    if (classe == null) {

		return null;
	    }

	    return ((AbstractHttpAction) MainApplication.getInstance().getApplicationContext().getBean(classe)).serve(uri, method, header, parameters, files);

	} catch (ClassNotFoundException e) {
	    e.printStackTrace();
	}

	return null;
    }

    private Class<?> getAction(String uri) throws ClassNotFoundException {

	ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(true);

	Class<RequestMapping> requestMappingClass = RequestMapping.class;

	scanner.addIncludeFilter(new AnnotationTypeFilter(requestMappingClass));

	for (BeanDefinition bd : scanner.findCandidateComponents("com.dlnapps.controller")) {

	    Class<?> actionClass = Class.forName(bd.getBeanClassName());

	    for (String value : actionClass.getAnnotation(requestMappingClass).value()) {

		if (value.equals(uri)) {

		    return actionClass;
		}
	    }

	}

	return null;
    }

}
