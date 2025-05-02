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
import com.example.mediassist.activities.AddPrescriptionActivity;
import com.example.mediassist.activities.PrescriptionsActivity;
import com.example.mediassist.database.DatabaseHelper;
import com.example.mediassist.models.Prescription;

import java.io.File;
import java.util.List;

public class PrescriptionAdapter extends RecyclerView.Adapter<PrescriptionAdapter.PrescriptionViewHolder> {

    private Context context;
    private List<Prescription> prescriptionList;
    private DatabaseHelper databaseHelper;

    public PrescriptionAdapter(Context context, List<Prescription> prescriptionList) {
        this.context = context;
        this.prescriptionList = prescriptionList;
        this.databaseHelper = new DatabaseHelper(context);
    }

    @NonNull
    @Override
    public PrescriptionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_prescription, parent, false);
        return new PrescriptionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PrescriptionViewHolder holder, int position) {
        Prescription prescription = prescriptionList.get(position);

        holder.tvDate.setText(prescription.getDate());
        holder.tvDescription.setText(prescription.getDescription());

        // Afficher l'image de l'ordonnance si disponible
        if (prescription.getImagePath() != null && !prescription.getImagePath().isEmpty()) {
            File imgFile = new File(prescription.getImagePath());
            if (imgFile.exists()) {
                holder.ivPrescriptionImage.setImageURI(Uri.fromFile(imgFile));
            }
        }

        //suppression
        holder.ivDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Confirmation")
                    .setMessage("Voulez-vous vraiment supprimer cette ordonnance ?")
                    .setPositiveButton("Oui", (dialog, which) -> {
                        boolean success = databaseHelper.deleteMedication(prescription.getId());
                        if (success) {
                            prescriptionList.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, prescriptionList.size());
                            Toast.makeText(context, "Ordonnance supprimée", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Erreur lors de la suppression", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Non", null)
                    .show();
        });

        holder.ivPrescriptionImage.setOnClickListener(v -> {
            String imagePath = prescription.getImagePath(); // suppose que tu as une méthode getImagePath()

            if (imagePath != null && !imagePath.isEmpty()) {
                // Récupération du context et cast vers l'activité
                if (context instanceof PrescriptionsActivity) {
                    ((PrescriptionsActivity) context).showImageOverlay(imagePath);
                }
            } else {
                Toast.makeText(context, "Aucune image disponible pour cette ordonnance", Toast.LENGTH_SHORT).show();
            }
        });

        // Gestionnaire d'événements pour le clic sur une carte
        holder.cardPrescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Afficher l'image en plein écran
                showFullImageDialog(prescription.getImagePath());
            }
        });

        // Gestionnaire d'événements pour le clic long sur une carte
        holder.cardPrescription.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showDeleteConfirmationDialog(position);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return prescriptionList.size();
    }

    private void showFullImageDialog(String imagePath) {
        if (imagePath == null || imagePath.isEmpty()) {
            Toast.makeText(context, "Image non disponible", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_full_image, null);

        ImageView ivFullImage = dialogView.findViewById(R.id.ivFullImage);
        File imgFile = new File(imagePath);
        if (imgFile.exists()) {
            ivFullImage.setImageURI(Uri.fromFile(imgFile));
        }

        builder.setView(dialogView);
        builder.setPositiveButton("Fermer", null);
        builder.show();
    }

    private void showDeleteConfirmationDialog(final int position) {
        Prescription prescription = prescriptionList.get(position);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Confirmation");
        builder.setMessage("Êtes-vous sûr de vouloir supprimer cette ordonnance ?");
        builder.setPositiveButton("Supprimer", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Supprimer de la base de données
                databaseHelper.deletePrescription(prescription.getId());

                // Supprimer le fichier image s'il existe
                if (prescription.getImagePath() != null && !prescription.getImagePath().isEmpty()) {
                    File imgFile = new File(prescription.getImagePath());
                    if (imgFile.exists()) {
                        imgFile.delete();
                    }
                }

                // Supprimer de la liste
                prescriptionList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, prescriptionList.size());

                Toast.makeText(context, "Ordonnance supprimée", Toast.LENGTH_SHORT).show();

                // Si la liste est vide, rafraîchir l'activité pour afficher le message "Aucune ordonnance"
                if (prescriptionList.isEmpty()) {
                    // Cette méthode doit être implémentée dans l'activité qui utilise cet adaptateur
                    // ((PrescriptionsActivity)context).refreshEmptyView();
                }
            }
        });
        builder.setNegativeButton("Annuler", null);
        builder.show();
    }

    public class PrescriptionViewHolder extends RecyclerView.ViewHolder {
        CardView cardPrescription;
        ImageView ivPrescriptionImage;
        TextView tvDate, tvDescription;
        ImageView ivDelete;

        public PrescriptionViewHolder(@NonNull View itemView) {
            super(itemView);
            cardPrescription = itemView.findViewById(R.id.cardPrescription);
            ivPrescriptionImage = itemView.findViewById(R.id.ivPrescriptionImage);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            ivDelete = itemView.findViewById(R.id.ivDelete);
        }
    }
}