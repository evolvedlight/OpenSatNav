package org.opensatnav.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.opensatnav.R;

import android.app.AlertDialog.Builder;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.net.Uri;
import android.widget.ScrollView;

public class UKPostCodeValidator {
	private final static String postCodePattern = ".*[A-Z]{1,2}[0-9R][0-9A-Z]? [0-9][ABD-HJLNP-UW-Z]{2}.*";
	private final static Pattern pattern = Pattern.compile(postCodePattern,
			Pattern.CASE_INSENSITIVE);
	private final static String freeThePostCodeUpdateURI = "http://market.android.com/search?q=pname:org.freethepostcode.android";

	public static boolean isPostCode(CharSequence text) {
		if (text == null)
			return false;
		Matcher matcher = pattern.matcher(text);
		return matcher.matches();
	}

	public static void showFreeThePostCodeDialog(final Context ctx) {
		Builder builder = new Builder(ctx);
		builder.setTitle(R.string.uk_postcode_not_recognised_title).setMessage(
				R.string.uk_postcode_not_recognised).setPositiveButton(
				android.R.string.ok, new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						try {
							// if FreeThePostcode activity is present, start it
							Intent intent = new Intent(Intent.ACTION_MAIN);
							intent.setClassName("org.freethepostcode.android", "org.freethepostcode.android.FreeThePostcode");
							ctx.startActivity(intent);
						}
						catch (ActivityNotFoundException e) {
							// else go to the market to install it
							Intent intent = new Intent(Intent.ACTION_VIEW, Uri
									.parse(freeThePostCodeUpdateURI));
							ctx.startActivity(intent);							
						}
					}
				}).setNegativeButton(android.R.string.cancel, null);
		builder.show();
	}

}
