/*
 * Copyright 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.playcez;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.api.client.auth.oauth2.draft10.AccessProtectedResource;
import com.google.api.client.extensions.android2.AndroidHttp;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.plus.Plus;
import com.playcez.AuthUtils;

// TODO: Auto-generated Javadoc
/**
 * The Class PlusWrap.
 */
public class PlusWrap {
  
  /** The plus. */
  private final Plus plus;

  /**
   * Instantiates a new plus wrap.
   *
   * @param context the context
   */
  public PlusWrap(Context context) {
    final SharedPreferences prefs = context.getSharedPreferences(AuthUtils.PREFS_NAME, 0);
    final String accessToken = prefs.getString("accessToken", null);
    final AccessProtectedResource authInitializer = new AccessProtectedResource(
        accessToken, AccessProtectedResource.Method.AUTHORIZATION_HEADER);
    
    plus = new Plus(AndroidHttp.newCompatibleTransport(), authInitializer, new GsonFactory());
    plus.setKey(" AIzaSyCWp7NYJ-ZTVhXe9TnzYCB5wLmRg1gL6KE");
    plus.setApplicationName("PlayCez");
  }

  /**
   * Gets the.
   *
   * @return the plus
   */
  public Plus get() {
    return plus;
  }
}