package elfoe.trulyrandomimgur;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class Favorites {
    private static ArrayList<ImageData> fav_list = new ArrayList<ImageData>();
    private static final String FILE_NAME = "favorites.txt";
    private static final String FOLDER_NAME = "TRI";
    private static final String LOG_TAG = "Favorites";
    public Context context;
    private File favoritesFile = null;
    private boolean saveLocally = false;//TODO implement


    private Favorites(){};
    private static final Favorites instance = new Favorites();
    public static Favorites getInstance(){
        return instance;
    }
    public void init(Context context){
        this.context = context;
    }

    public ArrayList<ImageData> getFavoriteList(){
        return this.fav_list;
    }

    public void loadFavesFromStorage(){
        final String splitOn = " ";
        File textFile = GetTextFile();
        BufferedReader reader;
        try{
            reader = new BufferedReader(new FileReader(textFile));
            String line = reader.readLine();
            while (line != null) {
                String[] parts = line.split(splitOn);
                String url = parts[0];
                String fileType = parts[1];
                ImageData imageData = new ImageData(url, fileType);
                //addFavorite(imageData);
                this.fav_list.add(imageData);
                line = reader.readLine();
            }
            reader.close();
        } catch (FileNotFoundException fileNotFound){
        } catch (IOException e){
        }
        //TODO
    }
    private File createFaveFile(){
        if(ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            Log.e(LOG_TAG,"Permission granted for external storage");
        } else {
            Log.e(LOG_TAG,"Permission denied for external storage, asking user...");
            int result = 0;
            ActivityCompat.requestPermissions((Activity)context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, result);
            if(ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.e(LOG_TAG, "User has granted permission");
            } else {
                Log.e(LOG_TAG,"User has denied permission, favorites will not be saved");
                Toast toast = Toast.makeText(context, "Favorites will not be saved", Toast.LENGTH_LONG);
                toast.show();
                return null;
            }

        }
        if (isExternalStorageWritable()){
            File file = new File (Environment.getExternalStorageDirectory()+"/"+FOLDER_NAME);
                if (!file.mkdirs()) {
                    Log.e(LOG_TAG, "Folder already exists: " + file.getPath());
                } else {
                    Log.e(LOG_TAG, "Created folder: " + file.getPath());
                }
            return file;
        }
        Log.e(LOG_TAG,"External storage is unwritable");
        return null;
    }
    private void writeImageToStorage(ImageData imageData){
        File textFile = GetTextFile();
        Log.e(LOG_TAG, "Appending imageData (" + imageData.toString() + ") to: " + textFile.getPath());
        appendStrToFile(textFile, imageData.toString());
    }
    private File GetTextFile(){
        if (favoritesFile == null) {
            favoritesFile = createFaveFile();
        }
        return new File(favoritesFile, FILE_NAME);
    }
    private void appendStrToFile(File file, String str) {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(file, true));
            out.write(str + "\n");
            out.close();
        }
        catch (IOException e) {
            Log.e(LOG_TAG,"Unable to write to text file" + e);
        }
    }
    public void addFavorite(ImageData img){

        this.fav_list.add(img);

        //push out to storage
        writeImageToStorage(img);
        }
    public boolean removeFavorite(ImageData img){
        if (this.fav_list.contains(img)){
            this.fav_list.remove(img);
            rewriteToStorage();
            return true;
        }
        return false;
    }
    public boolean removeFavorite(int index){
        if ((index > 0) && (index < this.getSize())){
            this.fav_list.remove(index);
            rewriteToStorage();
            return true;
        }
        return false;
    }
    public ImageData findImageDataByURL(String url){
        for (int i=0; i < this.getSize(); i++){
            if(fav_list.get(i).getFullUrl().equalsIgnoreCase(url)){
                return fav_list.get(i);
            }
        }
        return null;
    }
    public boolean isFavorite(ImageData img){
        return this.fav_list.contains(img);
    }
    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }
    public String toString(){
        String retVal = "";
        for(int i=0; i<fav_list.size(); i++){
            retVal = retVal + fav_list.get(i).toString() + "\n";
        }
        return retVal;
    }
    public void rewriteToStorage(){
        File file = GetTextFile();
        String str_to_write = "";
        for(int i=0; i<fav_list.size(); i++){
                str_to_write = str_to_write + fav_list.get(i).toString() + "\n";
        }
        Log.e("Writing to storage",str_to_write);
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(file, false));
            out.write(str_to_write);
            out.close();
        }
        catch (IOException e) {
            Log.e(LOG_TAG,"Unable to write to text file" + e);
        }
    }
    public int getIndexOf(ImageData imageData){
        return fav_list.indexOf(imageData);
    }
    public int getSize(){
        return fav_list.size();
    }

}
