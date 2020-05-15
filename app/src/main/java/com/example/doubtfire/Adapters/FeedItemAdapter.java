package com.example.doubtfire.Adapters;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doubtfire.Activities.FeedOnClick;
import com.example.doubtfire.Fragments.FeedContainingFragment;
import com.example.doubtfire.Models.ImageModel;
import com.example.doubtfire.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class FeedItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private ArrayList<ImageModel> mDataset;
    private FeedContainingFragment feedContainingFragment;

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;


    public FeedItemAdapter(ArrayList<ImageModel> myDataset, FeedContainingFragment activity) {
        mDataset = myDataset;
        feedContainingFragment = activity;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_feed_item, parent, false);
            return new ItemViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            populateItemRows((ItemViewHolder) holder, position);
        } else if (holder instanceof LoadingViewHolder) {
            showLoadingView((LoadingViewHolder) holder, position);
        }
    }



    @Override
    public int getItemCount() {
        return mDataset.size();
    }



    public void addImage(ImageModel image) {
        mDataset.add(0, image);
        Log.i(TAG, "addImage:>>>>>>>>>>>>>>>>added image to adapter " + image.downurl);
//        notifyDataSetChanged();
        notifyItemInserted(mDataset.size()-1);
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        ImageView feedImage;
        TextView feedPoster,feedDesc,feedSubject;
        public ItemViewHolder(@NonNull final View itemView) {
            super(itemView);
            feedDesc = itemView.findViewById(R.id.feed_desc);
            feedImage = itemView.findViewById(R.id.feed_item_image);
            feedPoster = itemView.findViewById(R.id.feed_who_posted);
            feedSubject = itemView.findViewById(R.id.feed_subject);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent expand = new Intent(itemView.getContext(), FeedOnClick.class);
                    expand.putExtra("imagekey",feedImage.getTag().toString());
                    expand.putExtra("name",feedPoster.getText().toString());
                    Log.i(TAG, ">>>>>>>>>>>>>>>>>>>>onClick: sent image ID "+feedImage.getTag().toString());
                    expand.setAction("feed");
                    itemView.getContext().startActivity(expand);
                }
            });
        }

//        public void populate(ImageModel imageModel){
//            if (imageModel.user != null) {
//                feedPoster.setText(imageModel.user.name);
//                feedSubject.setText(imageModel.subject);
//                feedImage.setTag(imageModel.key);
//                feedDesc.setText(imageModel.description);
//            }
//            Picasso.get().load(imageModel.downurl).fit().centerCrop().into(feedImage);
//        }
    }

    private class LoadingViewHolder extends RecyclerView.ViewHolder {

        ProgressBar progressBar;

        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }

    private void populateItemRows(ItemViewHolder viewHolder, int position) {

        ImageModel imageModel = mDataset.get(position);
        if (imageModel.user != null) {
            viewHolder.feedPoster.setText(imageModel.user.name);
            viewHolder.feedSubject.setText(imageModel.subject);
            viewHolder.feedImage.setTag(imageModel.key);
            viewHolder.feedDesc.setText(imageModel.description);
        }

        Picasso.get().load(imageModel.downurl).fit().centerCrop().into(viewHolder.feedImage);

    }

    private void showLoadingView(LoadingViewHolder viewHolder, int position) {
        //ProgressBar would be displayed

    }

}
