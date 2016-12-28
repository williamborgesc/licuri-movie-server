package com.dlnapps.service;

import java.util.Observable;

import com.dlnapps.xml.model.yts.Torrent;


public interface TorrentService {
    
    Observable start(Torrent torrent);

    void stop();

}
