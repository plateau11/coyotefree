package com.example.coyotefree;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.PendingPurchasesParams;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.android.billingclient.api.QueryPurchasesParams;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BillingManager {

    private static BillingManager instance;
    private final Context context;
    private final BillingClient billingClient;

    // ProductDetails
    private ProductDetails premiumProductDetails;
    private ProductDetails relaxProductDetails;
    private ProductDetails filterProductDetails;

    // 🔹 SharedPreferences for each product
    private final SharedPreferences prefss;
    private final SharedPreferences prefss2;
    private final SharedPreferences prefss3;

    private static final String PREFS_NAME = "MyAppPrefs";
    private static final String PREFS_NAME2 = "MyAppPrefs2";
    private static final String PREFS_NAME3 = "MyAppPrefs3";

    private static final String KEY_PREMIUM = "isPremiumUser";
    private static final String KEY_PREMIUM2 = "isPremiumUser2";
    private static final String KEY_PREMIUM3 = "isPremiumUser3";

    private static final String PRODUCT_ID = "premium_upgrade";
    private static final String PRODUCT_ID2 = "relax_section";
    private static final String PRODUCT_ID3 = "filter_types";

    private boolean isConnected = false;

    // 🔸 Private constructor
    private BillingManager(Context context) {
        this.context = context.getApplicationContext();

        prefss = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefss2 = context.getSharedPreferences(PREFS_NAME2, Context.MODE_PRIVATE);
        prefss3 = context.getSharedPreferences(PREFS_NAME3, Context.MODE_PRIVATE);

        billingClient = BillingClient.newBuilder(this.context)
                .enablePendingPurchases(
                        PendingPurchasesParams.newBuilder()
                                .enableOneTimeProducts()
                                .build()
                )
                .setListener((billingResult, purchases) -> {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {
                        for (Purchase purchase : purchases) {
                            handlePurchase(purchase);
                        }
                    }
                })
                .build();

        startConnection();
    }

    // Singleton
    public static synchronized BillingManager getInstance(Context context) {
        if (instance == null) {
            instance = new BillingManager(context);
        }
        return instance;
    }

    // Connect to Billing Service
    private void startConnection() {
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    isConnected = true;
                    queryProductDetails();
                    checkIfUserOwnsProducts();
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                isConnected = false;
            }
        });
    }

    // Query all product details
    private void queryProductDetails() {
        List<QueryProductDetailsParams.Product> productList = new ArrayList<>();
        productList.add(QueryProductDetailsParams.Product.newBuilder()
                .setProductId(PRODUCT_ID)
                .setProductType(BillingClient.ProductType.INAPP)
                .build());
        productList.add(QueryProductDetailsParams.Product.newBuilder()
                .setProductId(PRODUCT_ID2)
                .setProductType(BillingClient.ProductType.INAPP)
                .build());
        productList.add(QueryProductDetailsParams.Product.newBuilder()
                .setProductId(PRODUCT_ID3)
                .setProductType(BillingClient.ProductType.INAPP)
                .build());

        QueryProductDetailsParams params = QueryProductDetailsParams.newBuilder()
                .setProductList(productList)
                .build();

        billingClient.queryProductDetailsAsync(params, (billingResult, queryProductDetailsResult) -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                List<ProductDetails> detailsList = queryProductDetailsResult.getProductDetailsList();
                if (detailsList != null) {
                    for (ProductDetails details : detailsList) {
                        switch (details.getProductId()) {
                            case PRODUCT_ID:
                                premiumProductDetails = details;
                                break;
                            case PRODUCT_ID2:
                                relaxProductDetails = details;
                                break;
                            case PRODUCT_ID3:
                                filterProductDetails = details;
                                break;
                        }
                    }
                }
            } else {
                Log.e("BillingManager", "Product query failed: " + billingResult.getDebugMessage());
            }
        });
    }

    // Handle purchases and save flags in correct prefs
    private void handlePurchase(Purchase purchase) {
        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged()) {
                AcknowledgePurchaseParams acknowledgeParams =
                        AcknowledgePurchaseParams.newBuilder()
                                .setPurchaseToken(purchase.getPurchaseToken())
                                .build();

                billingClient.acknowledgePurchase(acknowledgeParams, billingResult -> {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                        for (String id : purchase.getProducts()) {
                            if (id.equals(PRODUCT_ID)) {
                                prefss.edit().putBoolean(KEY_PREMIUM, true).apply();
                                Toast.makeText(context, "✅ Premium Unlocked!", Toast.LENGTH_SHORT).show();
                            } else if (id.equals(PRODUCT_ID2)) {
                                prefss2.edit().putBoolean(KEY_PREMIUM2, true).apply();
                                Toast.makeText(context, "🧘 Relax Section Unlocked!", Toast.LENGTH_SHORT).show();
                            } else if (id.equals(PRODUCT_ID3)) {
                                prefss3.edit().putBoolean(KEY_PREMIUM3, true).apply();
                                Toast.makeText(context, "🔍 Filter Types Unlocked!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        }
    }

    // Check if user already owns any of the 3 products
    private void checkIfUserOwnsProducts() {
        billingClient.queryPurchasesAsync(
                QueryPurchasesParams.newBuilder()
                        .setProductType(BillingClient.ProductType.INAPP)
                        .build(),
                (billingResult, purchaseList) -> {
                    boolean has1 = false, has2 = false, has3 = false;

                    for (Purchase purchase : purchaseList) {
                        for (String id : purchase.getProducts()) {
                            if (id.equals(PRODUCT_ID)) has1 = true;
                            else if (id.equals(PRODUCT_ID2)) has2 = true;
                            else if (id.equals(PRODUCT_ID3)) has3 = true;
                        }
                    }

                    prefss.edit().putBoolean(KEY_PREMIUM, has1).apply();
                    prefss2.edit().putBoolean(KEY_PREMIUM2, has2).apply();
                    prefss3.edit().putBoolean(KEY_PREMIUM3, has3).apply();
                }
        );
    }

    // Launch purchase flow from any Activity
    public void launchPurchaseFlow(Activity activity, String productId) {
        if (!isConnected) {
            Toast.makeText(context, "Billing not ready yet. Try again.", Toast.LENGTH_SHORT).show();
            startConnection();
            return;
        }

        ProductDetails selected = null;
        if (productId.equals(PRODUCT_ID)) {
            selected = premiumProductDetails;
        } else if (productId.equals(PRODUCT_ID2)) {
            selected = relaxProductDetails;
        } else if (productId.equals(PRODUCT_ID3)) {
            selected = filterProductDetails;
        }

        if (selected != null) {
            BillingFlowParams params = BillingFlowParams.newBuilder()
                    .setProductDetailsParamsList(Collections.singletonList(
                            BillingFlowParams.ProductDetailsParams.newBuilder()
                                    .setProductDetails(selected)
                                    .build()
                    ))
                    .build();
            billingClient.launchBillingFlow(activity, params);
        } else {
            Toast.makeText(context, "Purchase unavailable. Try again later.", Toast.LENGTH_SHORT).show();
        }
    }

    // Check product access
    public boolean hasAccess(String key, int productNumber) {
        switch (productNumber) {
            case 1:
                return prefss.getBoolean(key, false);
            case 2:
                return prefss2.getBoolean(key, false);
            case 3:
                return prefss3.getBoolean(key, false);
            default:
                return false;
        }
    }
}
