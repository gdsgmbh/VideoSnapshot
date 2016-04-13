package com.wegmeth.video.gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import java.util.Calendar;
import java.util.GregorianCalendar;

import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

/**
 * @author Jan Wegmeth 
 * Main Dialog
 *
 */
public class Videoplayer {
	private Text qrURL;
	private Button btnCancel;
	private Button btnOk;
	private EmbeddedMediaPlayer mediaPlayer;
	private Shell shell;
	private Runnable run;
	private DateTime dateTime;

	private Image imgPlay;
	private Image imgPause;
	private Button optionThumb;
	
	
	public void close(){
		shell.dispose();
	}
	
	/**
	 * Main Dialog with all Controls and Options for Thumbnail
	 */
	public Videoplayer (){
		shell = new Shell();
		imgPlay= new Image(shell.getDisplay(),getClass().getResourceAsStream("/com/wegmeth/video/res/play.png"));
		imgPause= new Image(shell.getDisplay(),getClass().getResourceAsStream("/com/wegmeth/video/res/pause.png"));
		
		shell.setSize(600, 700);
		shell.setLayout(new GridLayout(1, false));
		this.shell.addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent arg0) {
				mediaPlayer.release();
			}
		});
		
		VideoCmp cmpVideo = new VideoCmp(shell, SWT.EMBEDDED);
		mediaPlayer = cmpVideo.getPlayer();
		
		Composite cmpControls = new Composite(shell, SWT.NONE);
		cmpControls.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		cmpControls.setLayout(new GridLayout(1, false));
		
		Composite cmpOptions = new Composite(shell, SWT.NONE);
		GridData gd_cmpOptions = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_cmpOptions.widthHint = 777;
		cmpOptions.setLayoutData(gd_cmpOptions);
		cmpOptions.setLayout(new GridLayout(1, false));
		
		final Scale scale = new Scale(cmpControls, SWT.NONE);
		GridData gd_scale = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_scale.widthHint = 767;
		scale.setLayoutData(gd_scale);
		
		scale.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event arg0) {
				int val = 100 - (scale.getMaximum()
						- scale.getSelection() + scale
						.getMinimum());
				long max =mediaPlayer.getLength();
				Videoplayer.this.mediaPlayer.setTime(
						(max * val / 100));
			}
		});
		
		scale.setMinimum(1);
		scale.setMaximum((int) this.mediaPlayer
				.getLength());
		scale.setPageIncrement(1);
		run = new Runnable() {
			@Override
			public void run() {
				long pos = Videoplayer.this.mediaPlayer.getTime();
				long max = mediaPlayer.getLength();
				if (max == 0) {
					return;
				}
				float f = (pos % max);
				float p = (f / max) * 100;
				if (Videoplayer.this.mediaPlayer
						.isPlaying()) {
					if (f != 0) {
						scale.setSelection((int) p);
						int sec = (int) ((f / 1000) % 60);
						int min = (int) ((f / 1000) / 60);
						dateTime.setTime(0, min, sec);
					}
				}
			}
		};
		
		final Button btnPlay = new Button(cmpControls, SWT.NONE);
		btnPlay.setImage(imgPause);
		btnPlay.setSize(75, 25);
		
		btnPlay.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean isPlaying = mediaPlayer.isPlaying();
				mediaPlayer.setPause(isPlaying);
				
				if(!isPlaying){
					btnPlay.setImage(imgPause);
				}else{
					btnPlay.setImage(imgPlay);
				}
			}
		});
		
		this.dateTime = new DateTime(cmpControls, SWT.TIME);
		this.dateTime.setBounds(0, 0, 76, 24);
		this.dateTime.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				super.widgetSelected(arg0);
				if (Videoplayer.this.mediaPlayer
						.isPlaying()) {
					Videoplayer.this.mediaPlayer
							.pause();
				}
				Calendar c = new GregorianCalendar();
				c.set(Calendar.SECOND, Videoplayer.this.dateTime.getSeconds());
				c.set(Calendar.MINUTE, Videoplayer.this.dateTime.getMinutes());
				c.set(Calendar.HOUR, Videoplayer.this.dateTime.getHours());
				long milis = (Videoplayer.this.dateTime.getSeconds()
						+ (Videoplayer.this.dateTime.getMinutes() * 60) + (Videoplayer.this.dateTime
						.getHours() * 60 * 60)) * 1000;

				if (milis <= Videoplayer.this.mediaPlayer.getLength()) {
					mediaPlayer
							.setTime(milis);
					long pos =mediaPlayer.getTime();
					long max = mediaPlayer.getLength();
					float f = (pos % max);
					float p = (f / max) * 100;
					scale.setSelection((int) p);
				}
			}
		});
		
		optionThumb = new Button(cmpOptions, SWT.RADIO);
		optionThumb.setText("Standbild erstellen");
		optionThumb.setSelection(true);
		
		Button optionQR = new Button(cmpOptions, SWT.RADIO);
		optionQR.setText("QR-Code erzeugen");
		
		qrURL = new Text(cmpOptions, SWT.BORDER|SWT.SEARCH);
		qrURL.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		qrURL.setMessage("Video URL");
		
		Composite cmpButtons = new Composite(shell, SWT.NONE);
		GridData gd_cmpButtons = new GridData(SWT.RIGHT, SWT.BOTTOM, true, true, 1, 1);
		gd_cmpButtons.widthHint = 120;
		cmpButtons.setLayoutData(gd_cmpButtons);
		cmpButtons.setLayout(new GridLayout(2, false));
		
		btnCancel = new Button(cmpButtons, SWT.RIGHT);
		btnCancel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		btnCancel.setAlignment(SWT.CENTER);
		btnCancel.setText("Abbrechen");
		
		btnOk = new Button(cmpButtons, SWT.CENTER);
		btnOk.setText("OK");
	}
	
	public void open(){
		shell.open();
		shell.setVisible(true);
		while (!shell.isDisposed()) {
			syncScaler();
			if (!shell.getDisplay().readAndDispatch()) {
				this.shell.getDisplay().sleep();
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void syncScaler() {
		if (mediaPlayer.isPlaying()) {
			shell.getDisplay().syncExec(this.run);
		}
	}
	
	/**
	 * @param adapter what happens when dialog is ok
	 */
	public void addOkAdapter(SelectionAdapter adapter){
		btnOk.addSelectionListener(adapter);
	}
	
	/**
	 * @param adapter What happend if dialog is canceled
	 */
	public void addCancelAdapter(SelectionAdapter adapter){
		btnCancel.addSelectionListener(adapter);
	}
	
	public boolean isSnap(){
		return optionThumb.getSelection();
	}

	public java.awt.Image getSnap() {
		return mediaPlayer.getSnapshot();
	}

	public String getQRURL() {
		return qrURL.getText();
	}

	public void play(String mediaPath) {
		mediaPlayer.playMedia(mediaPath);
	}
}
