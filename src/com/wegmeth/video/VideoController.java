package com.wegmeth.video;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.wegmeth.video.gui.FDialog;
import com.wegmeth.video.gui.Videoplayer;

public class VideoController {

	private Videoplayer player;
	private final static String DEFAULT_OUTPUT = new File(
			System.getenv("APPDATA"),"Thumbnails").getAbsolutePath();
	private File outputFile;

	public VideoController(Videoplayer p, String output) {
		this(p,new FDialog(new Shell(), "Wähle ein Video").open(),output);
	}

	public VideoController(Videoplayer p) {
		this(p,DEFAULT_OUTPUT);
	}

	public VideoController(Videoplayer videoplayer, String input, String output) {
		if(input == null || !new File(input).exists()) System.exit(1);
		outputFile = new File(output);
		this.player = videoplayer;
		this.player.addCancelAdapter(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				player.close();
			}
		});

		this.player.addOkAdapter(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				java.awt.Image img;
				if(player.isSnap())
					img =player.getSnap();
				else
					img = createQR(player.getQRURL());

				if(!outputFile.getParentFile().exists()) outputFile.getParentFile().mkdirs();
				File out = (outputFile);
				try {
					ImageIO.write((RenderedImage) img, "JPEG", out);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				System.exit(0);
			}
		});
		
		player.play(input);
	}

	public void open() {
		player.open();
	}
	
	

	private BufferedImage createQR(String text){
		QRCodeWriter qw = new QRCodeWriter();
		BitMatrix matrix=null;
		try {
			matrix = qw.encode(text, BarcodeFormat.QR_CODE, 300, 300);
		} catch (WriterException e) {
			e.printStackTrace();
		}
		BufferedImage ret = new BufferedImage(matrix.getWidth(), matrix.getHeight(),BufferedImage.TYPE_INT_RGB);
		ret.createGraphics();
		
		Graphics2D gra = (Graphics2D) ret.getGraphics();
		gra.setColor(Color.WHITE);
		gra.fillRect(0, 0, matrix.getWidth(), matrix.getHeight());
		gra.setColor(Color.black);
		
		for(int i=0;i<matrix.getWidth();i++){
			for(int j = 0; j<matrix.getHeight();j++){
				if (matrix.get(i, j)) {
					gra.fillRect(i, j, 1, 1);
				}
			}
		}
		return ret;
	}
}
