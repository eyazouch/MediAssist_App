package com.example.mediassist.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
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
import com.example.mediassist.activities.AddMedicationActivity;
import com.example.mediassist.database.DatabaseHelper;
import com.example.mediassist.models.Medication;
import com.example.mediassist.utils.NotificationHelper;

import java.io.File;
import java.util.List;

public class MedicationAdapter extends RecyclerView.Adapter<MedicationAdapter.MedicationViewHolder> {

    private Context context;
    private List<Medication> medicationList;
    private DatabaseHelper databaseHelper;
    private NotificationHelper notificationHelper;

    public MedicationAdapter(Context context, List<Medication> medicationList) {
        this.context = context;
        this.medicationList = medicationList;
        this.databaseHelper = new DatabaseHelper(context);
        this.notificationHelper = new NotificationHelper(context);
    }

    @NonNull
    @Override
    public MedicationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_medication, parent, false);
        return new MedicationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MedicationViewHolder holder, int position) {
        Medication medication = medicationList.get(position);

        holder.tvMedicationName.setText(medication.getName());
        holder.tvDosage.setText(medication.getDosage());
        holder.tvFrequency.setText(medication.getFrequency());
        holder.tvTime.setText(medication.getTime());

        // Afficher l'image du médicament si disponible
        if (medication.getImagePath() != null && !medication.getImagePath().isEmpty()) {
            File imgFile = new File(medication.getImagePath());
            if (imgFile.exists()) {
                holder.ivMedicationImage.setImageURI(Uri.fromFile(imgFile));
            }
        }

        //Test delete
        holder.ivDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Confirmation")
                    .setMessage("Voulez-vous vraiment supprimer ce médicament ?")
                    .setPositiveButton("Oui", (dialog, which) -> {
                        boolean success = databaseHelper.deleteMedication(medication.getId());
                        if (success) {
                            medicationList.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, medicationList.size());
                            Toast.makeText(context, "Médicament supprimé", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Erreur lors de la suppression", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Non", null)
                    .show();
        });

        //Test3
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, AddMedicationActivity.class);
            intent.putExtra("medication_id", medication.getId());
            intent.putExtra("name", medication.getName());
            intent.putExtra("dosage", medication.getDosage());
            intent.putExtra("frequency", medication.getFrequency());
            intent.putExtra("time", medication.getTime());
            intent.putExtra("image_path", medication.getImagePath());
            context.startActivity(intent);
        });

        // Gestionnaire d'événements pour le clic long sur une carte
        holder.cardMedication.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showActionsDialog(position);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return medicationList.size();
    }

    private void showActionsDialog(final int position) {
        Medication medication = medicationList.get(position);

        CharSequence[] actions = {"Modifier", "Supprimer"};

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(medication.getName());
        builder.setItems(actions, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    // Modifier le médicament
                    // Ici, vous lanceriez une activité pour modifier le médicament
                    Toast.makeText(context, "Fonctionnalité de modification à implémenter", Toast.LENGTH_SHORT).show();
                } else {
                    // Supprimer le médicament
                    showDeleteConfirmationDialog(position);
                }
            }
        });
        builder.show();
    }

    private void showDeleteConfirmationDialog(final int position) {
        Medication medication = medicationList.get(position);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Confirmation");
        builder.setMessage("Êtes-vous sûr de vouloir supprimer " + medication.getName() + " ?");
        builder.setPositiveButton("Supprimer", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Annuler la notification associée
                notificationHelper.cancelMedicationReminder(medication.getId());

                // Supprimer de la base de données
                databaseHelper.deleteMedication(medication.getId());

                // Supprimer de la liste
                medicationList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, medicationList.size());

                Toast.makeText(context, "Médicament supprimé", Toast.LENGTH_SHORT).show();

                // Si la liste est vide, rafraîchir l'activité pour afficher le message "Aucun médicament"
                if (medicationList.isEmpty()) {
                    // Cette méthode doit être implémentée dans l'activité qui utilise cet adaptateur
                    // ((MedicationsActivity)context).refreshEmptyView();
                }
            }
        });
        builder.setNegativeButton("Annuler", null);
        builder.show();
    }

    public class MedicationViewHolder extends RecyclerView.ViewHolder {
        CardView cardMedication;
        ImageView ivMedicationImage;
        TextView tvMedicationName, tvDosage, tvFrequency, tvTime;

        ImageView ivDelete;

        public MedicationViewHolder(@NonNull View itemView) {
            super(itemView);
            cardMedication = itemView.findViewById(R.id.cardMedication);
            ivMedicationImage = itemView.findViewById(R.id.ivMedicationImage);
            tvMedicationName = itemView.findViewById(R.id.tvMedicationName);
            tvDosage = itemView.findViewById(R.id.tvDosage);
            tvFrequency = itemView.findViewById(R.id.tvFrequency);
            tvTime = itemView.findViewById(R.id.tvTime);
            //Test delete
            ivDelete = itemView.findViewById(R.id.ivDelete);
        }
    }
}