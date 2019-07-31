package jsngalloway.trulyrandomimgur.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import jsngalloway.trulyrandomimgur.Favorites;
import jsngalloway.trulyrandomimgur.ImageData;
import jsngalloway.trulyrandomimgur.R;
import jsngalloway.trulyrandomimgur.UrlGenerator;
import jsngalloway.trulyrandomimgur.UrlList;

//import android.content.Intent;


//mport android.content.Intent;

public class TrulyRandom extends Fragment {

    public static final String FRAG_TAG = "generator";
    private static FloatingActionButton nextBtn;
    private static FloatingActionButton faveBtn;
    //private ImageView imageView;
    private PhotoView photoView;

    public static TextView urlsPerSec;
    public static Context context;
    private UrlGenerator urlGen;
    private static Toolbar toolbar;

    public UrlList urlList;
    public static Favorites favoriteList;
    private ImageData currentImage;

    private RequestOptions requestOptions = new RequestOptions().priority(Priority.HIGH);//.placeholder(Drawable);//.onlyRetrieveFromCache(true);
    private DrawableTransitionOptions drawableTransitionOptions = new DrawableTransitionOptions().crossFade();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_truly_random, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        context = getActivity();
        urlList = UrlList.getInstance();
        urlList.init(context);
        Log.e("TrulyRandom.java: ", "urlList Created");
        favoriteList = Favorites.getInstance();
        favoriteList.init(context);
        favoriteList.loadFavesFromStorage();

        urlsPerSec = (TextView) getActivity().findViewById(R.id.urlsPerSecView);
        if(PreferenceManager.getDefaultSharedPreferences(context).getBoolean("show_url_per_sec_check", false)){
           urlsPerSec.setVisibility(View.VISIBLE);
        } else {
            urlsPerSec.setVisibility(View.GONE);
        }

        nextBtn = (FloatingActionButton) getActivity().findViewById(R.id.btnNext);
        faveBtn = (FloatingActionButton) getActivity().findViewById(R.id.btnFave);
        //imageView = (ImageView) getActivity().findViewById(R.id.imageDisplay);
        photoView = (PhotoView) getActivity().findViewById(R.id.photo_view);
        toolbar = (Toolbar) getActivity().findViewById(R.id.gen_toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);




        ((AppCompatActivity)getActivity()).setTitle("Generator");
        toolbar.setTitle("Generator");
        toolbar.setSubtitle("URLs ready:");
        setUrlsMessage(0);
        setHasOptionsMenu(true);
        greyOut(faveBtn, true);
        faveBtn.setEnabled(false);



        nextBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (currentImage == null){
                    //this is our first image displayed
                    greyOut(nextBtn, false);
                    faveBtn.setEnabled(true);
                    greyOut(faveBtn, false);
                }
                Log.e("TrulyRandom.java: ", "Button clicked, displaying next url");
                if (urlList != null && urlList.getUrlListSize() > 0) {
                    ImageData i = urlList.getNextUrl();
                    currentImage = i;
                    if (i.getFileType().equalsIgnoreCase("GIF")) {
                        Glide.with(getActivity())
                                .asGif()
                                .load(i.getUrl() + "." + i.getFileType().toLowerCase())
                                .transition(drawableTransitionOptions)
                                //.listener(requestListener)
                                .apply(requestOptions)
                                .into(photoView);
                    } else {
                        Glide.with(getActivity())
                                .load(i.getUrl() + "." + i.getFileType().toLowerCase())
                                .transition(drawableTransitionOptions)
                                //.listener(requestListener)
                                .apply(requestOptions)
                                .into(photoView);
                    }
                    //urlDisplay.setText(i.getUrl());
                    faveBtn.setImageResource(R.drawable.ic_favorite_border_black_24dp);

                } else {
                    Toast.makeText(context, "No URLS ready", Toast.LENGTH_SHORT).show();
                }
                //preload the next image if there is one
                if(urlList.getUrlListSize() > 0){
                    Glide.with(getActivity()).load(urlList.peekNextFullUrl()).preload();
                }
            }
        });
        faveBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FavoritesFragment fragm = (FavoritesFragment) getFragmentManager().findFragmentByTag(FavoritesFragment.FRAG_TAG);
                if (favoriteList.isFavorite(currentImage)){
                    //the item is already a favorite
                    if(fragm != null) {
                        int index = favoriteList.getIndexOf(currentImage);
                        fragm.notifyRemovedItem(index);
                    }
                    favoriteList.removeFavorite(currentImage);
                    Toast.makeText(context, "Removed from Favorites", Toast.LENGTH_SHORT).show();
                    faveBtn.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                } else {
                    //the item is not a favorite
                    favoriteList.addFavorite(currentImage);
                    //we have a favorites fragment already so let's add it to the list
                    if (fragm != null){
                        fragm.notifyAddedItem();
                        Log.e("FaveBtn","Notified fave to add to list");
                    }
                    Toast.makeText(context, "Added to Favorites", Toast.LENGTH_SHORT).show();
                    faveBtn.setImageResource(R.drawable.ic_favorite_black_24dp);
                }
            }
        });

    }

    @Override
    public void onDestroy() {
        Log.e("TrulyRand", "DESTROYED");
        stopGenerator(urlGen);
        super.onDestroy();
    }

    public static void setUrlsMessage(int newText) {
        String display = "URLs ready: %3d";
        display = String.format(display, newText);
        TrulyRandom.toolbar.setSubtitle(display);
        if (newText == 0){
            greyOut(nextBtn, true);
        } else {
            greyOut(nextBtn, false);
        }
    }

    public static void setUrlsPerSecond(float urlsSec) {
        String display = "URLs/sec: %3d";
        display = String.format(display, Math.round(urlsSec));
        urlsPerSec.setText(display);
    }

    private static void clearUrlsPerSecond() {
        setUrlsPerSecond(0);
    }

    private UrlGenerator startGenerator() {
        urlGen = new UrlGenerator();
        urlGen.setPriority(1);//1 is min
        urlGen.start();
        Log.e("startGenerator", "creating generator");
        return urlGen;
    }

    private void stopGenerator(UrlGenerator generatorToStop) {
        if (generatorToStop != null) {
            generatorToStop.turnOff();
            Log.e("stopGenerator", "stopping generator");
        }
        clearUrlsPerSecond();
    }

    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden){
            Log.e("onHidden","Generator has been shown");
            if (currentImage != null){
                if(!(favoriteList.isFavorite(currentImage))){
                    faveBtn.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                }
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.generator_actions, menu);


        Switch generatorSwitch = (Switch)menu.findItem(R.id.app_bar_switch)
                .getActionView().findViewById(R.id.gen_switch_toolbar);
        generatorSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Start or stop the generator
                //Log.e("SWITCH","FLIPPED");
                if (isChecked) {
                    Log.e("On switch flip", "starting generator");
                    clearUrlsPerSecond();
                    urlGen = startGenerator();
                } else {
                    Log.e("On switch flip", "stopping generator");
                    stopGenerator(urlGen);
                    clearUrlsPerSecond();
                }
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu){
        if (currentImage == null){
            toolbar.getMenu().findItem(R.id.open_in_app).setEnabled(false);
            toolbar.getMenu().findItem(R.id.copy_to_clipboard).setEnabled(false);
            toolbar.getMenu().findItem(R.id.image_details).setEnabled(false);
        } else {
            toolbar.getMenu().findItem(R.id.open_in_app).setEnabled(true);
            toolbar.getMenu().findItem(R.id.copy_to_clipboard).setEnabled(true);
            toolbar.getMenu().findItem(R.id.image_details).setEnabled(true);
        }
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_settings :
                Intent intent = new Intent(this.getActivity(), SettingsActivity.class);
                //EditText editText = (EditText) findViewById(R.id.editText);
                //String message = editText.getText().toString();
                //intent.putExtra(EXTRA_MESSAGE, message);
                this.startActivity(intent);
                return true;
            case R.id.open_in_app :
                Intent openIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(currentImage.getFullUrl()));
                startActivity(openIntent);
                return true;
            case R.id.image_details :
                String dialog = "Filetype: " + currentImage.getFileType() + "\n";
                dialog += "Height: " + currentImage.getHeight() + "\n";
                dialog += "Width: " + currentImage.getWidth() + "\n";
                dialog += "Size: " + (float)((float)currentImage.getSize()/1000000) + "MB \n";
                dialog += "URL: " + currentImage.getFullUrl() + "";
                new MaterialDialog(getActivity()).title(1, "Image Data").message(null, dialog, true, (float)1.2).show();
                return true;
            case R.id.copy_to_clipboard:
                ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Url", currentImage.getFullUrl());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public String toString() {
        return FRAG_TAG;
    }

    private static void greyOut(FloatingActionButton btn, boolean yes){
        if(yes){
            btn.setAlpha(.3f);
        } else {
            btn.setAlpha(1f);
        }
    }
}
