/*
 * Copyright 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.playcez;

import android.accounts.Account;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.api.client.googleapis.extensions.android2.auth.GoogleAccountManager;
import com.playcez.R;

/**
 * The Class AccountListActivity.
 *
 * @author third party
 */
public class AccountListActivity extends ListActivity {
	
	/** The callback. */
	private Intent callback;

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedState) {
		super.onCreate(savedState);
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();
		final Intent intent = getIntent();
		if (intent != null && intent.hasExtra("callback")) {
			callback = (Intent) intent.getExtras().get("callback");
		}

		final Account[] accounts = new GoogleAccountManager(
				getApplicationContext()).getAccounts();
		this.setListAdapter(new AccountListAdapter(this, R.layout.account_list,
				accounts));
	}

	/* (non-Javadoc)
	 * @see android.app.ListActivity#onListItemClick(android.widget.ListView, android.view.View, int, long)
	 */
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		final Account account = (Account) getListView().getItemAtPosition(
				position);
		final SharedPreferences settings = getSharedPreferences(
				AuthUtils.PREFS_NAME, 0);
		final SharedPreferences.Editor editor = settings.edit();
		editor.putString(AuthUtils.PREF_ACCOUNT_NAME, account.name);
		editor.commit();
		startActivity(callback);
		finish();
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		finish();
		super.onPause();
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onStop()
	 */
	@Override
	protected void onStop() {
		finish();
		super.onStop();
	}

	/**
	 * The Class AccountListAdapter.
	 */
	private static class AccountListAdapter extends ArrayAdapter<Account> {
		
		/** The accounts. */
		private final Account[] accounts;

		/**
		 * Instantiates a new account list adapter.
		 *
		 * @param ctx the ctx
		 * @param resourceId the resource id
		 * @param accounts the accounts
		 */
		public AccountListAdapter(Context ctx, int resourceId,
				Account[] accounts) {
			super(ctx, resourceId, accounts);
			this.accounts = accounts;
		}

		/* (non-Javadoc)
		 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
		 */
		@Override
		public View getView(int position, View view, ViewGroup parent) {
			if (view == null) {
				final LayoutInflater layoutInflater = (LayoutInflater) getContext()
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = layoutInflater.inflate(R.layout.account_list, null);
			}

			final Account account = accounts[position];
			final TextView textView = (TextView) view
					.findViewById(R.id.account);
			if (account != null) {

				if (textView != null) {
					textView.setText(account.name);
				}
			}
			return view;
		}
	}
}
