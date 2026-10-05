package com.blake.gamevault.util;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.MultiModelLoaderFactory;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.StorageReference;
import java.io.InputStream;
import android.net.Uri;
public class FirebaseUrlLoader implements ModelLoader<StorageReference, InputStream> {
    private final ModelLoader<GlideUrl, InputStream> urlLoader;
    public FirebaseUrlLoader(ModelLoader<GlideUrl, InputStream> urlLoader) {
        this.urlLoader = urlLoader;
    }
    @Nullable
    @Override
    public LoadData<InputStream> buildLoadData(@NonNull StorageReference reference, int width, int height, @NonNull Options options) {
        return new LoadData<>(
                new com.bumptech.glide.signature.ObjectKey(reference.getPath()),
                new FirebaseUrlFetcher(reference, urlLoader, width, height, options));
    }
    @Override
    public boolean handles(@NonNull StorageReference reference) {
        return true;
    }
    public static class Factory implements ModelLoaderFactory<StorageReference, InputStream> {
        @NonNull
        @Override
        public ModelLoader<StorageReference, InputStream> build(@NonNull MultiModelLoaderFactory multiFactory) {
            return new FirebaseUrlLoader(multiFactory.build(GlideUrl.class, InputStream.class));
        }
        @Override
        public void teardown() {
        }
    }
    private static class FirebaseUrlFetcher implements DataFetcher<InputStream> {
        private final StorageReference reference;
        private final ModelLoader<GlideUrl, InputStream> urlLoader;
        private final int width;
        private final int height;
        private final Options options;
        private DataFetcher<InputStream> urlFetcher;
        private boolean isCancelled;
        FirebaseUrlFetcher(StorageReference reference, ModelLoader<GlideUrl, InputStream> urlLoader, int width, int height, Options options) {
            this.reference = reference;
            this.urlLoader = urlLoader;
            this.width = width;
            this.height = height;
            this.options = options;
        }
        @Override
        public void loadData(@NonNull Priority priority, @NonNull DataCallback<? super InputStream> callback) {
            try {
                Uri uri = com.google.android.gms.tasks.Tasks.await(reference.getDownloadUrl());
                if (isCancelled) {
                    return;
                }
                GlideUrl glideUrl = new GlideUrl(uri.toString());
                ModelLoader.LoadData<InputStream> loadData = urlLoader.buildLoadData(glideUrl, width, height, options);
                if (loadData != null && loadData.fetcher != null) {
                    urlFetcher = loadData.fetcher;
                    urlFetcher.loadData(priority, callback);
                } else {
                    callback.onLoadFailed(new Exception("Failed to build url fetcher"));
                }
            } catch (Exception e) {
                callback.onLoadFailed(e);
            }
        }
        @Override
        public void cleanup() {
            if (urlFetcher != null) {
                urlFetcher.cleanup();
            }
        }
        @Override
        public void cancel() {
            isCancelled = true;
            if (urlFetcher != null) {
                urlFetcher.cancel();
            }
        }
        @NonNull
        @Override
        public Class<InputStream> getDataClass() {
            return InputStream.class;
        }
        @NonNull
        @Override
        public DataSource getDataSource() {
            return DataSource.REMOTE;
        }
    }
}