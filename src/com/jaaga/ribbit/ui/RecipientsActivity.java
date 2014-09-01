package com.jaaga.ribbit.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jaaga.ribbit.R;
import com.jaaga.ribbit.adapter.UserAdapter;
import com.jaaga.ribbit.utils.FileHelper;
import com.jaaga.ribbit.utils.ParseConstants;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class RecipientsActivity extends Activity {

	private static final String TAG = RecipientsActivity.class.getSimpleName();
	protected List<ParseUser> mFriends;
	protected ParseRelation<ParseUser> mFriendsRelation;
	protected ParseUser mCurrentUser;

	protected MenuItem mSendMenuItem;

	protected Uri mMediaUri;
	protected String mFileType;
	protected GridView mGridView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.user_grid);
		// Show the Up button in the action bar.
		setupActionBar();

		mGridView = (GridView) findViewById(R.id.friendsGrid);
		mGridView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		mGridView.setOnItemClickListener(mOnItemClickListener);
		
		TextView emptyTextView = (TextView) findViewById(android.R.id.empty);
        mGridView.setEmptyView(emptyTextView);

		mMediaUri = getIntent().getData();
		mFileType = getIntent().getExtras().getString(
				ParseConstants.KEY_FILE_TYPE);

	}

	@Override
	public void onResume() {
		super.onResume();

		mCurrentUser = ParseUser.getCurrentUser();
		mFriendsRelation = mCurrentUser
				.getRelation(ParseConstants.KEY_FRIENDS_RELATION);

		setProgressBarIndeterminateVisibility(true);

		ParseQuery<ParseUser> query = mFriendsRelation.getQuery();
		query.addAscendingOrder(ParseConstants.KEY_USERNAME);

		query.findInBackground(new FindCallback<ParseUser>() {

			@Override
			public void done(List<ParseUser> friends, ParseException e) {
				setProgressBarIndeterminateVisibility(false);
				if (e == null) {
					mFriends = friends;

					String[] usernames = new String[mFriends.size()];
					int i = 0;
					for (ParseUser user : mFriends) {
						usernames[i] = user.getUsername();
						i++;
					}

					if (mGridView.getAdapter() == null) {
						UserAdapter adapter = new UserAdapter(
								RecipientsActivity.this, mFriends);
						mGridView.setAdapter(adapter);
					} else {
						((UserAdapter) mGridView.getAdapter()).refill(mFriends);
					}
				} else {
					Log.e(TAG, e.getMessage());
					AlertDialog.Builder builder = new AlertDialog.Builder(
							RecipientsActivity.this);
					builder.setTitle("Oops!").setMessage(e.getMessage())
							.setPositiveButton(android.R.string.ok, null);

					AlertDialog dialog = builder.create();
					dialog.show();
				}
			};
		});
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.recipients, menu);
		mSendMenuItem = menu.getItem(0);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		case R.id.action_send:
			ParseObject message = CreateMessage();

			if (message == null) {
				// error
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle(R.string.error_selecting_file_title)
						.setMessage(R.string.error_selecting_file)
						.setPositiveButton(android.R.string.ok, null);
				AlertDialog dialog = builder.create();
				dialog.show();
			} else {
				send(message);
				finish();

			}

			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void send(ParseObject message) {
		message.saveInBackground(new SaveCallback() {

			@Override
			public void done(ParseException e) {
				if (e == null) {
					// success
					Toast.makeText(RecipientsActivity.this,
							R.string.success_message, Toast.LENGTH_LONG).show();
					sendPushNotifications();
				} else {
					AlertDialog.Builder builder = new AlertDialog.Builder(
							RecipientsActivity.this);
					builder.setTitle(R.string.error_selecting_file_title)
							.setMessage(R.string.error_sending_message)
							.setPositiveButton(android.R.string.ok, null);
					AlertDialog dialog = builder.create();
					dialog.show();

				}

			}
		});
	}

	private ParseObject CreateMessage() {
		ParseObject message = new ParseObject(ParseConstants.CLASS_MESSAGE);
		message.put(ParseConstants.KEY_SENDER_ID, ParseUser.getCurrentUser()
				.getObjectId());
		message.put(ParseConstants.KEY_SENDER_NAME, ParseUser.getCurrentUser()
				.getUsername());
		message.put(ParseConstants.KEY_RECIPIENTS_IDs, getRecipientsIds());
		message.put(ParseConstants.KEY_FILE_TYPE, mFileType);

		byte[] fileBytes = FileHelper.getByteArrayFromFile(
				RecipientsActivity.this, mMediaUri);

		if (fileBytes == null) {
			return null;
		} else {
			if (mFileType.equals(ParseConstants.TYPE_IMAGE)) {
				fileBytes = FileHelper.reduceImageForUpload(fileBytes);
			}

			String fileName = FileHelper.getFileName(RecipientsActivity.this,
					mMediaUri, mFileType);
			ParseFile file = new ParseFile(fileName, fileBytes);
			message.put(ParseConstants.KEY_FILE, file);
			return message;
		}

	}

	private ArrayList<String> getRecipientsIds() {
		ArrayList<String> recipientIds = new ArrayList<String>();
		for (int i = 0; i < mGridView.getCount(); i++) {
			if (mGridView.isItemChecked(i)) {
				recipientIds.add(mFriends.get(i).getObjectId());
			}
		}
		return recipientIds;
	}

	protected OnItemClickListener mOnItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View view, int position,
				long arg3) {
			if (mGridView.getCheckedItemCount() > 0) {
				mSendMenuItem.setVisible(true);
			} else {
				mSendMenuItem.setVisible(false);
			}

			ImageView checkImageView = (ImageView) view
					.findViewById(R.id.checkImageView);

			if (mGridView.isItemChecked(position)) {
				// add recipient
				checkImageView.setVisibility(view.VISIBLE);
			} else {
				// Remove recipient
				checkImageView.setVisibility(view.INVISIBLE);
			}
		}
	};
	
	protected void sendPushNotifications(){
		ParseQuery<ParseInstallation> query = ParseInstallation.getQuery();
		query.whereContainedIn(ParseConstants.KEY_USER_ID, getRecipientsIds());
		
		//send push notification
		ParsePush push = new ParsePush();
		push.setQuery(query);
		push.setMessage(getString(R.string.push_message,
				ParseUser.getCurrentUser().getUsername()));
		push.sendInBackground();
	}
}
