package com.jaaga.ribbit;

import java.util.List;

import com.parse.ParseObject;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
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
		
		if(convertView != null){
			convertView = LayoutInflater.from(mContext).inflate(R.layout.message_item, null);
			holder = new ViewHolder();
			holder.iconImageView = (ImageView) convertView.findViewById(R.id.messageIcon);
			holder.nameLabel = (TextView) convertView.findViewById(R.id.senderLabel);
			Log.d("MessageAdapter", "view");
		}
		else{
			holder = (ViewHolder)convertView.getTag();
			Log.d("MessageAdapter", "view is already defined");
		}
		
		ParseObject messgae = mMessages.get(position);
		
		if(messgae.getString(ParseConstants.KEY_FILE_TYPE).equals(ParseConstants.TYPE_IMAGE)){
			holder.iconImageView.setImageResource(R.drawable.ic_action_picture);
		}
		else {
			holder.iconImageView.setImageResource(R.drawable.ic_action_play_over_video);
		}
		
		holder.nameLabel.setText(messgae.getString(ParseConstants.KEY_SENDER_NAME));
		
		return convertView;
	}
	
	private static class ViewHolder {
		ImageView iconImageView;
		TextView nameLabel;
	}
}
