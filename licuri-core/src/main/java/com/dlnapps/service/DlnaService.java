package com.dlnapps.service;

import java.util.List;

import com.dlnapps.xml.model.Device;
import com.dlnapps.xml.model.yts.Torrent;

public interface DlnaService {

    List<Device> listDevices();

    void play(Device device, Torrent torrent);

    void stop(Device device);

}
