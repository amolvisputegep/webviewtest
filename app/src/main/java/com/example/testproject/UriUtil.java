package com.example.testproject;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;

import android.text.TextUtils;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.browser.customtabs.CustomTabsService;
import androidx.core.util.Pair;
import androidx.core.util.Preconditions;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility methods for extracting parameters from Uri objects.
 */
public final class UriUtil {

    private UriUtil() {
        throw new IllegalStateException("This type is not intended to be instantiated");
    }

    public static Uri parseUriIfAvailable(@Nullable String uri) {
        if (uri == null) {
            return null;
        }

        return Uri.parse(uri);
    }

    public static void appendQueryParameterIfNotNull(
            @NonNull Uri.Builder uriBuilder,
            @NonNull String paramName,
            @Nullable Object value) {
        if (value == null) {
            return;
        }

        String valueStr = value.toString();
        if (valueStr == null) {
            return;
        }

        uriBuilder.appendQueryParameter(paramName, value.toString());
    }

    public static Long getLongQueryParameter(@NonNull Uri uri, @NonNull String param) {
        String valueStr = uri.getQueryParameter(param);
        if (valueStr != null) {
            return Long.parseLong(valueStr);
        }
        return null;
    }

    @SuppressLint("RestrictedApi")
    public static List<Bundle> toCustomTabUriBundle(Uri[] uris, int startIndex) {
        Preconditions.checkArgument(startIndex >= 0, "startIndex must be positive");
        if (uris == null || uris.length <= startIndex) {
            return Collections.emptyList();
        }

        List<Bundle> uriBundles = new ArrayList<>(uris.length - startIndex);
        for (int i = startIndex; i < uris.length; i++) {
            if (uris[i] == null) {
//                Logger.warn("Null URI in possibleUris list - ignoring");
                continue;
            }

            Bundle uriBundle = new Bundle();
            uriBundle.putParcelable(CustomTabsService.KEY_URL, uris[i]);
            uriBundles.add(uriBundle);
        }

        return uriBundles;
    }

    public static String formUrlEncode(Map<String, String> parameters) {
        if (parameters == null) {
            return "";
        }

        List<String> queryParts = new ArrayList<>();
        for (Map.Entry<String, String> param : parameters.entrySet()) {
            try {
                queryParts.add(param.getKey() + "=" + URLEncoder.encode(param.getValue(), "utf-8"));
            } catch (UnsupportedEncodingException e) {
                // Should not end up here
//                Logger.error("Could not utf-8 encode.");
            }
        }
        return TextUtils.join("&", queryParts);
    }

    public static List<Pair<String, String>> formUrlDecode(String encoded) {
        if (TextUtils.isEmpty(encoded)) {
            return Collections.emptyList();
        }

        String[] parts = encoded.split("&");
        List<Pair<String, String>> params = new ArrayList<>();

        for (String part : parts) {
            String[] paramAndValue = part.split("=");
            String param = paramAndValue[0];
            String encodedValue = paramAndValue[1];

            try {
                params.add(Pair.create(param, URLDecoder.decode(encodedValue, "utf-8")));
            } catch (UnsupportedEncodingException ex) {
//                Logger.error("Unable to decode parameter, ignoring", ex);
            }
        }

        return params;
    }

    public static Map<String, String> formUrlDecodeUnique(String encoded) {
        List<Pair<String, String>> params = UriUtil.formUrlDecode(encoded);
        Map<String, String> uniqueParams = new HashMap<>();

        for (Pair<String, String> param : params) {
            uniqueParams.put(param.first, param.second);
        }

        return uniqueParams;
    }
}