package com.example.mediassist.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mediassist.R;

public class WelcomeSliderAdapter extends RecyclerView.Adapter<WelcomeSliderAdapter.SliderViewHolder> {

    private Context context;
    private int[] imageResources;
    private String[] titles;
    private String[] descriptions;

    public WelcomeSliderAdapter(Context context, int[] imageResources, String[] titles, String[] descriptions) {
        this.context = context;
        this.imageResources = imageResources;
        this.titles = titles;
        this.descriptions = descriptions;
    }

    @NonNull
    @Override
    public SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.slide_item, parent, false);
        return new SliderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SliderViewHolder holder, int position) {
        holder.slideImage.setImageResource(imageResources[position]);
        holder.slideTitle.setText(titles[position]);
        holder.slideDescription.setText(descriptions[position]);
    }

    @Override
    public int getItemCount() {
        return titles.length;
    }

    public class SliderViewHolder extends RecyclerView.ViewHolder {
        ImageView slideImage;
        TextView slideTitle;
        TextView slideDescription;

        public SliderViewHolder(@NonNull View itemView) {
            super(itemView);
            slideImage = itemView.findViewById(R.id.slideImage);
            slideTitle = itemView.findViewById(R.id.slideTitle);
            slideDescription = itemView.findViewById(R.id.slideDescription);
        }
    }
}