package com.dlnapps.util;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class SubtitleChooserTest {

    @Test
    public void deveEscolherONomeMaisParecido() throws UnsupportedEncodingException {

	// given
	List<File> files = new ArrayList<File>();

	files.add(new File("American.Sniper.2014.BluRay+BDRip+BRRip"));
	files.add(new File("American.Sniper.2014.1080p.BluRay.REMUX.AVC.TrueHD.7.1.Atmos-RARBG"));
	files.add(new File("American.Ninja.3.Blood.Hunt.1989.720p.BluRay.x264.YIFY"));
	files.add(new File("American.Ninja.2.The.Confrontation.1987.720p.BluRay.x264.YIFY"));
	files.add(new File("American.Ninja.1985.720p.BluRay.x264.YIFY"));
	files.add(new File("American.Odyssey.S01E06.720p.HDTV.X264-DIMENSION-FUM-LOL-KiNGS"));
	files.add(new File("The.Americans.S03.1080p.WEB-DL"));
	files.add(new File("The.Americans.S03.720p.WEB-DL"));
	files.add(new File("The.Americans.2013.S03.HDTV.XviD"));
	files.add(new File("The.Americans.2013.S03.720p.HDTV.x264"));
	files.add(new File("The.Americans.2013.S03.HDTV.x264"));
	files.add(new File("American.Odyssey.S01E01.1080p.WEB-DL.DD5.1.H.264-KiNGS"));
	files.add(new File("American.Crime.S01E10.HDTV.x264-LOL-FUM-DIMENSION-PSA-KiNGS"));
	files.add(new File("American.Odyssey.S01E04.HDTV.x264-LOL-FUM-KiNGS"));
	files.add(new File("American.Odyssey.S01E03.HDTV.x264-LOL-FUM-KiNGS"));
	files.add(new File("American.Odyssey.S01E02.HDTV.x264-LOL-FUM-KiNGS"));
	files.add(new File("American.Odyssey.S01E05.HDTV.x264-LOL-DIMENSION-FUM-KiNGS"));
	files.add(new File("American.Crime.S01E09.HDTV.x264-2HD-FUM-PSA-KiNGS"));
	files.add(new File("American.Odyssey.S01E04.720p.HDTV.X264-DIMENSION"));
	files.add(new File("American.Sniper.2014.1080p.WEB-DL.x264.AC3-JYK"));
	files.add(new File("American.Crime.S01E08.HDTV.x264-LOL-FUM-DIMENSION-RMTEAM-KiNGS"));
	files.add(new File("The.Americans.2013.S03E13.HDTV.x264-KILLERS-FUM-KILLERS-NTb"));
	files.add(new File("American.History.X.1998.1080p.BluRay.x264.anoXmous."));

	// when

	File file = SubtitleChooser.chooseFile("American.Sniper.2014.720p.BluRay.x264.YIFY", files);

	// then
	Assert.assertEquals("American.Sniper.2014.BluRay+BDRip+BRRip", file.getName());

    }

}
