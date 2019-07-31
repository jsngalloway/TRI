package jsngalloway.trulyrandomimgur;

import android.util.Log;

import java.net.HttpURLConnection;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class UrlGenerator extends Thread {
    private boolean terminate = false;
    static long count;
    static long startTime;
    private long lastTime;
    //private long currentTime;
    private long lastTasks;
    static long success;
    ThreadPoolExecutor executor;



    public UrlGenerator(){
    }

    public void run(){

        startTime = System.currentTimeMillis();
        lastTime = System.currentTimeMillis();
        lastTasks = 0;
        count = 0;
        success = 0;
        generator();
    }

    public void turnOff(){
        terminate = true;
    }
    private void generator(){
        HttpURLConnection.setFollowRedirects(false);
        RandomImage ri = new RandomImage(UrlList.getInstance());
        String url = null;

        executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        //executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(4);
        Log.e("URL GENERATOR","new executor created ");
        //executor.setMaximumPoolSize(50);

        while (!terminate) {
            while ((UrlList.getInstance().getUrlListSize() < UrlList.getInstance().getListCap())) {
                /** Generate new URLs only if the Queue is less than 200 */
                if (executor.getQueue().remainingCapacity() < 200) {
                    url = ("https://i.imgur.com/" + ri.randomString(7));
                    executor.submit(new ParallelImage(url));
                }
                count = executor.getCompletedTaskCount();
                if (count % 50 == 0) {
                    updatePerSec();
                }
                if (terminate) break;
            }
                //Log.e("outer looper","looping");
                updatePerSec();
        }
        executor.shutdown();
        Log.e("Generator","terminated.");

}

void updatePerSec(){
    long currentTime = System.currentTimeMillis();
    long elapsedTime = currentTime - lastTime;
    if (elapsedTime > 500) {
        long currentTasks = executor.getCompletedTaskCount();
        long elapsedTasks = currentTasks - lastTasks;
        double perSec = (double)((double)(elapsedTasks*1000)/elapsedTime);
        //Log.e("RealTime Tasks",perSec + " tasks/sec");
        UrlList.getInstance().updateUrlsPerSecond((float)perSec);

        lastTasks = currentTasks;
        lastTime = currentTime;
    }
    }//update the screen every 100 things
}
