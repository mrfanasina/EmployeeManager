package com.fa.employeemanager.api;

public class ApiConfig {

    public static String BASE_URL = "http://10.0.2.2:3000/";

    public static void init(String ip, String port, String read) {
        BASE_URL = "http://" + ip + ":" + port + "/" + read + "/";
    }
}