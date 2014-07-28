package com.jaaga.ribbit;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import com.parse.ParseUser;

public class MainActivity extends Activity implements ActionBar.TabListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v13.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;
    
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

	public static final String TAG = MainActivity.class.getSimpleName();
	
	private static final int TAKE_PHOTO_REQUEST = 0;
	private static final int TAKE_VIDEO_REQUEST = 1;
	private static final int PICK_PHOTO_REQUEST = 2;
	private static final int PICK_VIDEO_REQUEST = 3;
	
	private static final int MEDIA_TYPE_IMAGE = 4;
	private static final int MEDIA_TYPE_VIDEO = 5;
	
	protected Uri mMediaUri;
	
	protected DialogInterface.OnClickListener mDialogListener =
			new DialogInterface.OnClickListener() {
		
		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case 0:
				//Take Picture
				Intent TakePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				mMediaUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
				if(mMediaUri == null){
					//Display an Error
					Toast.makeText(MainActivity.this, 
							R.string.error_external_storage, 
							Toast.LENGTH_SHORT).show();
				}
				else{
					TakePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
					startActivityForResult(TakePhotoIntent, TAKE_PHOTO_REQUEST);
				}
				break;
				
			case 1:
				//Take Video
				Intent TakeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
				mMediaUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);
				if(mMediaUri == null){
					//Display an Error
					Toast.makeText(MainActivity.this, 
							R.string.error_external_storage, 
							Toast.LENGTH_SHORT).show();
				}
				else{
					TakeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
					TakeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 60);
					TakeVideoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);//0 = low resolution, 1 = high 
					startActivityForResult(TakeVideoIntent, TAKE_VIDEO_REQUEST);
				}
				break;
			case 2:
				//Choose Picture
				Intent ChoosePhotoIntent = new Intent(Intent.ACTION_GET_CONTENT);
				ChoosePhotoIntent.setType("image/*");
				startActivityForResult(ChoosePhotoIntent, PICK_PHOTO_REQUEST);
				break;
			case 3:
				//Choose Video
				break;
			}
		}

		private Uri getOutputMediaFileUri(int mediaType) {
			//To be safe,you should check that the SDcard is mounted
			//using Environment.getExternalStorageState().
			if(isExternalStorageAvailable()){
				//get the Uri
				
				//1.Get the External Storage Directory
				String appName = MainActivity.this.getString(R.string.app_name);
				File mediaStorageDir = new File(
						Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
						appName);
				
				//2.Create our Sub-Directory
				if(!mediaStorageDir.exists()){
					if(!mediaStorageDir.mkdirs()){
						Log.e(TAG, "Failed to create Directory.");
						return null;
					}
				}
				
				//3.Create a file name
				//4.Create the file
				File mediaFile;
				Date now = new Date();
				String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(now);
				
				String path = mediaStorageDir.getPath() + File.separator;
				if(mediaType == MEDIA_TYPE_IMAGE){
					mediaFile = new File(path + "IMG_" + timestamp + ".jpg");
				}
				else if(mediaType == MEDIA_TYPE_VIDEO){
					mediaFile = new File(path + "VID_" + timestamp + ".mp4");
				}
				else
					return null;
				
				Log.d(TAG, "File: " + Uri.fromFile(mediaFile));
				
				//5.Return the file's Uri
				return Uri.fromFile(mediaFile);
			}
			else{
				return null;
			}
				
		}
		
		private boolean isExternalStorageAvailable(){
			String state = Environment.getExternalStorageState();
			
			if(state.equals(Environment.MEDIA_MOUNTED))
				return true;
			else
				return false;
		}
	};
			
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_main);
        
        ParseUser currentuser = ParseUser.getCurrentUser();
        
        if(currentuser == null){
	        navigateToLogin();
        }
        else{
        	Log.i(TAG, currentuser.getUsername());
        }

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	
    	if(resultCode == RESULT_OK){
    		//add it to the Gallery
    		
    		if(requestCode == PICK_PHOTO_REQUEST || requestCode == PICK_VIDEO_REQUEST){
    			if(data ==null){
    				Toast.makeText(MainActivity.this, getString(R.string.general_error),
    						Toast.LENGTH_LONG).show();
    			}
    			else{
    				
    			}
    		}
    		
    		Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
    		mediaScanIntent.setData(mMediaUri);
    		sendBroadcast(mediaScanIntent);
    	}
    	else if(resultCode != RESULT_CANCELED){
    		Toast.makeText(MainActivity.this, R.string.general_error, Toast.LENGTH_LONG).show();
    	}
    }
    
	private void navigateToLogin() {
		Intent intent = new Intent(this, LoginActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(intent);
	}


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        
        switch (id) {
		case R.id.action_logout:
			ParseUser.logOut();
        	navigateToLogin();
        	
		case R.id.action_edit_friends:
			Intent intent = new Intent(this,EditFriendsActivity.class);
        	startActivity(intent);
        	
		case R.id.action_camera:
			AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
			builder.setItems(R.array.camera_choices, mDialogListener);
			
			AlertDialog dialog = builder.create();
			dialog.show();
		}
        
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    
    
    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
        	switch (position) {
			case 0:
				return new InboxFragment();
			case 1:
				return new FriendsFragment();
			}
        	return null;
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
            }
            return null;
        }
    }


}
