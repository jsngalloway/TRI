package elfoe.trulyrandomimgur;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import elfoe.trulyrandomimgur.ui.BigImageActivity;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.ViewHolder> {
    private List<ImageData> mImageData;
    private final RequestManager glide;
    private Favorites favorites;
    private Context context;
    private FrameLayout frameLayout;
    private RequestOptions requestOptions = new RequestOptions().centerCrop().placeholder(R.drawable.ic_image_white_24dp);//.onlyRetrieveFromCache(true);


    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public ImageView imageView;
        //public CheckBox delCheck;
        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.image_View);
            frameLayout = (FrameLayout) itemView.findViewById(R.id.fragLayout);
            //int width = calculateColumnWidth(context, 3);
            //frameLayout.setLayoutParams(new LinearLayout.LayoutParams(width,width));


        }
    }
    public FavoritesAdapter(List<ImageData> imageData, RequestManager glide, Favorites favorites, Context context){
        mImageData = imageData;
        this.glide = glide;
        this.favorites = favorites;
        this.context = context;
    }

    // Usually involves inflating a layout from XML and returning the holder
    @Override
    public FavoritesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View favoriteView = inflater.inflate(R.layout.favorite_item_layout, parent, false);


        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(favoriteView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(final FavoritesAdapter.ViewHolder viewHolder, final int position) {
        // Get the data model based on position
        final ImageData imageData = mImageData.get(position);

        ImageView imageView_M = viewHolder.imageView;
        glide.load(imageData.getFullUrl()).apply(requestOptions).into(imageView_M);
        imageView_M.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(context, BigImageActivity.class);
                intent.putExtra("URL", imageData.getUrl());
                intent.putExtra("filetype", imageData.getFileType());

//                intent.putExtra("height", imageData.getHeight());
//                intent.putExtra("width", imageData.getWidth());
//                intent.putExtra("size", imageData.getSize());

                intent.putExtra("position", position);
                ((Activity)context).startActivityForResult(intent, 1);
                //1 is delete
                //2 is unfavorite
                //TODO arent these the same?
            }
        });
    }

    //@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("RECIEVED","RESULT");
        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                int result=data.getIntExtra("result", BigImageActivity.BIG_RESULT_NONE);
                int position=data.getIntExtra("position", -1);
                switch (result){
                    case BigImageActivity.BIG_RESULT_DELETE :
                        if (position != -1) {
                            notifyItemRemoved(position);
                        }
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }//onActivityResult

    @Override
    public int getItemCount() {
        return mImageData.size();
    }

    private int calculateColumnWidth(Context context, int columns) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        return (int)(dpWidth / columns);
    }

}
