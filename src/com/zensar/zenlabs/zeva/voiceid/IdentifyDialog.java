package com.zensar.zenlabs.zeva.voiceid;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.microsoft.cognitive.speakerrecognition.contract.identification.IdentificationException;
import com.zensar.zenlabs.zeva.voiceid.data.Enrollment;
import com.zensar.zenlabs.zeva.voiceid.mscs.SpeakerIdentificationService;

import org.eclipse.swt.widgets.Button;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;

public class IdentifyDialog extends Dialog {
	private final static String samplePath = System.getProperty("user.home") + "/ZevaVoiceID/Identify/sample.wav";
	private final static String startLabel = "Start Recording";
	private final static String endLabel = "End Recording";
	protected Object result;
	protected Shell shell;
	private List<Enrollment> enrolls;
	private SoundRecorder recorder;
	private SpeakerIdentificationService service;

	/**
	 * Create the dialog.
	 * 
	 * @param parent
	 * @param style
	 */
	public IdentifyDialog(Shell parent, int style, List<Enrollment> enrolls, SpeakerIdentificationService service) {
		super(parent, style);
		super.setText("Speak to Verify the User");
		this.service = service;
		this.enrolls = enrolls;
		this.recorder = new SoundRecorder(samplePath,shell);
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
		shell.setSize(717, 188);
		shell.setText(getText());

		Button btnRecordButton = new Button(shell, SWT.NONE);
		btnRecordButton.setBounds(10, 10, 204, 28);
		btnRecordButton.setText(startLabel);

		Button btnSubmit = new Button(shell, SWT.NONE);
		btnSubmit.setBounds(10, 44, 204, 28);
		btnSubmit.setText("Submit For Identification");
		btnSubmit.setEnabled(false);
		Button btnClose = new Button(shell, SWT.NONE);
		btnClose.addListener(SWT.Selection, event -> shell.dispose());
		btnClose.setBounds(10, 110, 204, 28);
		btnClose.setText("Close");

		Button btnNewButton = new Button(shell, SWT.NONE);
		btnNewButton.setBounds(10, 78, 204, 28);
		btnNewButton.setText("Verify Id");
		btnNewButton.setEnabled(false);

		Label lblHelpText = new Label(shell, SWT.WRAP);
		lblHelpText.setBounds(220, 39, 487, 87);
		lblHelpText.setText(
				"ZenLabs is a brand new group within Zensar and will be a parallel to state-of-the-art Innovation Labs one can find in other large corporations. The team will embrace innovations and co-create solutions with customer R&D teams. Predominantly exploration driven with a fail quickly and fail often model of thinking");

		btnRecordButton.addListener(SWT.Selection, event -> {
			if (btnRecordButton.getText().equals(startLabel)) {
				btnSubmit.setEnabled(false);
				btnClose.setEnabled(false);
				btnRecordButton.setText(endLabel);
				recorder.start();
			} else {
				btnSubmit.setEnabled(true);
				btnClose.setEnabled(true);
				btnRecordButton.setText(startLabel);
				recorder.finish();
			}

		});
		btnSubmit.addListener(SWT.Selection, event -> {
				try {
					service.submitIdentify(samplePath,
							enrolls.stream().map(m -> m.getId()).collect(Collectors.toList()));
					btnNewButton.setEnabled(true);

				} catch (IdentificationException | IOException e1) {
					UIHelper.handelException(shell, e1);
				}
			}
		);
		btnNewButton.addListener(SWT.Selection, event -> {

				try {
					service.verifyIdentiy();
					UUID id = service.getLastId();
					UIHelper.handelAlerts(shell, "Voice is Identified as "
							+ enrolls.parallelStream().filter(s -> s.getId().equals(id)).findFirst().orElse(null).getName(), SWT.ICON_INFORMATION);
					shell.dispose();
				} catch (IdentificationException | IOException e1) {
					UIHelper.handelException(shell, e1);
				}

			
		});

	}
}
