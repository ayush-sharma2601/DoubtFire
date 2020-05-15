package com.example.doubtfire.Adapters;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doubtfire.Activities.FeedOnClick;
import com.example.doubtfire.Activities.ImageViewer;
import com.example.doubtfire.Models.MyTag;
import com.example.doubtfire.Models.SolutionImage;
import com.example.doubtfire.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class SolutionAdapter extends RecyclerView.Adapter<SolutionAdapter.SolutionVH> {

    private ArrayList<SolutionImage> solutionSet;

    public SolutionAdapter(ArrayList<SolutionImage> solutionSet, FeedOnClick feedOnClick) {
        this.solutionSet = solutionSet;
        this.feedOnClick = feedOnClick;
    }

    private FeedOnClick feedOnClick;

    @NonNull
    @Override
    public SolutionVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SolutionVH(LayoutInflater.from(parent.getContext()).inflate(R.layout.solution_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SolutionVH holder, int position) {
        holder.populate(solutionSet.get(position));
    }

    @Override
    public int getItemCount() {
        return solutionSet.size();
    }
    public void addSolutionImage(SolutionImage image) {
        solutionSet.add(0, image);
        Log.i(TAG, "addImage:>>>>>>>>>>>>>>>>added image to adapter " + image.downurl);
        notifyDataSetChanged();
    }

    public class SolutionVH extends RecyclerView.ViewHolder {
        ImageView solutionPhoto;
        TextView solutionPoster,solutionDesc;
        public SolutionVH(@NonNull final View itemView) {
            super(itemView);
            solutionDesc = itemView.findViewById(R.id.solution_desc);
            solutionPhoto = itemView.findViewById(R.id.solution_item_image);
            solutionPoster = itemView.findViewById(R.id.solution_who_posted);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent imageview = new Intent(itemView.getContext(), ImageViewer.class);
                    MyTag intentTag = (MyTag) solutionPhoto.getTag();
                    Log.i(TAG, "onSolutionClick: >>>>>>>>>>>>>>>" +intentTag.key);
                    imageview.putExtra("key",intentTag.key);
                    imageview.putExtra("magnifier",intentTag.magnifier);
                    itemView.getContext().startActivity(imageview);
                }
            });
        }

        public void populate(SolutionImage solutionImage) {
            if(solutionImage.user!=null){
                solutionDesc.setText(solutionImage.description);
                solutionPoster.setText(solutionImage.user.name);
                MyTag myTag = new MyTag(solutionImage.key,solutionImage.magnifierkey);
                solutionPhoto.setTag(myTag);
            }
            Picasso.get().load(solutionImage.downurl).into(solutionPhoto);
        }
    }
}
