package com.example.mediassist.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mediassist.R;
import com.example.mediassist.activities.AddAppointmentActivity;
import com.example.mediassist.database.DatabaseHelper;
import com.example.mediassist.models.Appointment;
import com.example.mediassist.utils.NotificationHelper;

import java.util.List;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder> {

    private Context context;
    private List<Appointment> appointmentList;
    private DatabaseHelper databaseHelper;
    private NotificationHelper notificationHelper;

    public AppointmentAdapter(Context context, List<Appointment> appointmentList) {
        this.context = context;
        this.appointmentList = appointmentList;
        this.databaseHelper = new DatabaseHelper(context);
        this.notificationHelper = new NotificationHelper(context);
    }

    @NonNull
    @Override
    public AppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_appointment, parent, false);
        return new AppointmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentViewHolder holder, int position) {
        Appointment appointment = appointmentList.get(position);

        holder.tvDoctor.setText(appointment.getDoctor());
        holder.tvType.setText(appointment.getType());
        holder.tvDate.setText(appointment.getDate());
        holder.tvTime.setText(appointment.getTime());

        // Définir l'icône en fonction du type de rendez-vous
        if ("Consultation".equals(appointment.getType())) {
            holder.ivTypeIcon.setImageResource(R.drawable.ic_doctor);
        } else {
            holder.ivTypeIcon.setImageResource(R.drawable.ic_analysis);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, AddAppointmentActivity.class);
            intent.putExtra("appointment_id", appointment.getId());
            intent.putExtra("date", appointment.getDate());
            intent.putExtra("time", appointment.getTime());
            intent.putExtra("type", appointment.getType());
            intent.putExtra("doctor", appointment.getDoctor());
            intent.putExtra("notes", appointment.getNotes()); // Si vous avez des notes dans votre modèle Appointment
            context.startActivity(intent);
        });

        holder.ivDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Confirmation")
                    .setMessage("Voulez-vous vraiment supprimer ce rendez-vous ?")
                    .setPositiveButton("Oui", (dialog, which) -> {
                        boolean success = databaseHelper.deleteAppointment(appointment.getId());
                        if (success) {
                            appointmentList.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, appointmentList.size());
                            Toast.makeText(context, "rendez-vous supprimé", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Erreur lors de la suppression", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Non", null)
                    .show();
        });

        // Gestionnaire d'événements pour le clic long sur une carte
        holder.cardAppointment.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showActionsDialog(position);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return appointmentList.size();
    }

    private void showActionsDialog(final int position) {
        Appointment appointment = appointmentList.get(position);

        CharSequence[] actions = {"Modifier", "Supprimer"};

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(appointment.getDoctor() + " - " + appointment.getType());
        builder.setItems(actions, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    // Modifier le rendez-vous
                    // Ici, vous lanceriez une activité pour modifier le rendez-vous
                    Toast.makeText(context, "Fonctionnalité de modification à implémenter", Toast.LENGTH_SHORT).show();
                } else {
                    // Supprimer le rendez-vous
                    showDeleteConfirmationDialog(position);
                }
            }
        });
        builder.show();
    }

    private void showDeleteConfirmationDialog(final int position) {
        Appointment appointment = appointmentList.get(position);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Confirmation");
        builder.setMessage("Êtes-vous sûr de vouloir supprimer ce rendez-vous avec " + appointment.getDoctor() + " ?");
        builder.setPositiveButton("Supprimer", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Annuler la notification associée
                notificationHelper.cancelAppointmentReminder(appointment.getId());

                // Supprimer de la base de données
                databaseHelper.deleteAppointment(appointment.getId());

                // Supprimer de la liste
                appointmentList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, appointmentList.size());

                Toast.makeText(context, "Rendez-vous supprimé", Toast.LENGTH_SHORT).show();

                // Si la liste est vide, rafraîchir l'activité pour afficher le message "Aucun rendez-vous"
                if (appointmentList.isEmpty()) {
                    // Cette méthode doit être implémentée dans l'activité qui utilise cet adaptateur
                    // ((AppointmentsActivity)context).refreshEmptyView();
                }
            }
        });
        builder.setNegativeButton("Annuler", null);
        builder.show();
    }

    public class AppointmentViewHolder extends RecyclerView.ViewHolder {
        public View ivDelete;
        CardView cardAppointment;
        ImageView ivTypeIcon;
        TextView tvDoctor, tvType, tvDate, tvTime;

        public AppointmentViewHolder(@NonNull View itemView) {
            super(itemView);
            cardAppointment = itemView.findViewById(R.id.cardAppointment);
            ivTypeIcon = itemView.findViewById(R.id.ivTypeIcon);
            tvDoctor = itemView.findViewById(R.id.tvDoctor);
            tvType = itemView.findViewById(R.id.tvType);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvTime = itemView.findViewById(R.id.tvTime);
            ivDelete = itemView.findViewById(R.id.ivDelete);
        }
    }
}