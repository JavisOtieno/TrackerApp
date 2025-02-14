package com.scg.tracker.util;


import android.content.Context;
import android.content.SharedPreferences;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class EncryptedPrefsUtil {

    private static SharedPreferences encryptedSharedPreferences;

    // Initialize EncryptedSharedPreferences
    public static void init(Context context) {
        try {
            // Generate or retrieve the master key
            String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);

            // Create EncryptedSharedPreferences instance
            encryptedSharedPreferences = EncryptedSharedPreferences.create(
                    "my_encrypted_prefs",              // Filename
                    masterKeyAlias,                    // Master key alias
                    context,                           // Context
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,  // Key encryption scheme
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM // Value encryption scheme
            );
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
    }

    // Save data securely
    public static void saveString(String key, String value) {
        if (encryptedSharedPreferences != null) {
            SharedPreferences.Editor editor = encryptedSharedPreferences.edit();
            editor.putString(key, value);
            editor.apply();
        }
    }

    // Retrieve data securely
    public static String getString(String key, String defaultValue) {
        if (encryptedSharedPreferences != null) {
            return encryptedSharedPreferences.getString(key, defaultValue);
        }
        return defaultValue;
    }

    // Remove data securely
    public static void remove(String key) {
        if (encryptedSharedPreferences != null) {
            SharedPreferences.Editor editor = encryptedSharedPreferences.edit();
            editor.remove(key);
            editor.apply();
        }
    }
}
