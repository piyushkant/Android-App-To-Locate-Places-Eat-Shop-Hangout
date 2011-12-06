package com.playcez;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;

public class ReviewBySmily extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.review);
		
		GridView gridView = (GridView) findViewById(R.id.smiy_grid);
		gridView.setAdapter(new ButtonAdapter(this));
	}

	public class ButtonAdapter extends BaseAdapter{

		private Context mContext;
		private String[] smilies = {
				"Blissful", "Love It",
				"Devilish", "Surprises Me",
				"Sick", "Hate It",
				"Make Me Cry", "Angry"
		};
		
		public ButtonAdapter(Context c){
			mContext = c;
		}
		
		public int getCount() {
			// TODO Auto-generated method stub
			return smilies.length;
		}

		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		public View getView(final int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			Button btn;
			if(convertView == null){
				btn = new Button(mContext);
				btn.setLayoutParams(new GridView.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
				btn.setPadding(8, 8, 8, 8);
			} else{
				btn = (Button) convertView;
			}
			btn.setText(smilies[position]);
			btn.setBackgroundDrawable(getApplicationContext().getResources().getDrawable(R.drawable.color_gradient));
			
			btn.setOnClickListener(new OnClickListener(){
				
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent mIntent = new Intent(getApplicationContext(), AddSmallTip.class);
					mIntent.putExtra("position", position+"");
					//getIntent().getStringExtra(name);
					startActivityForResult(mIntent,1);
				}				
			});
			
			Drawable img;
			switch(position){
			case 0:	
				img = getApplicationContext().getResources().getDrawable(R.drawable.bliss);
				btn.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);
				break;
			case 1:
				img = getApplicationContext().getResources().getDrawable(R.drawable.loveit);
				btn.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);
				break;
			case 2:
				img = getApplicationContext().getResources().getDrawable(R.drawable.devilish);
				btn.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);
				break;
			case 3:
				img = getApplicationContext().getResources().getDrawable(R.drawable.surprise);
				btn.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);
				break;
			case 4:
				img = getApplicationContext().getResources().getDrawable(R.drawable.sick);
				btn.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);
				break;
			case 5:
				img = getApplicationContext().getResources().getDrawable(R.drawable.hateit);
				btn.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);
				break;
			case 6:
				img = getApplicationContext().getResources().getDrawable(R.drawable.crying);
				btn.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);
				break;
			case 7:
				img = getApplicationContext().getResources().getDrawable(R.drawable.angry);
				btn.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);
				break;
			}
			return btn;
		}
		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		finish();
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	
}
