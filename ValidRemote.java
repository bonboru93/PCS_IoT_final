package com.example.bonboru93.pcs_sms_home;

import java.util.ArrayList;
import java.util.Set;

/**
 * Created by 506R05922142$ on 2016/12/13.
 */

public class ValidRemote {

    private static ArrayList<String> validRemote = new ArrayList<>();

    public static void addValidRemote(String newUser)
    {
        validRemote.add(newUser);
    }

    public static boolean isValidRemote(String incomeUser)
    {
        return (validRemote.contains(incomeUser));
    }
}
