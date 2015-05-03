package us.shandian.giga.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import us.shandian.giga.R;
import us.shandian.giga.util.Utility;

public class NavigationAdapter extends BaseAdapter
{
	private Context mContext;
	private LayoutInflater mInflater;
	private String[] mStrs;
	private int[] mIcons;
	
	public NavigationAdapter(Context context, int strs, int icons) {
		mContext = context;
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mStrs = context.getResources().getStringArray(strs);
		
		String[] iconStr = context.getResources().getStringArray(icons);
		mIcons = new int[iconStr.length];
		
		for (int i = 0; i < iconStr.length; i++) {
			String str = iconStr[i];
			if (!str.equals("null")) {
				mIcons[i] = context.getResources().getIdentifier(str, "drawable", "us.shandian.giga");
			}
		}
	}

	@Override
	public int getCount() {
		return mStrs.length;
	}

	@Override
	public Object getItem(int i) {
		return mStrs[i];
	}

	@Override
	public long getItemId(int i) {
		return i;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (position >= getCount()) {
			return null;
		} else {
			String str = mStrs[position];
			View v = convertView;
			
			if (str.equals("-")) {
				v = mInflater.inflate(R.layout.drawer_divider, parent, false);
				v.setTag(1);
				return v;
			} else {
			
				if (v == null || ((Integer)v.getTag()) == 1) {
					v = mInflater.inflate(R.layout.drawer_item, parent, false);
					v.setTag(0);
				}
				
				ImageView iv = Utility.findViewById(v, R.id.drawer_icon);
				TextView tv = Utility.findViewById(v, R.id.drawer_text);
				
				if (iv != null && tv != null) {
				
					iv.setImageDrawable(mContext.getResources().getDrawable(mIcons[position]));
					tv.setText(str);
					tv.getPaint().setFakeBoldText(true);
				
				}
			
				return v;
				
			}
		}
	}

}
