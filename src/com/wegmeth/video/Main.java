package com.wegmeth.video;

import java.io.File;
import com.wegmeth.video.gui.Videoplayer;

/**
 * @author Jan Wegmeth wegmeth.com info@wegmeth.com
 *
 */
public class Main {
	public static void main(String args[]) {
		VideoController vc;
		if (args.length > 1 && args[0] != null && args[1] != null) {
			vc = new VideoController(new Videoplayer(), args[1], args[0]);
		} else if (args.length > 0 && args[0] != null)
			vc = new VideoController(new Videoplayer(), args[0]);
		else
			vc = new VideoController(new Videoplayer());
		vc.open();
	}
}
