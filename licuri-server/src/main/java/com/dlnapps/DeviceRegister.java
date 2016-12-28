package com.dlnapps;

import java.util.ArrayList;
import java.util.List;

import org.fourthline.cling.UpnpService;
import org.fourthline.cling.UpnpServiceImpl;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.registry.DefaultRegistryListener;
import org.fourthline.cling.registry.Registry;

public class DeviceRegister extends DefaultRegistryListener {

    public String url = "http://192.168.0.4:8000";

    private List<RemoteDevice> deviceList;

    private static DeviceRegister instance;

    private UpnpService upnpService;

    private DeviceRegister() {
	deviceList = new ArrayList<RemoteDevice>();
	upnpService = new UpnpServiceImpl();
    }

    public static DeviceRegister getInstance() {

	if (instance == null) {
	    instance = new DeviceRegister();
	}
	return instance;
    }

    public List<RemoteDevice> getDeviceList() {
	return deviceList;
    }

    public UpnpService getUpnpService() {

	return upnpService;
    }

    @Override
    public void remoteDeviceAdded(Registry registry, RemoteDevice device) {

	System.out.println("Adicionado : " + device.getDisplayString());
	
	deviceList.add(device);
    }

    @Override
    public void remoteDeviceRemoved(Registry registry, RemoteDevice device) {
	deviceList.remove(device);
    }

}
