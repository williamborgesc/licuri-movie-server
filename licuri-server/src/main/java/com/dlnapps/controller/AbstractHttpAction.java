package com.dlnapps.controller;

import java.util.Map;

import fi.iki.elonen.NanoHTTPD.Method;
import fi.iki.elonen.NanoHTTPD.Response;

public abstract class AbstractHttpAction{

    public abstract Response serve(String uri, Method method, Map<String, String> header, Map<String, String> parameters, Map<String, String> files);
}
