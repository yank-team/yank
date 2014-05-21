package com.yankteam.yank.app;

import java.io.File;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Vector;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.Toast;

import com.yankteam.yank.app.lobbyfragments.MapFragment;
import com.yankteam.yank.app.lobbyfragments.NearbyFragment;
import com.yankteam.yank.app.lobbyfragments.SavedFragment;

public class LobbyActivity extends ActionBarActivity implements ActionBar.TabListener {
	private SectionsPagerAdapter mSectionsPagerAdapter;
	private ViewPager mViewPager;

	// Fragment manager
	public static FragmentManager fragmentManager;

	String FileName;
	ImageView imageViewPhoto;
	private static final int CAMERA_WITH_DATA = 3023;
	private static final int PHOTO_PICKED_WITH_DATA = 3021;
	
	private File mCurrentPhotoFile; // picture captured from camera
	// path to store the picture
	private static final File PHOTO_DIR = new File(Environment.getExternalStorageDirectory() + "/ASoohue/CameraCache");
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lobby);

		// set up the action bar
		final ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayShowHomeEnabled(false);

		// set up the view pager
		initializePager();
		mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
			@Override
			public void onPageSelected(int position) {
				actionBar.setSelectedNavigationItem(position);
			}
		});

		// Set up tabs
		ActionBar.Tab[] lobbyTabs = {
				actionBar.newTab()
				.setIcon(R.drawable.ic_action_map)
				.setTabListener(this),
				actionBar.newTab()
				.setIcon(R.drawable.ic_action_place)
				.setTabListener(this),
				actionBar.newTab()
				.setIcon(R.drawable.ic_action_save)
				.setTabListener(this)
		};

		// append the tabs to their adapter
		for(int i=0; i<mSectionsPagerAdapter.getCount(); i++){
			actionBar.addTab(lobbyTabs[i]);
		}

		// establish the fragment manager
		fragmentManager = getSupportFragmentManager();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.lobby, menu);
		return true;
	}

	private void initializePager() {
		// create a list to hold fragments
		List<Fragment> fragments = new Vector<Fragment>();
		fragments.add(Fragment.instantiate(this, MapFragment.class.getName()));
		fragments.add(Fragment.instantiate(this, NearbyFragment.class.getName()));
		fragments.add(Fragment.instantiate(this, SavedFragment.class.getName()));
		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), fragments);
		mViewPager = (ViewPager) findViewById(R.id.lobby_pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		mViewPager.setOffscreenPageLimit(3);
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
		mViewPager.setCurrentItem(tab.getPosition());
	}

	// stubs necessitated by implementing TabListener
	@Override
	public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {}
	@Override
	public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {}

	// listener for the menu (New Yank & Settings)
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch(item.getItemId()){
		case R.id.action_newyank:
			doPickPhotoAction();
			Toast.makeText(this, "new yank !", Toast.LENGTH_SHORT).show();
			return true;
		case R.id.action_settings:
			// do some settings
			return true;
		}
		return true;
	}
	private void doPickPhotoAction() {
		Context context = this;
		// Wrap our context to inflate list items using correct theme
		final Context dialogContext = new ContextThemeWrapper(context, android.R.style.Theme_Light);
		String cancel = "Cancel";
		String[] choices;
		choices = new String[2];
		choices[0] = getString(R.string.take_photo); 
		choices[1] = getString(R.string.pick_photo);
		final ListAdapter adapter = new ArrayAdapter<String>(dialogContext, android.R.layout.simple_list_item_1,
				choices);
		final AlertDialog.Builder builder = new AlertDialog.Builder(dialogContext);
		builder.setTitle(R.string.attachToContact);
		builder.setSingleChoiceItems(adapter, -1, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				switch (which) {
				case 0: {
					String status = Environment.getExternalStorageState();
					if (status.equals(Environment.MEDIA_MOUNTED)) { // if there's SD card
						doTakePhoto(); // take a picture from the camera
					} else {
						Toast.makeText(LobbyActivity.this, "No SD card available", Toast.LENGTH_LONG).show();
					}
					break;
				}
				case 1:
					doPickPhotoFromGallery(); // get one from the user's album
					break;
				}
			}
		});
		builder.setNegativeButton(cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.create().show();
	}
	private void doTakePhoto() {
		try {
			// Launch camera to take photo for selected contact
			if (!PHOTO_DIR.exists()) {
				boolean iscreat = PHOTO_DIR.mkdirs(); // build the save path
			}
			FileName = System.currentTimeMillis() + ".jpg";
			mCurrentPhotoFile = new File(PHOTO_DIR, FileName);
			final Intent intent = getTakePickIntent(mCurrentPhotoFile);
			startActivityForResult(intent, CAMERA_WITH_DATA);
		} catch (ActivityNotFoundException e) {
			Toast.makeText(this, "photoPickerNotFoundText", Toast.LENGTH_LONG).show();
		}
	}
	
	public static Intent getTakePickIntent(File f) {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE, null);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
		return intent;
	}
	
	public static Intent getPhotoPickIntent(File f) {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
		intent.setType("image/*");
		return intent;
	}
	
	// name the picture using the current time
	private String getPhotoFileName() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat("'IMG'_yyyy-MM-dd HH:mm:ss");
		return dateFormat.format(date) + ".jpg";
	}
	
	// call the Gallery activity
	protected void doPickPhotoFromGallery() {
		try {
			Intent intent = getPhotoPickIntent();
			startActivityForResult(intent, PHOTO_PICKED_WITH_DATA);
		} catch (ActivityNotFoundException e) {
			Toast.makeText(this, R.string.photoPickerNotFoundText, Toast.LENGTH_LONG).show();
		}
	}
	
	// issue the Gallery request intent
	public static Intent getPhotoPickIntent() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
		intent.setType("image/*");
		return intent;
	}
	
	// return from the two get-picture activities
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK)
			return;
		switch (requestCode) {
		case PHOTO_PICKED_WITH_DATA:
			// I think both cases here should start a new ac to compose a description and  then 
			// do an upload
			Uri uri = data.getData();
			break;
		case CAMERA_WITH_DATA:		
			break;
		}
	}

	// the pager adapter
	public class SectionsPagerAdapter extends FragmentPagerAdapter {
		private List<Fragment> fragments;
		public SectionsPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
			super(fm);
			this.fragments = fragments;
		}

		@Override
		public Fragment getItem(int position) {
			return fragments.get(position);
		}
		@Override
		public int getCount() {
			return this.fragments.size();
		}

		@Override
		public CharSequence getPageTitle(int position) {
			switch (position) {
			case 0:
				return "MAP VIEW";
			case 1:
				return "YANK LIST";
			case 2:
				return "SAVED YANKS";
			}
			return null;
		}
	}

	// Intercept the back button so we don't go back to the login screen unless the
	// user logs out on purpose
	@Override
	public void onBackPressed(){
		// nothing yet...
	}
}
