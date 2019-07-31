package jsngalloway.trulyrandomimgur;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import jsngalloway.trulyrandomimgur.ui.FavoritesFragment;
import jsngalloway.trulyrandomimgur.ui.TrulyRandom;

public class Navigation extends AppCompatActivity /*implements OnFragmentInteractionListener*/ {

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_generator:
                    switchFrag2(TrulyRandom.FRAG_TAG);
                    return true;
                case R.id.navigation_favorites:
                    switchFrag2(FavoritesFragment.FRAG_TAG);
                    return true;
                case R.id.navigation_browse:
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        //we open default to the generator
        switchFrag2(TrulyRandom.FRAG_TAG);
    }

    public void switchFrag2(String fragTag){
        FragmentManager fragmentManager = getSupportFragmentManager();

        //The new fragment to show
        Fragment fragment = fragmentManager.findFragmentByTag(fragTag);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        //hide all other fragments
        for(Fragment frag : fragmentManager.getFragments()){
            if (!frag.toString().equalsIgnoreCase(fragTag)){
                fragmentTransaction.hide(frag);
            }
        }
        if (fragment == null) {
            // fragment must be added, it doesn't exist yet
            if (fragTag.equalsIgnoreCase(TrulyRandom.FRAG_TAG)){
                fragment = new TrulyRandom();
            } else if (fragTag.equalsIgnoreCase(FavoritesFragment.FRAG_TAG)){
                fragment = new FavoritesFragment();

            } else {
                Log.e("Fragment switch 2","undefined fragment");
                return;
            }
            fragmentTransaction.add(R.id.fragmentContainer, fragment, fragment.toString());
            Log.e("frag2","Creating " + fragment.toString());
            //else if browse
        } else {
            fragmentTransaction.show(fragment);
            Log.e("frag2","Showing " + fragment.toString());
        }
        fragmentTransaction.commit();
    }

//    public Fragment getFragment(String tag){
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        Fragment ret = fragmentManager.findFragmentByTag(tag);
//        return ret;
//    }

//    public void requestWritePermission(){
//        int result = 1;
//        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, result);
//    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Fragment FavoriteFrag = getSupportFragmentManager().findFragmentByTag(FavoritesFragment.FRAG_TAG);
        Log.e("onRet","returned with result");
        FavoriteFrag.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

}
