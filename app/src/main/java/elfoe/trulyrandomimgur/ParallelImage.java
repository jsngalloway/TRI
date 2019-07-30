package elfoe.trulyrandomimgur;

import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import elfoe.trulyrandomimgur.ui.TrulyRandom;

public class ParallelImage implements Runnable {
    String url;
    RandomImage ri;

    public ParallelImage(String url) {
        this.url = url;
        this.ri = new RandomImage(UrlList.getInstance());
    }

    public void run() {
        long[] data = ri.validateURL(url);
        if (data != null) {
            String fileType  = ri.getImageType(url);
            //Log.e("DEBUG: ","FILE TYPE IS: " + fileType);
            UrlGenerator.success++;
            Log.e("DEBUG: ",(float) UrlGenerator.count / (System.currentTimeMillis() / 1000 - UrlGenerator.startTime / 1000) + " URL/sec " + (int) (UrlGenerator.count / UrlGenerator.success) + " fail/win. \t" + url + " ." + fileType);

            ri.addToList(url, fileType, data[0], data[1], data[2]);

            RequestOptions ro = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL);
            Glide.with(TrulyRandom.context).load(UrlList.getInstance().peekLastFullUrl()).apply(ro).submit();
            //  Log.e("Downloading", "downloading... (" + UrlList.peekLastFullUrl() + ")");
        }
        //Log.e("Ending task",url);
    }
}

