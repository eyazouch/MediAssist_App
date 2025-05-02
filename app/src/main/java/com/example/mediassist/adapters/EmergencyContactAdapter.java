package com.example.mediassist.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mediassist.R;
import com.example.mediassist.database.DatabaseHelper;
import com.example.mediassist.models.EmergencyContact;

import java.io.File;
import java.util.List;

public class EmergencyContactAdapter extends RecyclerView.Adapter<EmergencyContactAdapter.ContactViewHolder> {

    private Context context;
    private List<EmergencyContact> contactList;
    private DatabaseHelper databaseHelper;
    private OnContactClickListener listener;

    public interface OnContactClickListener {
        void onCallClick(String phoneNumber);
    }

    public EmergencyContactAdapter(Context context, List<EmergencyContact> contactList, OnContactClickListener listener) {
        this.context = context;
        this.contactList = contactList;
        this.databaseHelper = new DatabaseHelper(context);
        this.listener = listener;
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_emergency_contact, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        EmergencyContact contact = contactList.get(position);

        holder.tvName.setText(contact.getName());
        holder.tvPhone.setText(contact.getPhone());

        // Afficher l'image du contact si disponible
        if (contact.getImagePath() != null && !contact.getImagePath().isEmpty()) {
            File imgFile = new File(contact.getImagePath());
            if (imgFile.exists()) {
                holder.ivContactImage.setImageURI(Uri.fromFile(imgFile));
            }
        }

        // Gestionnaire d'événements pour le bouton d'appel
        holder.btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onCallClick(contact.getPhone());
            }
        });

        // Gestionnaire d'événements pour le clic long sur une carte
        holder.cardContact.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showActionsDialog(position);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    private void showActionsDialog(final int position) {
        EmergencyContact contact = contactList.get(position);

        CharSequence[] actions = {"Modifier", "Supprimer"};

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(contact.getName());
        builder.setItems(actions, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    // Modifier le contact
                    // Ici, vous lanceriez une activité pour modifier le contact
                    Toast.makeText(context, "Fonctionnalité de modification à implémenter", Toast.LENGTH_SHORT).show();
                } else {
                    // Supprimer le contact
                    showDeleteConfirmationDialog(position);
                }
            }
        });
        builder.show();
    }

    private void showDeleteConfirmationDialog(final int position) {
        EmergencyContact contact = contactList.get(position);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Confirmation");
        builder.setMessage("Êtes-vous sûr de vouloir supprimer " + contact.getName() + " ?");
        builder.setPositiveButton("Supprimer", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Supprimer de la base de données
                databaseHelper.deleteEmergencyContact(contact.getId());

                // Supprimer de la liste
                contactList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, contactList.size());

                Toast.makeText(context, "Contact supprimé", Toast.LENGTH_SHORT).show();

                // Si la liste est vide, rafraîchir l'activité pour afficher le message "Aucun contact"
                if (contactList.isEmpty()) {
                    // Cette méthode doit être implémentée dans l'activité qui utilise cet adaptateur
                    // ((EmergencyContactsActivity)context).refreshEmptyView();
                }
            }
        });
        builder.setNegativeButton("Annuler", null);
        builder.show();
    }

    public class ContactViewHolder extends RecyclerView.ViewHolder {
        CardView cardContact;
        ImageView ivContactImage;
        TextView tvName, tvPhone;
        ImageButton btnCall;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            cardContact = itemView.findViewById(R.id.cardContact);
            ivContactImage = itemView.findViewById(R.id.ivContactImage);
            tvName = itemView.findViewById(R.id.tvName);
            tvPhone = itemView.findViewById(R.id.tvPhone);
            btnCall = itemView.findViewById(R.id.btnCall);
        }
    }
}