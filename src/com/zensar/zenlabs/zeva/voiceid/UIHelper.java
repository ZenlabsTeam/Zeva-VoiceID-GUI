package com.zensar.zenlabs.zeva.voiceid;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public  class UIHelper {
	public static void handelException(Shell parent,Exception e1){
		e1.printStackTrace();
		handelErrors(parent,"Unable to enroll, failed with error message as" + e1.getMessage());
	}
	
	public static void handelErrors(Shell parent,String message){
		handelAlerts(parent,message,SWT.ICON_ERROR);
	}
	public static void handelAlerts(Shell parent,String message,int style){
		MessageBox messageBox = new MessageBox(parent, style);
		messageBox.setMessage(message);
		messageBox.open();
		
	}
}
