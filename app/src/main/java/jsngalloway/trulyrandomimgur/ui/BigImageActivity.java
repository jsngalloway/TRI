package jsngalloway.trulyrandomimgur.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.github.piasy.biv.BigImageViewer;
import com.github.piasy.biv.loader.ImageLoader;
import com.github.piasy.biv.loader.glide.GlideImageLoader;
import com.github.piasy.biv.view.BigImageView;
import com.github.piasy.biv.view.GlideImageViewFactory;
import com.github.piasy.biv.view.ImageViewFactory;

import java.io.File;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import jsngalloway.trulyrandomimgur.ImageData;
import jsngalloway.trulyrandomimgur.R;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class BigImageActivity extends AppCompatActivity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = false;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 0;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private BigImageView bigImageView;
    private String url;
    private Toolbar toolbar;
    public final static int BIG_RESULT_NONE = 0;
    public final static int BIG_RESULT_DELETE = 1;
    public final static int BIG_RESULT_UNFAVE = 2;

    private ImageData imageData;


    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar
            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            //hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    private BigImageView mBiv;
    private int position;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle bundle = getIntent().getExtras();
        url = bundle.getString("URL", "defaultKey");
        position = bundle.getInt("position", -1);
        String filetype = bundle.getString("filetype", ".jpg");
        //long height = bundle.getLong("height", 0);
        //long width = bundle.getLong("width", 0);
        //long size = bundle.getLong("size", 0);
        imageData = new ImageData(url, filetype);
        BigImageViewer.initialize(GlideImageLoader.with(getApplicationContext()));

        super.onCreate(savedInstanceState);

        ImageViewFactory imageViewFactory = new GlideImageViewFactory();
        mBiv = new BigImageView(BigImageActivity.this);


        setContentView(R.layout.activity_big_image);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.fullscreen_content);
        mContentView = findViewById(R.id.big_image_viewm);
        mBiv = findViewById(R.id.big_image_viewm);

//
        mBiv.setImageLoaderCallback(myImageLoaderCallback);
        mBiv.setImageViewFactory(imageViewFactory);
        mBiv.showImage(Uri.parse(imageData.getFullUrl()));


        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        //findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);
        toolbar = findViewById(R.id.showHideToolbar);
        toolbar.setOnTouchListener(mDelayHideTouchListener);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        //bigImageView.showImage(Uri.parse(url));
    }

//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        inflater.inflate(R.menu.big_image_actions, menu);
//        super.onCreateOptionsMenu(menu, inflater);
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.big_image_actions, menu);
        // return true so that the menu pop up is opened
        return true;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("action", BIG_RESULT_NONE);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
                return true;
            }
            case R.id.mDelete: {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("url", imageData.getFullUrl());
                returnIntent.putExtra("action", BIG_RESULT_DELETE);
                returnIntent.putExtra("position", position);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
                return true;
            }
            case R.id.mOpen_in_imgur: {
                Intent openIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(openIntent);
            }
            case R.id.mImage_details: {
                imageData.setHeight(mBiv.getSSIV().getSHeight());
                imageData.setWidth(mBiv.getSSIV().getSWidth());
                //Log.e("HEIGHTL: ", ""+mBiv.getSSIV().getSHeight());
                String dialog = "Filetype: " + imageData.getFileType() + "\n";
                dialog += "Height: " + imageData.getHeight() + "\n";
                dialog += "Width: " + imageData.getWidth() + "\n";
                //dialog += "Size: " + (float) ((float) imageData.getSize() / 1000000) + "MB \n";
                dialog += "URL: " + imageData.getFullUrl() + "";
                new MaterialDialog(this).title(1, "Image Data").message(null, dialog, true, (float) 1.2).show();
                return true;

            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }


    /**
     * IMAGE LOADING CALLBACKS
     */
    ImageLoader.Callback myImageLoaderCallback = new ImageLoader.Callback() {
        @Override
        public void onCacheHit(int imageType, File image) {
            // Image was found in the cache
        }

        @Override
        public void onCacheMiss(int imageType, File image) {
            // Image was downloaded from the network
        }

        @Override
        public void onStart() {
            // Image download has started
        }

        @Override
        public void onProgress(int progress) {
            // Image download progress has changed
        }

        @Override
        public void onFinish() {
            Log.e("CALLBACK","image finished loading");

            //imageData.setSize(mBiv.getSSIV().);
        }

        @Override
        public void onSuccess(File image) {
            // Image was retrieved successfully (either from cache or network)
        }

        @Override
        public void onFail(Exception error) {
            // Image download failed
        }
    };
}
