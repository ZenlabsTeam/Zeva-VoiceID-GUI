package com.zensar.zenlabs.zeva.voiceid;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.microsoft.cognitive.speakerrecognition.contract.EnrollmentException;
import com.zensar.zenlabs.zeva.voiceid.data.Enrollment;
import com.zensar.zenlabs.zeva.voiceid.mscs.SpeakerIdentificationService;

import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;


public class EnrollDialog extends Dialog {
	private final static String samplePath = System.getProperty("user.home") + "/ZevaVoiceID/enroll/sample.wav";
	private final static String startLabel = "Start Recording";
	private final static String endLabel = "End Recording";
	protected Object result;
	protected Shell shell;
	private Enrollment enroll;
	private SoundRecorder recorder;
	private SpeakerIdentificationService service;

	public EnrollDialog(Shell parent, int style, Enrollment enroll, SpeakerIdentificationService service) {
		super(parent, style);
		setText("Speak To Enroll Your Voice");
		this.enroll = enroll;
		this.service = service;
		this.recorder = new SoundRecorder(samplePath, shell);
	}

	/**
	 * Open the dialog.
	 * 
	 * @return the result
	 */
	public Object open() {
		createContents();
		shell.open();
		shell.layout();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}

		return result;
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shell = new Shell(getParent(), getStyle());
		shell.setSize(735, 199);
		shell.setText(getText());
		shell.setLayout(null);

		Button btnStartSpeaking = new Button(shell, SWT.NONE);
		btnStartSpeaking.setBounds(10, 10, 252, 28);
		btnStartSpeaking.setText(startLabel);

		Button btnSubmit = new Button(shell, SWT.NONE);

		btnSubmit.setBounds(10, 48, 252, 28);
		btnSubmit.setText("Submit For Enrollment");
		btnSubmit.setEnabled(false);

		Button btnClose = new Button(shell, SWT.NONE);
		btnClose.addListener(SWT.Selection, event ->shell.dispose());
		btnClose.setBounds(10, 77, 252, 28);
		btnClose.setText("Close");

		Label lblUserName = new Label(shell, SWT.NONE);
		lblUserName.setAlignment(SWT.CENTER);
		lblUserName.setBounds(0, 125, 272, 37);
		lblUserName.setText("This voice is recorded for the user " + enroll.getName());

		Label lblNewLabel = new Label(shell, SWT.WRAP);
		lblNewLabel.setBounds(268, 10, 446, 157);
		lblNewLabel.setText(
				"Zensar Labs (ZenLabs) is a new corporate initiative at Zensar that is focused on setting up a new advanced R&D team that will focus on investigating technologies that will become mainstream in 3 to 5 years\nIn today’s world technological innovations are disrupting the industrial, business and governance landscapes at a phenomenal rate. Today, no company, however successful, is secure from the fear of disruption. Consequently, all firms want to know which new technologies pose a threat to their business and how they can effectively respond to it. Zensar Labs is focused at helping customers overcome disruptions that arise in their industry. Zensar Labs motto is to engage in meaningful endeavours that are good for our customers and the society at large");

		btnStartSpeaking.addListener(SWT.Selection, event -> {
				if (btnStartSpeaking.getText().equals(startLabel)) {
					btnSubmit.setEnabled(false);
					btnClose.setEnabled(false);
					btnStartSpeaking.setText(endLabel);
					recorder.start();
				} else {
					btnSubmit.setEnabled(true);
					btnClose.setEnabled(true);
					btnStartSpeaking.setText(startLabel);
					recorder.finish();
				}
			}
		);
		btnSubmit.addListener(SWT.Selection, event -> {
			try {
					service.enroll(enroll.getId(), samplePath);
					UIHelper.handelAlerts(shell, "Voice submitter for enrollment", SWT.ICON_INFORMATION);
					shell.dispose();
				} catch (IOException | EnrollmentException e1) {
					UIHelper.handelException(shell, e1);
				} 
			
		});

	}
}
