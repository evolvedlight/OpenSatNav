package org.opensatnav.util;

import java.util.ArrayList;

import org.opensatnav.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Resources;
import android.os.Build;

public class BugReportSender {
	private final Context parent;
	private static final String MIME_TYPE = "message/rfc822";

	public BugReportSender(Context parent) {
		this.parent = parent;
	}

	public void sendBugReport(final Throwable e) {

		AlertDialog dialog = new AlertDialog.Builder(parent).setPositiveButton(
				android.R.string.yes, new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						ArrayList<Throwable> al = new ArrayList<Throwable>();
						al.add(e);
						Intent intent = send(BugReportExceptionHandler.buildStackTraceMessage(al));
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						parent.startActivity(intent);
					}
				}).setNegativeButton(android.R.string.no, null).create();
		dialog.setMessage(parent.getString(R.string.bug_report_ask_user));
		dialog.setTitle(R.string.bug_report_ask_user_title);
		dialog.setIcon(android.R.drawable.ic_dialog_alert);
		dialog.show();
	}

	protected void sendBugReportAtRestart(final String stacktrace) {

		AlertDialog dialog = new AlertDialog.Builder(parent).setPositiveButton(
				android.R.string.yes, new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = send(stacktrace);
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						parent.startActivity(intent);
					}
				}).setNegativeButton(android.R.string.no, null).create();
		dialog.setMessage(parent
				.getString(R.string.bug_report_not_sent_ask_user));
		dialog.setTitle(R.string.bug_report_ask_user_title);
		dialog.setIcon(android.R.drawable.ic_dialog_alert);
		dialog.show();
	}

	/**
	 * Sends an email to the project with the given exceptions
	 * 
	 * @param exceptions
	 * @param resources
	 *            {@link Resources} for getting some Strings.
	 * @return
	 * @return Intent ready to start.
	 */
	private Intent send(final String stackTrace) {
		String[] addressee = { parent
				.getString(R.string.bug_report_mail_address) };
BugReportHelper helper = new BugReportHelper(parent);
		String subject = new StringBuilder(helper.getVersionName()).append(" [SVN Rev ").append(helper.getRevision()).append(
				"] Error Report (Android: ").append(Build.VERSION.RELEASE)
				.append(" - model: ").append(Build.MODEL).append(")")
				.toString();
		String message = parent.getString(R.string.error_email_message);

		message += stackTrace;

		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.putExtra(Intent.EXTRA_EMAIL, addressee);
		intent.putExtra(Intent.EXTRA_SUBJECT, subject);
		intent.putExtra(Intent.EXTRA_TEXT, message);
		intent.setType(MIME_TYPE);
		return Intent.createChooser(intent, parent
				.getString(R.string.error_email_chooser_title));
	}


}
