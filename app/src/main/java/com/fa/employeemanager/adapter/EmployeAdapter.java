package com.fa.employeemanager.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.fa.employeemanager.R;
import com.fa.employeemanager.api.ApiClient;
import com.fa.employeemanager.api.ApiConfig;
import com.fa.employeemanager.api.ApiService;
import com.fa.employeemanager.databinding.ItemEmployeBinding;
import com.fa.employeemanager.model.Employe;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EmployeAdapter extends RecyclerView.Adapter<EmployeAdapter.ViewHolder> {

    private List<Employe> employes = new ArrayList<>();

    public void setEmployes(List<Employe> newList) {
        this.employes = newList;
        notifyDataSetChanged();
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
        Context context = holder.itemView.getContext();

        holder.binding.txtNom.setText(e.getNom());

        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
        symbols.setGroupingSeparator(' ');
        DecimalFormat df = new DecimalFormat("#,##0", symbols);
        holder.binding.txtSalaire.setText(df.format(e.getSalaire()) + " €");

        holder.binding.txtObservation.setText(e.getObservation());

        updateObservationBadge(holder, e.getObservation(), context);

        // DELETE
        holder.binding.btnDelete.setOnClickListener(v -> {
            int currentPos = holder.getBindingAdapterPosition();
            if (currentPos == RecyclerView.NO_POSITION) return;

            Employe emp = employes.get(currentPos);

            new AlertDialog.Builder(context)
                    .setTitle("Confirmation")
                    .setMessage("Supprimer " + emp.getNom() + " "  + emp.getNumemp() + "   ?")
                    .setPositiveButton("Oui", (dialog, which) -> {

                        ApiService api = ApiClient.getClient().create(ApiService.class);

                        String url = ApiConfig.BASE_URL + emp.getNumemp();

                        api.deleteEmploye(url).enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {

                                if (response.isSuccessful()) {
                                    employes.remove(currentPos);
                                    notifyItemRemoved(currentPos);
                                    Toast.makeText(context, "Supprimé", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context, "Erreur suppression" + url, Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                Toast.makeText(context, "Erreur réseau", Toast.LENGTH_SHORT).show();
                            }
                        });

                    })
                    .setNegativeButton("Non", null)
                    .show();
        });

        // UPDATE
        holder.binding.btnEdit.setOnClickListener(v -> {
            int currentPos = holder.getBindingAdapterPosition();
            if (currentPos == RecyclerView.NO_POSITION) return;

            showEditDialog(context, currentPos);
        });
    }

    private void updateObservationBadge(ViewHolder holder, String observation, Context context) {
        int colorRes;

        switch (observation) {
            case "Médiocre":
                colorRes = R.color.emp_mediocre;
                break;
            case "Moyen":
                colorRes = R.color.emp_moyen;
                break;
            case "Grand":
                colorRes = R.color.emp_grand;
                break;
            default:
                colorRes = android.R.color.darker_gray;
                break;
        }

        holder.binding.txtObservation.setTextColor(ContextCompat.getColor(context, colorRes));
    }

    private void showEditDialog(Context context, int position) {
        Employe currentEmploye = employes.get(position);

        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_edit, null);
        EditText editNom = dialogView.findViewById(R.id.editNom);
        EditText editSalaire = dialogView.findViewById(R.id.editSalaire);

        editNom.setText(currentEmploye.getNom());
        editSalaire.setText(String.valueOf(currentEmploye.getSalaire()));

        new AlertDialog.Builder(context)
                .setTitle("Modifier l'employé")
                .setView(dialogView)
                .setPositiveButton("Enregistrer", (dialog, which) -> {

                    try {
                        String newNom = editNom.getText().toString().trim();
                        double newSalaire = Double.parseDouble(editSalaire.getText().toString().trim());

                        if (newNom.isEmpty()) return;

                        currentEmploye.setNom(newNom);
                        currentEmploye.setSalaire(newSalaire);

                        ApiService api = ApiClient.getClient().create(ApiService.class);

                        String url = ApiConfig.BASE_URL + currentEmploye.getNumemp();

                        api.updateEmploye(url, currentEmploye).enqueue(new Callback<Employe>() {
                            @Override
                            public void onResponse(Call<Employe> call, Response<Employe> response) {
                                if (response.isSuccessful()) {
                                    employes.set(position, currentEmploye);
                                    notifyItemChanged(position);
                                    Toast.makeText(context, "Modifié", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context, "Erreur update", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<Employe> call, Throwable t) {
                                Toast.makeText(context, "Erreur réseau", Toast.LENGTH_SHORT).show();
                            }
                        });

                    } catch (NumberFormatException ex) {
                        Toast.makeText(context, "Salaire invalide", Toast.LENGTH_SHORT).show();
                    }

                })
                .setNegativeButton("Annuler", null)
                .show();
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