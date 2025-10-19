package com.example.coyotefree;
import android.net.Uri;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.TransferListener;

import java.io.IOException;
import java.io.InputStream;

public class CipherDataSource implements DataSource {
    private final InputStream inputStream;
    public CipherDataSource(InputStream inputStream) {
        this.inputStream = inputStream;
    }
    @Override
    public void addTransferListener(TransferListener transferListener) {

    }

    @Override
    public long open(DataSpec dataSpec) throws IOException {
        return C.LENGTH_UNSET;
    }

    @Override
    public int read(byte[] buffer, int offset, int length) throws IOException {
        // Read data into the buffer from the input stream
        return inputStream.read(buffer, offset, length);
    }

    @Override
    public Uri getUri() {
        // Return null since there isn't a meaningful URI for this data source
        return null;
    }

    @Override
    public void close() throws IOException {
        // Close the input stream when finished
        inputStream.close();
    }
}
