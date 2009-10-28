/* The following code was written by Matthew Wiggins 
 * and is released under the APACHE 2.0 license 
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * see http://android.hlidskialf.com/post/android-seekbar-preference
 */

package com.hlidskialf.android.preference;

import android.content.Context;
import android.preference.DialogPreference;
import android.text.InputType;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.util.Log;

public class SeekBarPreference extends DialogPreference implements
		SeekBar.OnSeekBarChangeListener, View.OnTouchListener,
		OnFocusChangeListener, OnEditorActionListener {
	private static final String androidns = "http://schemas.android.com/apk/res/android";

	private SeekBar mSeekBar;
	private TextView mSplashText;
	private EditText mValueText;
	private Context mContext;

	private String mDialogMessage, mSuffix;
	private int mDefault, mMax, mValue = 0;

	public SeekBarPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		String dialogMessage = attrs.getAttributeValue(androidns,
				"dialogMessage");
		if (dialogMessage != null && dialogMessage.startsWith("@"))
			mDialogMessage = context.getString(Integer.valueOf(dialogMessage
					.substring(1)));
		else
			mDialogMessage = dialogMessage;
		String text = attrs.getAttributeValue(androidns, "text");
		if (text != null && text.startsWith("@"))
			mSuffix = context.getString(Integer.valueOf(text.substring(1)));
		else
			mSuffix = text;
		mDefault = attrs.getAttributeIntValue(androidns, "defaultValue", 0);
		mMax = attrs.getAttributeIntValue(androidns, "max", 100);
	}

	@Override
	protected View onCreateDialogView() {
		LinearLayout.LayoutParams params;
		LinearLayout layout = new LinearLayout(mContext);
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.setPadding(6, 6, 6, 6);

		mSplashText = new TextView(mContext);
		if (mDialogMessage != null)
			mSplashText.setText(mDialogMessage);
		layout.addView(mSplashText);

		mValueText = new EditText(mContext);
		mValueText.setGravity(Gravity.CENTER_HORIZONTAL);
		mValueText.setTextSize(32);
		params = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		layout.addView(mValueText, params);

		mSeekBar = new SeekBar(mContext);
		mSeekBar.setOnSeekBarChangeListener(this);
		layout.addView(mSeekBar, params);

		if (shouldPersist())
			mValue = getPersistedInt(mDefault);

		mSeekBar.setMax(modFunc(mMax));

		mValueText.setOnTouchListener(this);
		mValueText.setOnFocusChangeListener(this);
		mValueText.setOnEditorActionListener(this);
		mValueText.setInputType(InputType.TYPE_CLASS_NUMBER);
		mValueText.setFocusableInTouchMode(false);

		dispatchValue(mValue);

		return layout;
	}

	@Override
	protected void onBindDialogView(View v) {
		super.onBindDialogView(v);
		mSeekBar.setMax(modFunc(mMax));

		setProgress(mValue);
	}

	@Override
	protected void onSetInitialValue(boolean restore, Object defaultValue) {
		super.onSetInitialValue(restore, defaultValue);
		if (restore)
			mValue = shouldPersist() ? getPersistedInt(mDefault) : 0;
		else
			mValue = (Integer) defaultValue;
	}

	// called if the user touches the text edit box

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			mValueText.setFocusableInTouchMode(true);
			CharSequence text = ((TextView) v).getText();
			if (TextUtils.isEmpty(text) || !TextUtils.isDigitsOnly(text)) {
				// on Touch, remove the suffix if there is one.
				// If the user is already editing, then the suffix will already be gone
				// and in that case, don't change the value on the user.
				((TextView) v).setText(Integer.toString(mValue));
			}
		}
		return false;
	}

	// called when the user hits enter on the text edit box
	// sets the progress bar value, which flows on to other
	// values

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		setProgress(v.getText());
		return true;
	}

	private void setProgress(CharSequence text) {
		if (!TextUtils.isEmpty(text)) {
			if (TextUtils.isDigitsOnly(text))
				setProgress(Integer.parseInt(text.toString()));
		}
	}

	// set the Progress bar to a given value.
	// will in turn cause onProgressChanged() to be called

	public void setProgress(int progress) {
		int modifiedValue = modFunc(progress);
		
		if (mSeekBar != null)
			mSeekBar.setProgress(modifiedValue);
		dispatchValue(progress);
	}

	public int getProgress() {
		return mValue;
	}

	public void onProgressChanged(SeekBar seek, int modifiedValue, boolean fromTouch) {
		int value = invModFunc(modifiedValue);
		
		String t = String.valueOf(value);

		if (mValueText != null)
			mValueText.setText(mSuffix == null ? t : t.concat(mSuffix));
		mValue = value;
	}

	public void onStartTrackingTouch(SeekBar seek) {
	}

	public void onStopTrackingTouch(SeekBar seek) {
		dispatchValue(mValue);
	}

	private void dispatchValue(int value) {
		String t = String.valueOf(value);

		if (mValueText != null)
			mValueText.setText(mSuffix == null ? t : t.concat(mSuffix));
		mValue = value;
		// callChangeListener(new Integer(value));

		if (shouldPersist())
			persistInt(value);
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		if (hasFocus)
			((TextView) v).setText(Integer.toString(mValue));
		else
			setProgress(((TextView) v).getText());
	}

	public void setMax(int max) {
		mMax = max;
		if (mSeekBar != null)
			mSeekBar.setMax(modFunc(mMax));
	}

	public int getMax() {
		return mMax;
	}

	protected int modFunc(int value) {
		return (int)Math.sqrt(value);
	}

	protected int invModFunc(int value) {
		return value*value;
	}

}
