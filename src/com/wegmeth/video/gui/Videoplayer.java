package com.wegmeth.video.gui;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Scale;

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
	private Button optionQR;
	
	public void close(){
		this.shell.dispose();
	}
	
	/**
	 * Main Dialog with all Controls and Options for Thumbnail
	 */
	public Videoplayer (){
		this.shell = new Shell();
		this.imgPlay= new Image(this.shell.getDisplay(),getClass().getResourceAsStream("/com/wegmeth/video/res/play.png"));
		this.imgPause= new Image(this.shell.getDisplay(),getClass().getResourceAsStream("/com/wegmeth/video/res/pause.png"));
		
		this.shell.setSize(600, 700);
		this.shell.setLayout(new GridLayout(1, false));
		this.shell.addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent arg0) {
				Videoplayer.this.mediaPlayer.release();
			}
		});
		
		VideoCmp cmpVideo = new VideoCmp(this.shell, SWT.EMBEDDED);
		this.mediaPlayer = cmpVideo.getPlayer();
		
		Composite cmpControls = new Composite(this.shell, SWT.NONE);
		cmpControls.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		cmpControls.setLayout(new GridLayout(1, false));
		
		Composite cmpOptions = new Composite(this.shell, SWT.NONE);
		GridData gd_cmpOptions = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_cmpOptions.widthHint = 777;
		cmpOptions.setLayoutData(gd_cmpOptions);
		cmpOptions.setLayout(new GridLayout(1, false));
		
		Scale scale = new Scale(cmpControls, SWT.NONE);
		GridData gd_scale = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_scale.widthHint = 767;
		scale.setLayoutData(gd_scale);
		
		scale.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event arg0) {
				int val = 100 - (scale.getMaximum()
						- scale.getSelection() + scale
						.getMinimum());
				long max =Videoplayer.this.mediaPlayer.getLength();
				Videoplayer.this.mediaPlayer.setTime(
						(max * val / 100));
			}
		});
		
		scale.setMinimum(1);
		scale.setMaximum((int) this.mediaPlayer
				.getLength());
		scale.setPageIncrement(1);
		this.run = new Runnable() {
			@Override
			public void run() {
				long pos = Videoplayer.this.mediaPlayer.getTime();
				long max = Videoplayer.this.mediaPlayer.getLength();
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
						Videoplayer.this.dateTime.setTime(0, min, sec);
					}
				}
			}
		};
		
		Button btnPlay = new Button(cmpControls, SWT.NONE);
		btnPlay.setImage(this.imgPause);
		btnPlay.setSize(75, 25);
		
		btnPlay.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean isPlaying = Videoplayer.this.mediaPlayer.isPlaying();
				Videoplayer.this.mediaPlayer.setPause(isPlaying);
				
				if(!isPlaying){
					btnPlay.setImage(Videoplayer.this.imgPause);
				}else{
					btnPlay.setImage(Videoplayer.this.imgPlay);
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
					Videoplayer.this.mediaPlayer
							.setTime(milis);
					long pos =Videoplayer.this.mediaPlayer.getTime();
					long max = Videoplayer.this.mediaPlayer.getLength();
					float f = (pos % max);
					float p = (f / max) * 100;
					scale.setSelection((int) p);
				}
			}
		});
		
		this.optionThumb = new Button(cmpOptions, SWT.RADIO);
		this.optionThumb.setText("Standbild erstellen");
		this.optionThumb.setSelection(true);
		
		this.optionQR = new Button(cmpOptions, SWT.RADIO);
		this.optionQR.setText("QR-Code erzeugen");
		
		// Wenn QR-Code ausgewählt wurde, wird das QR-Code-Feld freigegeben
		// Des Weiteren wird geprueft, ob das Textfeld leer ist.
		// Ist dies der Fall, wird der OK-Button solange disabled
		this.optionQR.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				if(Videoplayer.this.optionQR.getSelection()){
					Videoplayer.this.qrURL.setEnabled(true);
					Videoplayer.this.btnOk.setEnabled(!Videoplayer.this.qrURL.getText().equals(""));
				}
				else{
					Videoplayer.this.qrURL.setEnabled(false);
					Videoplayer.this.btnOk.setEnabled(true);
				}
							
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		this.qrURL = new Text(cmpOptions, SWT.BORDER|SWT.SEARCH);
		this.qrURL.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		this.qrURL.setMessage("Video URL");

		// Wenn der Text sich aendert und QR-Code ausgewaehlt wurde, darf der OK-Button nur aktiv sein, wenn etwas eingetragen wurde.
		this.qrURL.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent arg0) {
				if(Videoplayer.this.optionQR.getSelection())
					Videoplayer.this.btnOk.setEnabled(!Videoplayer.this.qrURL.getText().equals(""));
			}
		});
		
		Composite cmpButtons = new Composite(this.shell, SWT.NONE);
		GridData gd_cmpButtons = new GridData(SWT.RIGHT, SWT.BOTTOM, true, true, 1, 1);
		gd_cmpButtons.widthHint = 120;
		cmpButtons.setLayoutData(gd_cmpButtons);
		cmpButtons.setLayout(new GridLayout(2, false));
		
		this.btnCancel = new Button(cmpButtons, SWT.RIGHT);
		this.btnCancel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		this.btnCancel.setAlignment(SWT.CENTER);
		this.btnCancel.setText("Abbrechen");
		
		this.btnOk = new Button(cmpButtons, SWT.CENTER);
		this.btnOk.setText("OK");
	}
	
	public void open(){
		this.shell.open();
		this.shell.setVisible(true);
		while (!this.shell.isDisposed()) {
			syncScaler();
			if (!this.shell.getDisplay().readAndDispatch()) {
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
		if (this.mediaPlayer.isPlaying()) {
			this.shell.getDisplay().syncExec(this.run);
		}
	}
	
	/**
	 * @param adapter what happens when dialog is ok
	 */
	public void addOkAdapter(SelectionAdapter adapter){
		this.btnOk.addSelectionListener(adapter);
	}
	
	/**
	 * @param adapter What happend if dialog is canceled
	 */
	public void addCancelAdapter(SelectionAdapter adapter){
		this.btnCancel.addSelectionListener(adapter);
	}
	
	public boolean isSnap(){
		return this.optionThumb.getSelection();
	}

	public java.awt.Image getSnap() {
		return this.mediaPlayer.getSnapshot();
	}

	public String getQRURL() {
		return this.qrURL.getText();
	}

	public void play(String mediaPath) {
		this.mediaPlayer.playMedia(mediaPath);
	}
}
