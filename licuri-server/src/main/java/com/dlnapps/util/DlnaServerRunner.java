package com.dlnapps.util;

import java.io.IOException;

import fi.iki.elonen.NanoHTTPD;

public class DlnaServerRunner {

    public static void run(Class<?> serverClass) {
	try {
	    executeInstance((NanoHTTPD) serverClass.newInstance());
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    private static NanoHTTPD server;

    public static void executeInstance(NanoHTTPD server) {

	DlnaServerRunner.server = server;

	try {
	    DlnaServerRunner.server.start();
	} catch (IOException ioe) {
	    System.err.println("Couldn't start server:\n" + ioe);
	    System.exit(-1);
	}

    }

    public static void stopServer() {

	server.stop();
	System.out.println("Server stopped.\n");
    }
}
