package jsngalloway.trulyrandomimgur;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;

import java.util.LinkedList;

import jsngalloway.trulyrandomimgur.ui.TrulyRandom;

public class UrlList {

    private LinkedList<ImageData> url_list = new LinkedList<ImageData>();
    private Handler handler;
    private Context context;

    private UrlList(){};
    private static final UrlList instance = new UrlList();
    public static UrlList getInstance(){
        return instance;
    }
    public void init(Context context){
        handler = new Handler(context.getMainLooper());
        this.context = context;
    }


    private void runOnUiThread(Runnable r) {
        handler.post(r);
    }
    public int getUrlListSize(){
        return this.url_list.size();
    }
    public int getListCap(){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String value = sp.getString("maximum_urls_pref","25");
        return Integer.parseInt(value);
    }
    public String getUrlListSizeStr(){
        return this.getUrlListSize() + "";
    }
    public ImageData getNextUrl(){
        ImageData ret = this.url_list.getFirst();
        this.url_list.removeFirst();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                 Stuff that updates the UI
                TrulyRandom.setUrlsMessage(getUrlListSize());
            }
        });

        return ret;
    }
    public String getNextDirectUrl(){
        ImageData ret = url_list.getFirst();
        url_list.removeFirst();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                 Stuff that updates the UI
                TrulyRandom.setUrlsMessage(getUrlListSize());
            }
        });

        return ret.getUrl() + "." + ret.getFileType().toLowerCase();
    }
    public void addUrl(ImageData toAdd){
        url_list.add(toAdd);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                 //Code to run on UI thread
                TrulyRandom.setUrlsMessage(getUrlListSize());
            }
        });
        return;
    }
    public void updateUrlsPerSecond(final float perSec){
        //final float perSec = UrlGenerator.count / ((System.currentTimeMillis() / 1000)+1 - (UrlGenerator.startTime / 1000));
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TrulyRandom.setUrlsPerSecond(perSec);
            }
        });
    }
    public String peekLastFullUrl(){
        ImageData imageData = this.url_list.peekLast();
        return imageData.getUrl() + "." + imageData.getFileType().toLowerCase();
    }
    public String peekNextFullUrl(){
        ImageData imageData = this.url_list.peek();
        return imageData.getUrl() + "." + imageData.getFileType().toLowerCase();
    }

}
