// Custom adapter class for RecyclerView used for displaying bluetooth devices //
package com.example.myapplication;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "RecyclerViewAdapter"; //debugging

    private ArrayList<String> mImageNames;  // = new ArrayList<>();
    private ArrayList<String> mImages;      // = new ArrayList<>();
    private Context mContext;

    // Default constructor will obtain image, image name, and context
    public RecyclerViewAdapter(Context context, ArrayList<String> imageNames, ArrayList<String> images) {
        mImageNames = imageNames;
        mImages = images;
        mContext = context;

    }

    // Method that inflates view (recycles view holder)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_listitem, parent, false);
        ViewHolder holder = new ViewHolder(view); //imageView widget
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called."); //good for debugging
        // Get images
        Glide.with(mContext)
                .asBitmap()                  //image type
                .load(mImages.get(position)) //reference image URLs
                .into(holder.image);         //target - circle image

        holder.imageName.setText(mImageNames.get(position));

        // Display name when clicked on
        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Log.d(TAG, "onClick: clicked on: " + mImageNames.get(position));
                Toast.makeText(mContext, mImageNames.get(position), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Tells adapter how many list items are in list
    @Override
    public int getItemCount() {
        return mImageNames.size();
    }


    // Hold widgets (circle image and text) in memory of each individual entry
    public class ViewHolder extends RecyclerView.ViewHolder{

        CircleImageView image;
        TextView imageName;
        RelativeLayout parentLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            imageName = itemView.findViewById(R.id.imageName);
            parentLayout = itemView.findViewById((R.id.parent_layout));
        }

    }
}
