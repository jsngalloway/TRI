package elfoe.trulyrandomimgur;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import elfoe.trulyrandomimgur.ui.TrulyRandom;

public class RandomImage {
    String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    String lower = upper.toLowerCase();
    String numbers = "0123456789";
    String all = upper + lower + numbers;
    public static final long IMAGE_THRESHOLD = 300*300;
    UrlList urlList;
    public static int MINIMUM_WIDTH;
    public static int MINIMUM_HEIGHT;
    public static long MINIMUM_BYTES;
    public static boolean GIF_EXCEPTION;

    public RandomImage(UrlList urlList){
        this.urlList = urlList;
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(TrulyRandom.context);
        MINIMUM_WIDTH = Integer.parseInt("0" + sp.getString("minimum_width_images",""));
        MINIMUM_HEIGHT = Integer.parseInt("0" + sp.getString("minimum_height_images",""));
        MINIMUM_BYTES = Long.parseLong("0" + sp.getString("minimum_bytes",""));
        GIF_EXCEPTION = sp.getBoolean("animation_exception", true);
    }
    String randomString(int length){
        String ret = "";
        int Alllength = all.length();
        for(int i = 0; i < length; i++) {
            int randInt = (int) (Math.random() * Alllength);
            ret = ret + all.charAt(randInt);
        }
        return ret;
    }
    String getTitle(String url) {
        //HttpURLConnection.setFollowRedirects(false);
        InputStream response = null;
        String title = null;
        try {
            response = new URL(url).openStream();
            Scanner scanner = new Scanner(response);
            String responseBody = scanner.useDelimiter("\\A").next();
            try {
                title = responseBody.substring(responseBody.indexOf("<title>") + 7, responseBody.indexOf("</title>"));
            } catch (Exception e) {
                Log.e("DEBUG: ","Error with URL: " + url);
                if (responseBody.substring(0, 20).contains("PNG")) {
                    //it's a PNG
                    scanner.close();
                    return "PNG";
                }
                e.printStackTrace();
            }
            scanner.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                response.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return title;
    }
    boolean getResponseCode(String url) throws IOException {
        //HttpURLConnection.setFollowRedirects(false);
        //Log.e("Checking response",url);
        URL u = new URL (url);
        HttpURLConnection huc =  (HttpURLConnection) u.openConnection();
        huc.setRequestMethod ("HEAD");//TODO
        huc.connect();
        int code = huc.getResponseCode();
        //if (code == 200)Log.e("DEBUG: ","SizeHead: " + huc.getContentLengthLong());
        huc.disconnect();
        return (code == 200);
    }

    public long[] validateURL(String url){
        RandomImage ri = new RandomImage(urlList);
        //TODO I'm pretty sure we can move a bunch of this out of random Image.
        try {
            if (ri.getResponseCode(url + ".jpg")) {
                //we have have response code 200
                ImageData imageData = new ImageData();
                long[] metaData = imageData.getHeightWidthBytes(url + ".jpg");
                imageData.setHeight(metaData[0]);
                imageData.setWidth(metaData[1]);
                imageData.setSize(metaData[2]);

                if(imageData.getSize() >= MINIMUM_BYTES && imageData.getWidth() >= MINIMUM_WIDTH && imageData.getHeight() >= MINIMUM_HEIGHT) {
                    //the image is large enough or it's a GIF or mp4
                    return metaData;
                } else if ( ((gifOrPng(url + ".jpg") == "GIF") && GIF_EXCEPTION)  || (ri.getResponseCode(url + ".mp4") && GIF_EXCEPTION)) {
                    Log.e("DEBUG: ","Small but special excepiton made for animated image: " + url);
                    return metaData;
                } else {
                    Log.e("VALID IMAGE FAILUE","Url: " + url + " " + " failed due to sizing.");
                    return null;
                }
            }
        } catch (IOException e) {
            Log.e("DEBUG: ","Unable to get valid response from: " + url);
            e.printStackTrace();
        }
        return null;
    }
    public void addToList(String url, String fileType, long height, long width, long size){
        urlList.addUrl(new ImageData(url, fileType, height, width, size));
    }
    public String gifOrPng(String url_str) {
        //HttpURLConnection.setFollowRedirects(false);
        try
        {
            URL url = new URL(url_str);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            //HttpURLConnection.setFollowRedirects(true);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

            String firstLine = bufferedReader.readLine();
            bufferedReader.close();
            urlConnection.disconnect();
            if (firstLine.contains("GIF")){
                Log.e("DEBUG: ","Special exception since it's a gif.");
                return "GIF";
            } else if (firstLine.contains("ftyp")) {
                //Log.e("DEBUG: ","It's a png.");
                return "MP4";
            } else if (firstLine.contains("PNG")) {
                return "PNG";
            } else {
                return null;
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
    public String getImageType(String url_str){
        try
        {
            URL url = new URL(url_str + ".jpg");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

            String firstLine = bufferedReader.readLine();
            bufferedReader.close();
            urlConnection.disconnect();
            //Log.e("DEBUG: ",firstLine);
            if (firstLine.contains("GIF")){
                return "GIF";
            } else if (firstLine.contains("ftyp")) {
                return "MP4";
            } else if (firstLine.contains("PNG")) {
                return "PNG";
            } else if (firstLine.contains("JFIF") || (firstLine.contains("ICC_PROFILE"))){
                return "JPG";
            } else{
                Log.e("DEBUG:", "JPG by default.");
                return "JPG";
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }



}
