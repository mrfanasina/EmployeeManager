package com.fa.employeemanager.api;

import com.fa.employeemanager.model.Employe;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.*;

public interface ApiService {

    @GET
    Call<List<Employe>> getEmployes(@Url String url);

    @POST
    Call<Employe> addEmploye(@Url String url, @Body Employe employe);

    @PUT
    Call<Employe> updateEmploye(@Url String url, @Body Employe employe);

    @DELETE
    Call<Void> deleteEmploye(@Url String url);
}