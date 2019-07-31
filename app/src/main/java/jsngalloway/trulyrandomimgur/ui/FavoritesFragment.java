package jsngalloway.trulyrandomimgur.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import jsngalloway.trulyrandomimgur.Favorites;
import jsngalloway.trulyrandomimgur.FavoritesAdapter;
import jsngalloway.trulyrandomimgur.ImageData;
import jsngalloway.trulyrandomimgur.R;

public class FavoritesFragment extends Fragment {

    public static final String FRAG_TAG = "favoriteList";
    //private FragViewModel mViewModel;
    private ImageView imageView;
    //private StfalconImageViewer<Image> viewer;
    private RecyclerView rvFave;
    private ArrayList<ImageData> favoriteList;
    private FavoritesAdapter adapter;
    public Favorites favorites;
    public Toolbar toolbar;
    private boolean updated;

    public static FavoritesFragment newInstance() {
        return new FavoritesFragment();
    }

    public FavoritesFragment(){
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        favorites = Favorites.getInstance();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.favorites_fragment, container, false);
        }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.e("Favorite activity","we just got made");


        toolbar = (Toolbar) getActivity().findViewById(R.id.fave_toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        toolbar.setTitle("Favorites");
        setHasOptionsMenu(true);
        //toolbar.inflateMenu(R.menu.favorites_actions);


        favorites = Favorites.getInstance();
        rvFave = (RecyclerView) getActivity().findViewById(R.id.listOfFavorites);
        favoriteList = favorites.getFavoriteList();
        adapter = new FavoritesAdapter(favoriteList, Glide.with(FavoritesFragment.this), favorites, getActivity());
        rvFave.setAdapter(adapter);
        rvFave.setLayoutManager(new GridLayoutManager(this.getContext(), 3));
        updated = false;

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.favorites_actions, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_refresh :
                Log.e("item id ", item.getItemId() + "");
                adapter.notifyDataSetChanged();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public String toString(){
        return FRAG_TAG;
    }

    public void notifyAddedItem(){
        adapter.notifyItemInserted(favoriteList.size());
        updated = true;
    }

    public void notifyRemovedItem(int index){
        adapter.notifyItemRemoved(index);
        updated = true;
    }

    public void onStop(){
        //favorites.rewriteToStorage();
        super.onStop();

    }
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(hidden && updated){
            Log.e("onHidden","favorites has been hidden");
            //favorites.rewriteToStorage();
        }
    }


    //this is actually snagged from Navigation
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("RECIEVED","RESULT");
        if (requestCode == 1) {//this is the callback from favorite big image view
            if(resultCode == Activity.RESULT_OK){
                int result=data.getIntExtra("action", BigImageActivity.BIG_RESULT_NONE);
                int position=data.getIntExtra("position", -1);
                String url = data.getStringExtra("url");
                switch (result){
                    case BigImageActivity.BIG_RESULT_DELETE :
                        Log.e("doing","DELETE");
                        if (position != -1) {
                            ImageData toRemove = favorites.findImageDataByURL(url);
                            favorites.removeFavorite(toRemove);
                            adapter.notifyItemRemoved(position);//THIS COULD BE WRONH SOMETIMES
                        }
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }





}
