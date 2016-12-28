package com.dlnapps.model.action;

import org.fourthline.cling.model.meta.Action;
import org.fourthline.cling.model.meta.ActionArgument;
import org.fourthline.cling.model.meta.RemoteService;

public class SetAVTransportURI extends Action<RemoteService> {

    public SetAVTransportURI(String name, ActionArgument<RemoteService>[] arguments) {
	super(name, arguments);
    }

}
