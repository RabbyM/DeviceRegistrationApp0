// Custom adapter class for RecyclerView used for displaying bluetooth devices //
package com.example.deviceregistration.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deviceregistration.R;
import com.example.deviceregistration.activities.SerialNumberActivity;

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

    // Called when adapter is created in main
    // Uses the layout provided by the xml layout file
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_listitem, parent, false);
        ViewHolder holder = new ViewHolder(view); //imageView widget
        return holder;
    }


    // This method is called for each ViewHolder to bind it to the adapter
    // This is where we will pass our data to our ViewHolder
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called.");
        // Handles image loading to circleimageview
//        Glide.with(mContext)
//                .asBitmap()                  //image type
//                .load(mImages.get(position)) //reference image URLs
//                .into(holder.image);         //changes default mipmap circleimageview to drawable png

        // Set the name of bluetooth device to the viewholder
        holder.imageName.setText(mImageNames.get(position));

        // Display name when clicked on and open the device activity
        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Log.d(TAG, "onClick: clicked on: " + mImageNames.get(position));

                // Show user name/power of bluetooth device
                Toast.makeText(mContext, mImageNames.get(position), Toast.LENGTH_SHORT).show();

                // Create intent getting the context of your View and the class where you want to go
                Intent intent = new Intent(view.getContext(), SerialNumberActivity.class);
                intent.putExtra("MAC", mImageNames.get(position));

                // Start the activity from the view/context
                view.getContext().startActivity(intent); //If you are inside activity, otherwise pass context to this function

            }
        });
    }

    // Tells adapter how many list items are in the list
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
