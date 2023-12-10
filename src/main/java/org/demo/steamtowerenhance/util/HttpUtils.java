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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Component
public class HttpUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpUtils.class);

    private final OkHttpClient okHttpClient;
    private final ObjectMapper objectMapper;
    private final CommonThreadPool commonThreadPool;

    public HttpUtils(OkHttpClient okHttpClient, ObjectMapper objectMapper, CommonThreadPool commonThreadPool) {
        this.okHttpClient = okHttpClient;
        this.objectMapper = objectMapper;
        this.commonThreadPool = commonThreadPool;
    }

    public String getAsString(@NotNull String url, @Nullable String caption) {
        LOGGER.debug("Requesting " + (caption != null ? caption : "URL") + "[" + url + "]");
        final Request request = new Request.Builder().url(url).build();
        try (Response response = okHttpClient.newCall(request).execute()) {
            if (response.code() != 200) {
                LOGGER.warn("HTTP" + response.code() + " - " + url);
                return null;
            }
            return response.body() == null ? null : response.body().string();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public<T> T getAsObject(@NotNull String url, @Nullable String caption, @NotNull Class<T> clazz) {
        LOGGER.debug("Requesting " + (caption != null ? caption : "URL") + "[" + url + "]");
        final Request request = new Request.Builder().url(url).build();
        try (Response response = okHttpClient.newCall(request).execute()) {
            if (response.code() != 200) {
                LOGGER.warn("HTTP" + response.code() + " - " + url);
                return null;
            }
            final InputStream inputStream = response.body() == null ? null : response.body().byteStream();
            return objectMapper.readValue(inputStream, clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public<T> Future<T> getAsObjectAsync(@NotNull String url, @Nullable String caption, @NotNull Class<T> clazz) {
        return commonThreadPool.submit(() -> {
            LOGGER.debug("Requesting(Async) " + (caption != null ? caption : "URL") + "[" + url + "]");
            final Request request = new Request.Builder().url(url).build();
            try (Response response = okHttpClient.newCall(request).execute()) {
                if (response.code() != 200) {
                    LOGGER.debug("HTTP" + response.code() + " - " + url);
                    return null;
                }
                final InputStream inputStream = response.body() == null ? null : response.body().byteStream();
                return objectMapper.readValue(inputStream, clazz);
            } catch (IOException e) {
                LOGGER.error("ERROR for " + caption + "[" + url + "]", e);
                return null;
            }
        });
    }


    public void getToFile(@NotNull String url, @Nullable String caption, @Nullable String path) throws IOException {
        final String theName = caption == null ? UUID.randomUUID().toString() : caption;
        final String thePath = path == null ? "http_response" : path;
        stringToFile(getAsString(url, caption), thePath + "/" + theName);
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
