package com.zensar.zenlabs.zeva.voiceid;

import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import org.eclipse.wb.swt.SWTResourceManager;

import java.io.IOException;
import java.util.List;

import com.microsoft.cognitive.speakerrecognition.contract.CreateProfileException;
import com.microsoft.cognitive.speakerrecognition.contract.GetProfileException;

import com.zensar.zenlabs.zeva.voiceid.data.Enrollment;
import com.zensar.zenlabs.zeva.voiceid.data.EnrollmentDAL;
import com.zensar.zenlabs.zeva.voiceid.mscs.SpeakerIdentificationService;


public class MainUILauncher {
	private static final String subscriptionKey = "cdc2211b2790401b8f94f9b03b2d9b27";
	private static Text text;
	private static Table table;
	private static SpeakerIdentificationService idService;
	private static CCombo userNameCombo;
	private static List<Enrollment> data;
	private static Shell shell;

	private static void refreshData() {
		data = EnrollmentDAL.getALLEnrollments();
		data.parallelStream().filter(ed -> !ed.getStatus().equals("ENROLLED")).forEach(ed -> {
			try {
				String status = idService.checkProfileStatus(ed.getId());
				if (!ed.getStatus().equals(status)) {
					ed.setStatus(status);
					EnrollmentDAL.saveEnrollment(ed);
				}
			} catch (GetProfileException | IOException e) {
				UIHelper.handelException(shell, e);
			}

		});

		table.clearAll();
		table.removeAll();
		userNameCombo.removeAll();
		data.stream().forEach(ed -> {
			(new TableItem(table, SWT.NONE)).setText(new String[] { ed.getName(), ed.getStatus() });
			userNameCombo.add(ed.getName());
		});

	}

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		idService = new SpeakerIdentificationService(subscriptionKey);
		Display display = Display.getDefault();
		shell = new Shell();
		shell.setSize(450, 300);
		shell.setText("Zeva - Voice ID");
		shell.setLayout(null);
		text = new Text(shell, SWT.BORDER);
		text.setBounds(10, 41, 207, 26);

		Label lblNewLabel = new Label(shell, SWT.NONE);
		lblNewLabel.setFont(SWTResourceManager.getFont(".SF NS Text", 11, SWT.BOLD));
		lblNewLabel.setAlignment(SWT.CENTER);
		lblNewLabel.setBounds(10, 14, 207, 14);
		lblNewLabel.setText("Enter the New Person Name");

		Label lblNewLabel_1 = new Label(shell, SWT.NONE);
		lblNewLabel_1.setFont(SWTResourceManager.getFont(".SF NS Text", 11, SWT.BOLD));
		lblNewLabel_1.setAlignment(SWT.CENTER);
		lblNewLabel_1.setBounds(10, 115, 207, 14);
		lblNewLabel_1.setText("Select the Person to Enroll");

		userNameCombo = new CCombo(shell, SWT.BORDER);
		userNameCombo.setBounds(10, 135, 207, 26);

		Button btnEnrollButton = new Button(shell, SWT.NONE);
		btnEnrollButton.addListener(SWT.Selection, event -> {
			int index = userNameCombo.getSelectionIndex();
			if (index != -1) {
				( new EnrollDialog(shell, SWT.APPLICATION_MODAL, data.get(index), idService)).open();
			} else {
				UIHelper.handelErrors(shell, "Please select user to enroll");
			}
		});
		btnEnrollButton.setBounds(10, 167, 207, 42);
		btnEnrollButton.setText("Enroll the User");

		Button btnVerifyTheUsers = new Button(shell, SWT.NONE);
		btnVerifyTheUsers.setBounds(10, 215, 207, 53);
		btnVerifyTheUsers.setText("Verify the Users Voice");
		btnVerifyTheUsers.addListener(SWT.Selection, event ->
			(new IdentifyDialog(shell, SWT.APPLICATION_MODAL, data, idService)).open());
		table = new Table(shell, SWT.BORDER | SWT.FULL_SELECTION);
		table.setBounds(224, 14, 216, 254);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		TableColumn tblclmnPersonName = new TableColumn(table, SWT.NONE);
		tblclmnPersonName.setWidth(139);
		tblclmnPersonName.setText("Person Name");

		TableColumn tblclmnEnrollmentStatus = new TableColumn(table, SWT.NONE);
		tblclmnEnrollmentStatus.setWidth(100);
		tblclmnEnrollmentStatus.setText("Enroll Status");

		Button btnAddANew = new Button(shell, SWT.NONE);
		btnAddANew.addListener(SWT.Selection, event -> {
			String name = text.getText();
			if (name.trim().length() > 1) {
				try {
					Enrollment ed = new Enrollment();
					ed.setName(name);
					ed.setId(idService.CreateProfile());
					ed.setStatus("New Profile");
					EnrollmentDAL.saveEnrollment(ed);
					refreshData();
				} catch (CreateProfileException | IOException e1) {
					UIHelper.handelException(shell, e1);
				}
			} else {
				UIHelper.handelErrors(shell, "Please Enter a proper name");
				
			}

		});
		btnAddANew.setBounds(10, 73, 207, 42);
		btnAddANew.setText("Add a New Person");
		refreshData();

		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
}
