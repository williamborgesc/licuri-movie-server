package com.dlnapps.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class MovieServiceTest {

    @Test
    public void deveEncontrarONomeDoFilmeComBaseNoNomeDoArquivo() {
	
	MovieService service = new MovieService();
	
	assertEquals("A Space Odyssey", service.getMovieTitle("A.Space.Odyssey"));
	assertEquals("A Space Odyssey", service.getMovieTitle("2001.A.Space.Odyssey.1968.720p.BrRip.x264.YIFY.mp4"));
    }

}
