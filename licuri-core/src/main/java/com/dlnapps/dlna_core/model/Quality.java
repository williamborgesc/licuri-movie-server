package com.dlnapps.dlna_core.model;

import java.util.Arrays;

public enum Quality {

    HD_720P("720p"), HD_1080P("1080p"), HD_3D("3D"), ALL("All");

    private String value;

    private Quality(String value) {
	this.value = value;
    }

    public static Quality getQuality(String value) {

	return Arrays.asList(Quality.values())
		.stream()
		.filter((q) -> (q.getValue().equals(value)))
		.findFirst()
		.get();
    }

    public String getValue() {
	return value;
    }
}
