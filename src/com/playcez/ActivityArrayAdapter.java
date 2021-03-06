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

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.api.services.plus.model.Activity;

import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * Creates an ArrayAdapter that displays your activity stream.
 *
 * @author Chirag Shah
 */
public class ActivityArrayAdapter extends ArrayAdapter<Activity> {
  
  /**
   * Instantiates a new activity array adapter.
   *
   * @param context the context
   * @param people the people
   */
  public ActivityArrayAdapter(Context context, List<Activity> people) {
    super(context, android.R.layout.simple_spinner_item, people);
    setDropDownViewResource(R.layout.activity_list);
  }
  
  /* (non-Javadoc)
   * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
   */
  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    return getDropDownView(position, convertView, parent);
  }

  /* (non-Javadoc)
   * @see android.widget.ArrayAdapter#getDropDownView(int, android.view.View, android.view.ViewGroup)
   */
  @Override
  public View getDropDownView(int position, View view, ViewGroup parent) {
    if (view == null) {
      final LayoutInflater inflater = (LayoutInflater) getContext()
          .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      view = inflater.inflate(R.layout.activity_list, parent, false);
    }

    final Activity activity = getItem(position);
    if (null == activity) {
    	Log.d("Activity Fetch", "Some Activity");
    	((TextView) view.findViewById(R.id.title)).setText("heyyyy");
      return view;
    }
    Log.d("Activity Fetch", "Some Activity");
    ((TextView) view.findViewById(R.id.title)).setText(activity.getTitle());
    return view;
  }
}