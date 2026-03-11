package com.fa.employeemanager.adapter;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fa.employeemanager.R;
import com.fa.employeemanager.databinding.ItemEmployeBinding;
import com.fa.employeemanager.model.Employe;

import java.util.List;

public class EmployeAdapter extends RecyclerView.Adapter<EmployeAdapter.ViewHolder> {

    private List<Employe> employes;

    public EmployeAdapter(List<Employe> employes) {
        this.employes = employes;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ItemEmployeBinding binding = ItemEmployeBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );

        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Employe e = employes.get(position);

        holder.binding.txtNom.setText(e.getNom());
        holder.binding.txtSalaire.setText("Salaire : " + e.getSalaire());
        holder.binding.txtObservation.setText("Observation : " + e.getObservation());

        // Couleur selon observation
        switch (e.getObservation()) {

            case "Médiocre":
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    holder.binding.layoutBackground.setBackgroundColor(
                            holder.itemView.getContext().getColor(R.color.emp_mediocre));
                }
                break;

            case "Moyen":
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    holder.binding.layoutBackground.setBackgroundColor(
                            holder.itemView.getContext().getColor(R.color.emp_moyen));
                }
                break;

            case "Grand":
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    holder.binding.layoutBackground.setBackgroundColor(
                            holder.itemView.getContext().getColor(R.color.emp_grand));
                }
                break;
        }


        // Supprimer
        holder.binding.btnDelete.setOnClickListener(v -> {

            employes.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, employes.size());

        });

        // Modifier
        holder.binding.btnEdit.setOnClickListener(v -> {

            View dialogView = LayoutInflater.from(v.getContext())
                    .inflate(com.fa.employeemanager.R.layout.dialog_edit, null);

            EditText editNom = dialogView.findViewById(com.fa.employeemanager.R.id.editNom);
            EditText editSalaire = dialogView.findViewById(com.fa.employeemanager.R.id.editSalaire);

            editNom.setText(e.getNom());
            editSalaire.setText(String.valueOf(e.getSalaire()));

            new AlertDialog.Builder(v.getContext())
                    .setTitle("Modifier employé")
                    .setView(dialogView)
                    .setPositiveButton("Enregistrer", (dialog, which) -> {

                        e.setNom(editNom.getText().toString());
                        e.setSalaire(Double.parseDouble(editSalaire.getText().toString()));

                        notifyItemChanged(position);

                    })
                    .setNegativeButton("Annuler", null)
                    .show();

        });
    }

    @Override
    public int getItemCount() {
        return employes.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ItemEmployeBinding binding;

        public ViewHolder(ItemEmployeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
