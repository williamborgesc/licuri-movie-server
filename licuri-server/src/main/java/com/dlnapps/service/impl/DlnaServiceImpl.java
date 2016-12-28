package com.dlnapps.service.impl;

import java.net.Inet4Address;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.fourthline.cling.UpnpService;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.model.meta.RemoteService;
import org.fourthline.cling.model.types.UDAServiceId;
import org.fourthline.cling.support.avtransport.callback.Play;
import org.fourthline.cling.support.avtransport.callback.SetAVTransportURI;
import org.fourthline.cling.support.avtransport.callback.Stop;
import org.springframework.stereotype.Service;

import com.dlnapps.DeviceRegister;
import com.dlnapps.controller.DlnaHttpServer;
import com.dlnapps.service.DlnaService;
import com.dlnapps.xml.model.Device;
import com.dlnapps.xml.model.yts.Torrent;

@Service
public class DlnaServiceImpl implements DlnaService {

    private UpnpService upnpService;
    private DeviceRegister deviceRegister;

    public DlnaServiceImpl() {

	this.deviceRegister = DeviceRegister.getInstance();
	this.upnpService = deviceRegister.getUpnpService();
    }

    @Override
    public List<Device> listDevices() {

	List<Device> deviceList = new ArrayList<Device>();

	for (RemoteDevice remoteDevice : deviceRegister.getDeviceList()) {
	    deviceList.add(new Device(remoteDevice));
	}

	return deviceList;
    }

    @Override
    @SuppressWarnings({ "rawtypes", "unused" })
    @Deprecated
    public void play(Device device, Torrent torrent) {

	if(!(1==2)){
	    
	    throw new RuntimeException("Não usar mais este método");
	}
	
	RemoteDevice remoteDevice = getRemoteDevice(device);
	

	try {
	    
	    String fileName = null;
	    String resourceUri = String.format("%s/dlna?fileName=%s", getLocalHostHttpAddress(), URLEncoder.encode(fileName, "UTF-8"));

	    RemoteService service = getAVTrasportService(remoteDevice);

	    SetAVTransportURI setAVTransportURI = new SetAVTransportURI(service, resourceUri) {

		@Override
		public void success(ActionInvocation invocation) {
		    play(remoteDevice);
		}

		@Override
		public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
		    throw new RuntimeException("Não foi possivel reproduzir o conteudo");
		}

	    };
	    

	    
	    upnpService.getControlPoint().execute(setAVTransportURI);

	} catch (Exception exception) {

	    exception.printStackTrace();
	}

    }

    @SuppressWarnings("rawtypes")
    public void play(RemoteDevice device) {

	try {

	    RemoteService service = getAVTrasportService(device);

	    upnpService.getControlPoint().execute(new Play(service) {

		@Override
		public void success(ActionInvocation invocation) {
		    System.out.println("Playou");
		}

		@Override
		public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
		    throw new RuntimeException("Não foi possivel reproduzir o conteúdo");
		}
	    });

	} catch (Exception exception) {

	    exception.printStackTrace();
	}

    }

    @SuppressWarnings("rawtypes")
    @Override
    public void stop(Device device) {

	try {

	    RemoteService service = getAVTrasportService(getRemoteDevice(device));

	    upnpService.getControlPoint().execute(new Stop(service) {

		@Override
		public void success(ActionInvocation invocation) {
		    System.out.println("Stopou");
		}

		@Override
		public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
		    throw new RuntimeException("Não foi possivel Parar!");
		}
	    });

	} catch (Exception exception) {

	    exception.printStackTrace();
	}
    }

    private RemoteService getAVTrasportService(RemoteDevice device) {

	return device.findService(new UDAServiceId("AVTransport"));
    }

    private RemoteDevice getRemoteDevice(Device device) {

	for (RemoteDevice remoteDevice : deviceRegister.getDeviceList()) {
	    if (remoteDevice.getIdentity().getUdn().getIdentifierString().equals(device.getUuid())) {
		return remoteDevice;
	    }
	}

	return null;

    }

    private String getLocalHostHttpAddress() {

	try {

	    return String.format("http://%s:%s", Inet4Address.getLocalHost().getHostAddress(), DlnaHttpServer.PORT);

	} catch (UnknownHostException e) {
	    e.printStackTrace();
	}

	return "";
    }

}
