/*
 * 
 */
package com.playcez.twitter.android;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Display;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.playcez.R;
import com.playcez.twitter.android.TwitterApp.TwDialogListener;
// TODO: Auto-generated Javadoc
/*
Copyright [2010] [Abhinava Srivastava]

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

https://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
/**
 * The Class TwitterDialog.
 */
public class TwitterDialog extends Dialog {

	/** The Constant DIMENSIONS_LANDSCAPE. */
	static final float[] DIMENSIONS_LANDSCAPE = { 460, 260 };
	
	/** The Constant DIMENSIONS_PORTRAIT. */
	static final float[] DIMENSIONS_PORTRAIT = { 280, 420 };
	
	/** The Constant FILL. */
	static final FrameLayout.LayoutParams FILL = new FrameLayout.LayoutParams(
			ViewGroup.LayoutParams.FILL_PARENT,
			ViewGroup.LayoutParams.FILL_PARENT);
	
	/** The Constant MARGIN. */
	static final int MARGIN = 4;
	
	/** The Constant PADDING. */
	static final int PADDING = 2;
	
	/** The m url. */
	private String mUrl;
	
	/** The m listener. */
	private TwDialogListener mListener;
	
	/** The m spinner. */
	private ProgressDialog mSpinner;
	
	/** The m web view. */
	private WebView mWebView;
	
	/** The m content. */
	private LinearLayout mContent;
	
	/** The m title. */
	private TextView mTitle;
	
	/** The progress dialog running. */
	private boolean progressDialogRunning = false;

	/**
	 * Instantiates a new twitter dialog.
	 *
	 * @param context the context
	 * @param url the url
	 * @param listener the listener
	 */
	public TwitterDialog(Context context, String url, TwDialogListener listener) {
		super(context);

		mUrl = url;
		mListener = listener;
	}

	/* (non-Javadoc)
	 * @see android.app.Dialog#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mSpinner = new ProgressDialog(getContext());

		mSpinner.requestWindowFeature(Window.FEATURE_NO_TITLE);
		mSpinner.setMessage("Loading...");

		mContent = new LinearLayout(getContext());

		mContent.setOrientation(LinearLayout.VERTICAL);

		setUpTitle();
		setUpWebView();

		Display display = getWindow().getWindowManager().getDefaultDisplay();
		final float scale = getContext().getResources().getDisplayMetrics().density;
		float[] dimensions = (display.getWidth() < display.getHeight()) ? DIMENSIONS_PORTRAIT
				: DIMENSIONS_LANDSCAPE;

		addContentView(mContent, new FrameLayout.LayoutParams(
				(int) (dimensions[0] * scale + 0.5f), (int) (dimensions[1]
						* scale + 0.5f)));
	}

	/**
	 * Sets the up title.
	 */
	private void setUpTitle() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		Drawable icon = getContext().getResources().getDrawable(
				R.drawable.twitter_icon);

		mTitle = new TextView(getContext());

		mTitle.setText("Twitter");
		mTitle.setTextColor(Color.WHITE);
		mTitle.setTypeface(Typeface.DEFAULT_BOLD);
		mTitle.setBackgroundColor(0xFFbbd7e9);
		mTitle.setPadding(MARGIN + PADDING, MARGIN, MARGIN, MARGIN);
		mTitle.setCompoundDrawablePadding(MARGIN + PADDING);
		mTitle.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);

		mContent.addView(mTitle);
	}

	/**
	 * Sets the up web view.
	 */
	private void setUpWebView() {
		mWebView = new WebView(getContext());

		mWebView.setVerticalScrollBarEnabled(false);
		mWebView.setHorizontalScrollBarEnabled(false);
		mWebView.setWebViewClient(new TwitterWebViewClient());
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.loadUrl(mUrl);
		mWebView.setLayoutParams(FILL);

		mContent.addView(mWebView);
	}

	/**
	 * The Class TwitterWebViewClient.
	 */
	private class TwitterWebViewClient extends WebViewClient {

		/* (non-Javadoc)
		 * @see android.webkit.WebViewClient#shouldOverrideUrlLoading(android.webkit.WebView, java.lang.String)
		 */
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			if (url.startsWith(TwitterApp.CALLBACK_URL)) {
				mListener.onComplete(url);

				TwitterDialog.this.dismiss();

				return true;
			} else if (url.startsWith("authorize")) {
				return false;
			}
			return true;
		}

		/* (non-Javadoc)
		 * @see android.webkit.WebViewClient#onReceivedError(android.webkit.WebView, int, java.lang.String, java.lang.String)
		 */
		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			super.onReceivedError(view, errorCode, description, failingUrl);
			mListener.onError(description);
			TwitterDialog.this.dismiss();
		}

		/* (non-Javadoc)
		 * @see android.webkit.WebViewClient#onPageStarted(android.webkit.WebView, java.lang.String, android.graphics.Bitmap)
		 */
		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
			mSpinner.show();
			progressDialogRunning = true;
		}

		/* (non-Javadoc)
		 * @see android.webkit.WebViewClient#onPageFinished(android.webkit.WebView, java.lang.String)
		 */
		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			String title = mWebView.getTitle();
			if (title != null && title.length() > 0) {
				mTitle.setText(title);
			}
            progressDialogRunning = false;
			mSpinner.dismiss();
		}

	}
	
	/* (non-Javadoc)
	 * @see android.app.Dialog#onStop()
	 */
	@Override
	protected void onStop() {
        progressDialogRunning = false;
		super.onStop();
	}

	/* (non-Javadoc)
	 * @see android.app.Dialog#onBackPressed()
	 */
	public void onBackPressed() {
    	if(!progressDialogRunning){
			TwitterDialog.this.dismiss();
    	}
    }
}
