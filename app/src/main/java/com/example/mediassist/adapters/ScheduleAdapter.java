package com.example.mediassist.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mediassist.R;
import com.example.mediassist.models.ScheduleEvent;

import java.util.List;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.EventViewHolder> {

    private Context context;
    private List<ScheduleEvent> eventList;

    public ScheduleAdapter(Context context, List<ScheduleEvent> eventList) {
        this.context = context;
        this.eventList = eventList;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_schedule_event, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        ScheduleEvent event = eventList.get(position);

        holder.tvTime.setText(event.getTime());
        holder.tvTitle.setText(event.getTitle());
        holder.tvDescription.setText(event.getDescription());

        // Définir l'icône et la couleur en fonction du type d'événement
        if ("medication".equals(event.getType())) {
            holder.ivEventIcon.setImageResource(R.drawable.ic_medications);
            holder.timeIndicator.setBackgroundColor(ContextCompat.getColor(context, R.color.primary));
            holder.cardEvent.setCardBackgroundColor(ContextCompat.getColor(context, R.color.accent));
        } else {
            holder.ivEventIcon.setImageResource(R.drawable.ic_appointments);
            holder.timeIndicator.setBackgroundColor(ContextCompat.getColor(context, R.color.primary_dark));
            holder.cardEvent.setCardBackgroundColor(ContextCompat.getColor(context, R.color.background));
        }
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public class EventViewHolder extends RecyclerView.ViewHolder {
        CardView cardEvent;
        View timeIndicator;
        ImageView ivEventIcon;
        TextView tvTime, tvTitle, tvDescription;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            cardEvent = itemView.findViewById(R.id.cardEvent);
            timeIndicator = itemView.findViewById(R.id.timeIndicator);
            ivEventIcon = itemView.findViewById(R.id.ivEventIcon);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
        }
    }
}