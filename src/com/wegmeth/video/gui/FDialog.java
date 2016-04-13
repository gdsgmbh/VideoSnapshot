package com.wegmeth.video.gui;

import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

public class FDialog extends FileDialog{
	public FDialog(Shell parent,String title) {
		super(parent);
		setText(title);
	}

	@Override
	protected void checkSubclass() {
	}
}