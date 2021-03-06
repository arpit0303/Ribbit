package com.jaaga.ribbit.ui;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.jaaga.ribbit.R;
import com.jaaga.ribbit.adapter.UserAdapter;
import com.jaaga.ribbit.utils.ParseConstants;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class EditFriendsActivity extends Activity {

	private static final String TAG = EditFriendsActivity.class.getSimpleName();
	protected List<ParseUser> musers;
	protected ParseRelation<ParseUser> mFriendsRelation;
	protected ParseUser mCurrentUser;
	protected GridView mGridView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.user_grid);
		// Show the Up button in the action bar.
		setupActionBar();
		
		mGridView = (GridView) findViewById(R.id.friendsGrid);
		
		mGridView.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE);
		mGridView.setOnItemClickListener(mOnItemClickListener);
		
		TextView emptyTextView = (TextView) findViewById(android.R.id.empty);
        mGridView.setEmptyView(emptyTextView);
	}

	@Override
	protected void onResume() {
		super.onResume();

		mCurrentUser = ParseUser.getCurrentUser();
		mFriendsRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);
		Log.d(TAG,mFriendsRelation.getQuery().getClassName());
		
		setProgressBarIndeterminateVisibility(true);
		
		ParseQuery<ParseUser> query = ParseUser.getQuery();
		query.orderByAscending(ParseConstants.KEY_USERNAME);
		query.setLimit(1000);

		query.findInBackground(new FindCallback<ParseUser>() {

			@Override
			public void done(List<ParseUser> users, ParseException e) {
				setProgressBarIndeterminateVisibility(false);
				
				if (e == null) {
					// success
					musers = users;
					String[] usernames = new String[musers.size()];
					int i = 0;
					for (ParseUser user : musers) {
						usernames[i] = user.getUsername();
						i++;
					}

					if(mGridView.getAdapter() == null){
						UserAdapter adapter = new UserAdapter(EditFriendsActivity.this, musers);
						mGridView.setAdapter(adapter);
					}
					else{
						((UserAdapter)mGridView.getAdapter()).refill(musers);
					}
					
					addFriendCheckMarks();

				} else {
					Log.e(TAG, e.getMessage());
					AlertDialog.Builder builder = new AlertDialog.Builder(
							EditFriendsActivity.this);
					builder.setTitle("Oops!").setMessage(e.getMessage())
							.setPositiveButton(android.R.string.ok, null);

					AlertDialog dialog = builder.create();
					dialog.show();
				}
			}	
		});
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

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
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void addFriendCheckMarks() {
		mFriendsRelation.getQuery().findInBackground(new FindCallback<ParseUser>() {
			
			@Override
			public void done(List<ParseUser> friends, ParseException e) {
				if(e == null){
					//list returned - look for a match
					for(int i=0; i<musers.size(); i++){
						ParseUser user = musers.get(i);
						
						for(ParseUser friend: friends){
							if(friend.getObjectId().equals(user.getObjectId())){
								mGridView.setItemChecked(i, true);
							}
	 					}
					}
				}
				else{
					Log.e(TAG, e.getMessage());
				}
			}
		});
	}
	
	protected OnItemClickListener mOnItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			ImageView checkImageView =(ImageView)view.findViewById(R.id.checkImageView);
			
			if(mGridView.isItemChecked(position)){
				//add Friends
				mFriendsRelation.add(musers.get(position));
				checkImageView.setVisibility(view.VISIBLE);
			}
			else{
				//Remove Friends
				mFriendsRelation.remove(musers.get(position));
				checkImageView.setVisibility(view.INVISIBLE);
			}
			
			mCurrentUser.saveInBackground(new SaveCallback() {
				
				@Override
				public void done(ParseException e) {
					setProgressBarIndeterminateVisibility(false);
					if(e != null){
						Log.e(TAG, e.getMessage());
						
					}
				}
			});
		}
	};
}
