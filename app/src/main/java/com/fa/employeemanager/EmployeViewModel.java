package com.fa.employeemanager;

import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.fa.employeemanager.api.ApiClient;
import com.fa.employeemanager.api.ApiConfig;
import com.fa.employeemanager.api.ApiService;
import com.fa.employeemanager.model.Employe;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EmployeViewModel extends ViewModel {

    private static boolean demoMode = false;
    private static final MutableLiveData<UiState<List<Employe>>> employes = new MutableLiveData<>();

    public LiveData<UiState<List<Employe>>> getEmployes() {
        return employes;
    }
    public void setDemoMode(boolean demo) {
        this.demoMode = demo;
        loadData();
    }


    public static void loadData() {
        employes.setValue(UiState.loading());

        if (demoMode) {
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                List<Employe> list = new ArrayList<>();
                list.add(new Employe(1, "Paul", 900));
                list.add(new Employe(2, "Anna", 2500));
                list.add(new Employe(3, "Marc", 6000));
                list.add(new Employe(4, "Sophie", 800));
                list.add(new Employe(5, "Lucas", 1200));
                list.add(new Employe(6, "Emma", 3000));
                list.add(new Employe(7, "Noah", 5200));
                list.add(new Employe(8, "Lina", 4800));
                list.add(new Employe(9, "David", 700));
                list.add(new Employe(10, "Sarah", 1500));
                list.add(new Employe(11, "Thomas", 4500));
                list.add(new Employe(12, "Julie", 6200));
                list.add(new Employe(13, "Kevin", 980));
                list.add(new Employe(14, "Laura", 2700));
                list.add(new Employe(15, "Nina", 5100));

                if (list.isEmpty()) {
                    employes.setValue(UiState.empty());
                } else {
                    employes.setValue(UiState.success(list));
                }

            }, 1500);
        } else {
            ApiService apiService = ApiClient.getClient().create(ApiService.class);
            Call<List<Employe>> call = apiService.getEmployes(ApiConfig.BASE_URL);


            call.enqueue(new Callback<List<Employe>>() {
                @Override
                public void onResponse(Call<List<Employe>> call, Response<List<Employe>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<Employe> list = response.body();

                        if (list.isEmpty()) {
                            employes.setValue(UiState.empty());
                        } else {
                            employes.setValue(UiState.success(list));
                        }
                    } else {
                        employes.setValue(UiState.error("Erreur serveur: " + response.code()));
                    }
                }

                @Override
                public void onFailure(Call<List<Employe>> call, Throwable t) {
                    employes.setValue(UiState.error("Échec: " + t.getMessage()));
                }
            });
        }
    }

    public void reload() {
        loadData();
    }
}