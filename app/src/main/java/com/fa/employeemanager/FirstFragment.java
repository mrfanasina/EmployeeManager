package com.fa.employeemanager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.fa.employeemanager.adapter.EmployeAdapter;
import com.fa.employeemanager.databinding.FragmentFirstBinding;
import com.fa.employeemanager.model.Employe;

import java.util.ArrayList;
import java.util.List;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        List<Employe> employes = new ArrayList<>();

        employes.add(new Employe(1,"Paul",900));
        employes.add(new Employe(2,"Anna",2500));
        employes.add(new Employe(3,"Marc",6000));
        employes.add(new Employe(4,"Sophie",800));
        employes.add(new Employe(5,"Lucas",1200));
        employes.add(new Employe(6,"Emma",3000));
        employes.add(new Employe(7,"Noah",5200));
        employes.add(new Employe(8,"Lina",4800));
        employes.add(new Employe(9,"David",700));
        employes.add(new Employe(10,"Sarah",1500));
        employes.add(new Employe(11,"Thomas",4500));
        employes.add(new Employe(12,"Julie",6200));
        employes.add(new Employe(13,"Kevin",980));
        employes.add(new Employe(14,"Laura",2700));
        employes.add(new Employe(15,"Nina",5100));

        EmployeAdapter adapter = new EmployeAdapter(employes);

        binding.recyclerEmployees.setLayoutManager(
                new LinearLayoutManager(getContext()));

        binding.recyclerEmployees.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
