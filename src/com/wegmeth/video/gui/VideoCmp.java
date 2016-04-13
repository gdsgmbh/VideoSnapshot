package com.wegmeth.video.gui;

import java.awt.Canvas;
import java.awt.Frame;
import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.player.embedded.videosurface.CanvasVideoSurface;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;

import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;

public class VideoCmp extends Composite{

	private EmbeddedMediaPlayerComponent mediaPlayerComponent;

	VideoCmp(Shell shell, int opt) {
		super(shell,opt);
		
		GridData gd_cmpVideo = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_cmpVideo.heightHint = 357;
		gd_cmpVideo.widthHint = 778;
		this.setLayoutData(gd_cmpVideo);
		NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(),"libs"+ File.separator + "VLC");
		Native.loadLibrary(RuntimeUtil.getLibVlcLibraryName(), LibVlc.class);
		
		Frame vFrame = SWT_AWT.new_Frame(this);
		Canvas canvas = new Canvas();
		canvas.setBackground(java.awt.Color.black);
		vFrame.add(canvas);
		MediaPlayerFactory fac = new MediaPlayerFactory();
		CanvasVideoSurface cVideo = fac.newVideoSurface(canvas);

		mediaPlayerComponent = new EmbeddedMediaPlayerComponent();
		this.mediaPlayerComponent.getMediaPlayer().setVideoSurface(cVideo);
		this.mediaPlayerComponent.getMediaPlayer().setRepeat(true);
	}

	
	public EmbeddedMediaPlayer getPlayer(){
		return mediaPlayerComponent.getMediaPlayer();
	}
}
