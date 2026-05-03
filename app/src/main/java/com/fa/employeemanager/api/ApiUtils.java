package com.fa.employeemanager.api;

public class ApiUtils {

    public static String getUrl(String endpoint) {
        return ApiConfig.BASE_URL + endpoint;
    }

    public static String getUrlWithId(String endpoint, int id) {

        if (endpoint.contains("{id}")) {
            return ApiConfig.BASE_URL + endpoint.replace("{id}", String.valueOf(id));
        } else {
            return ApiConfig.BASE_URL + endpoint + "/" + id;
        }
    }
}