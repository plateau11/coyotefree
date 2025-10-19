package com.example.coyotefree;

import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;

import androidx.annotation.RequiresApi;

import java.security.KeyStore;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

@RequiresApi(api = Build.VERSION_CODES.M) // API 23+
public class KeyUpdate {

    private static final String ANDROID_KEYSTORE = "AndroidKeyStore";
    private KeyStore keyStore;

    public KeyUpdate() throws Exception {
        // Load Keystore
        keyStore = KeyStore.getInstance(ANDROID_KEYSTORE);
        keyStore.load(null);
    }

    /**
     * Get existing key by alias, or generate if not present
     */
    public SecretKey getOrCreateKey(String alias) throws Exception {
        SecretKey secretKey = (SecretKey) keyStore.getKey(alias, null);

        if (secretKey == null) {
            // Key does not exist, generate it
            KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE);
            keyGenerator.init(
                    new KeyGenParameterSpec.Builder(
                            alias,
                            KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT
                    )
                            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                            .setKeySize(256)
                            .build()
            );

            secretKey = keyGenerator.generateKey();
        }

        return secretKey;
    }

    /**
     * Force generate a new key with a given alias (replaces old one if exists)
     */
    public SecretKey generateNewKey(String alias) throws Exception {
        if (keyStore.containsAlias(alias)) {
            keyStore.deleteEntry(alias); // delete existing key first
        }
        return getOrCreateKey(alias);
    }
}
