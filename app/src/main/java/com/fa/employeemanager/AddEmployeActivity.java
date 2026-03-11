package com.fa.employeemanager;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.material.appbar.MaterialToolbar;
import androidx.appcompat.app.AppCompatActivity;

public class AddEmployeActivity extends AppCompatActivity {

    private EditText editNom;
    private EditText editSalaire;
    private Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_employe);
        MaterialToolbar toolbar = findViewById(R.id.topAppBar);

        toolbar.setNavigationOnClickListener(v -> {
            finish();
        });
        editNom = findViewById(R.id.editNom);
        editSalaire = findViewById(R.id.editSalaire);
        btnSave = findViewById(R.id.btnSave);

        btnSave.setOnClickListener(v -> {

            String nom = editNom.getText().toString();
            String salaire = editSalaire.getText().toString();

            if(nom.isEmpty() || salaire.isEmpty()){
                Toast.makeText(this,"Remplir tous les champs",Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(this,"Employé ajouté (simulation)",Toast.LENGTH_SHORT).show();

            finish();
        });
    }
}