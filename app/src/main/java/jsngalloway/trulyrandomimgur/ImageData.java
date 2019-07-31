package jsngalloway.trulyrandomimgur;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

public class ImageData {
    private String url;
    private String fileType;
    private Long height;
    private Long width;
    private Long size;
//    String title;
//    String desc;
//    String date;
//    int views;
    private String[] acceptable_fileTypes = {"JPG", "PNG", "GIF", "MP4"};

    public ImageData(String url, String fileType, long height, long width, long size){
        this.url = url;
        fileType = fileType.toUpperCase();

        if (!Arrays.asList(acceptable_fileTypes).contains(fileType)){
            Log.e("DEBUG: ","Unacceptable filetype: " + fileType);
        } else {
            this.fileType = fileType;
        }
        this.height = height;
        this.width = width;
        this.size = size;
    }
    public ImageData(String url, String fileType){
        this.url = url;
        fileType = fileType.toUpperCase();

        if (!Arrays.asList(acceptable_fileTypes).contains(fileType)){
            Log.e("DEBUG: ","Unacceptable filetype: " + fileType);
        } else {
            this.fileType = fileType;
        }
        this.height = null;
        this.width = null;
        this.size = null;
    }
    /* Empty Constructor **/
    public ImageData(){

    }

    public String getFileType(){
        return this.fileType;
    }
    public String getUrl(){
        return this.url;
    }
    public String getFullUrl(){
        return this.url + "." + this.getFileType().toLowerCase();
    }
    public String toString(){
        return this.url + " " + this.fileType;
    }
    public Long getHeight(){
        return this.height;
    }
    public Long getWidth(){
        return this.width;
    }
    public Long getSize(){
        return this.size;
    }
    public void setHeight(long height){
        this.height = height;
    }
    public void setWidth(long width){
        this.width = width;
    }
    public void setSize(long size){
        this.size = size;
    }

    /**
     *
     * @param url_str the full url of the image to assess
     * @return an array of 3 longs, the Height, Width, and Bytes of the image
     * This is considered wasteful since it involved downloading the entire image and should be used sparingly
     */
    public long[] getHeightWidthBytes(String url_str) {
        //HttpURLConnection.setFollowRedirects(false);
        try {
            URL url = new URL(url_str);
            Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            long[] arr = {bmp.getHeight(), bmp.getWidth(), bmp.getByteCount()};
            return arr;
        } catch (MalformedURLException e1) {
            Log.e("DEBUG: ","Failed to parse URL");
            e1.printStackTrace();
        } catch (IOException e) {
            Log.e("DEBUG: ","Failed to load image");
            e.printStackTrace();
        }
        Log.e("DEBUG: ","Failed to load image size. Terminating");
        System.exit(2);
        return null;
    }

    /**
     * @return imageData with the Metadata attached
     */
    public void getMetaData(){
        long[] arr =  getHeightWidthBytes(this.getFullUrl());
        this.setHeight(arr[0]);
        this.setWidth(arr[1]);
        this.setSize(arr[2]);
    }
}
