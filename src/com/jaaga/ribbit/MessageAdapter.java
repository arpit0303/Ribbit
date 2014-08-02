package com.jaaga.ribbit;

import java.util.List;

import com.parse.ParseObject;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MessageAdapter extends ArrayAdapter<ParseObject>{
	
	protected Context mContext;
	protected List<ParseObject> mMessages;
	
	public MessageAdapter(Context context, List<ParseObject> messages) {
		super(context,R.layout.message_item,messages);
		mContext = context;
		mMessages = messages;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		
		return null;
	}
	
	private static class ViewHolder {
		ImageView iconImageView;
		TextView nameLabel;
	}
}
