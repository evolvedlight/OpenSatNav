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

		mSeekBar.setMax(mMax);

		mValueText.setOnTouchListener(this);
		mValueText.setOnFocusChangeListener(this);
		mValueText.setOnEditorActionListener(this);
		mValueText.setInputType(InputType.TYPE_CLASS_NUMBER);

		dispatchValue(mValue);

		return layout;
	}

	@Override
	protected void onBindDialogView(View v) {
		super.onBindDialogView(v);
		mSeekBar.setMax(mMax);
		mSeekBar.setProgress(mValue);
	}

	@Override
	protected void onSetInitialValue(boolean restore, Object defaultValue) {
		super.onSetInitialValue(restore, defaultValue);
		if (restore)
			mValue = shouldPersist() ? getPersistedInt(mDefault) : 0;
		else
			mValue = (Integer) defaultValue;
	}

	public void onProgressChanged(SeekBar seek, int value, boolean fromTouch) {
		dispatchValue(value);

		callChangeListener(new Integer(value));
	}

	private void dispatchValue(int value) {
		String t = String.valueOf(value);

		mValueText.setText(mSuffix == null ? t : t.concat(mSuffix));
		mValue = value;

		if (shouldPersist())
			persistInt(value);
	}

	public void onStartTrackingTouch(SeekBar seek) {
	}

	public void onStopTrackingTouch(SeekBar seek) {
	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		setProgress(v.getText());
		return true;
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		if (hasFocus)
			((TextView) v).setText(Integer.toString(mValue));
		else
			setProgress(((TextView) v).getText());
	}

	private void setProgress(CharSequence text) {

		if (!TextUtils.isEmpty(text)) {
			if (TextUtils.isDigitsOnly(text))
				dispatchValue(Integer.parseInt(text.toString()));
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN)
			((TextView) v).setText(Integer.toString(mValue));
		return false;
	}

	public void setMax(int max) {
		mMax = max;
	}

	public int getMax() {
		return mMax;
	}

	public void setProgress(int progress) {
		mValue = progress;
		if (mSeekBar != null)
			mSeekBar.setProgress(progress);
	}

	public int getProgress() {
		return mValue;
	}
}
