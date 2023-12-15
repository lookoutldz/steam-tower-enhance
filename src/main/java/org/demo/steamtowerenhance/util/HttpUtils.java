package org.demo.steamtowerenhance.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.demo.steamtowerenhance.config.CommonThreadPool;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

@Component
public class HttpUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpUtils.class);

    private final OkHttpClient okHttpClient;
    private final ObjectMapper objectMapper;

    public HttpUtils(OkHttpClient okHttpClient, ObjectMapper objectMapper, CommonThreadPool commonThreadPool) {
        this.okHttpClient = okHttpClient;
        this.objectMapper = objectMapper;
    }

    public<T> T get(@NotNull String url,
                    @Nullable String caption,
                    @NotNull Function<Response, T> successHandler,
                    @Nullable Function<Response, T> failedHandler,
                    @Nullable Consumer<Throwable> errorHandler) {
        final Request request = new Request
                .Builder()
                .url(url)
                .build();
        try (final Response response = okHttpClient.newCall(request).execute()) {
            if (response.isSuccessful()) {
                return successHandler.apply(response);
            } else {
                LOGGER.warn("Response " + response.code() + " for " + (caption != null ? caption : "URL") + "[" + url + "]");
                if (failedHandler != null) {
                    return failedHandler.apply(response);
                }
                return null;
            }
        } catch (Exception e) {
            if (errorHandler != null) {
                errorHandler.accept(e);
            } else {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    public String getAsString(@NotNull String url,
                              @Nullable String caption,
                              @Nullable Function<Response, String> failedHandler,
                              @Nullable Consumer<Throwable> errorHandler) {
        return get(url, caption, response -> {
            try {
                return response.body() == null ? "" : response.body().string();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, failedHandler, errorHandler);
    }

    public<T> T getAsObject(@NotNull String url,
                            @Nullable String caption,
                            @NotNull Class<T> clazz,
                            @Nullable Function<Response, T> failedHandler,
                            @Nullable Consumer<Throwable> errorHandler) {
        return get(url, caption, response -> {
            try {
                final InputStream inputStream = response.body() == null ? null : response.body().byteStream();
                return objectMapper.readValue(inputStream, clazz);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, failedHandler, errorHandler);
    }


    public void getToFile(@NotNull String url, @Nullable String caption, @Nullable String path) throws IOException {
        final String theName = caption == null ? UUID.randomUUID().toString() : caption;
        final String thePath = path == null ? "http_response" : path;
        stringToFile(getAsString(url, caption, null, null), thePath + "/" + theName);
    }

    private void stringToFile(String block, String uri) throws IOException {
        if (block == null) {
            return;
        }
        final File file = new File(uri);
        if (file.exists()) {
            if (!file.delete()) {
                throw new RuntimeException("Could not delete file [" + uri + "]");
            }
        }
        if (!file.createNewFile()) {
            throw new RuntimeException("Could not create file [" + uri + "]");
        }
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(block.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
