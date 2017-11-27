package ru.pozitivtelecom.cabinet.map;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import com.google.android.gms.maps.model.Tile;
import com.google.android.gms.maps.model.TileProvider;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

public class OnlineTileProvider implements TileProvider {
    private final String link;

    public OnlineTileProvider(String link) {
        this.link = link;
    }

    public Tile getTile(int x, int y, int z) {
        final String topLeftTileUrl = String.format(Locale.getDefault(), this.link, new Object[]{Integer.valueOf(z + 1), Integer.valueOf(x * 2), Integer.valueOf(y * 2)});
        final String topRightTileUrl = String.format(Locale.getDefault(), this.link, new Object[]{Integer.valueOf(z + 1), Integer.valueOf((x * 2) + 1), Integer.valueOf(y * 2)});
        final String bottomLeftTileUrl = String.format(Locale.getDefault(), this.link, new Object[]{Integer.valueOf(z + 1), Integer.valueOf(x * 2), Integer.valueOf((y * 2) + 1)});
        final String bottomRightTileUrl = String.format(Locale.getDefault(), this.link, new Object[]{Integer.valueOf(z + 1), Integer.valueOf((x * 2) + 1), Integer.valueOf((y * 2) + 1)});
        final Bitmap[] tiles = new Bitmap[4];
        Thread t1 = new Thread() {
            public void run() {
                tiles[0] = OnlineTileProvider.getBitmapFromURL(topLeftTileUrl);
            }
        };
        t1.start();
        Thread t2 = new Thread() {
            public void run() {
                tiles[1] = OnlineTileProvider.getBitmapFromURL(topRightTileUrl);
            }
        };
        t2.start();
        Thread t3 = new Thread() {
            public void run() {
                tiles[2] = OnlineTileProvider.getBitmapFromURL(bottomLeftTileUrl);
            }
        };
        t3.start();
        Thread t4 = new Thread() {
            public void run() {
                tiles[3] = OnlineTileProvider.getBitmapFromURL(bottomRightTileUrl);
            }
        };
        t4.start();
        try {
            t1.join();
            t2.join();
            t3.join();
            t4.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        byte[] tile = mergeBitmaps(tiles, CompressFormat.JPEG);
        if (tile == null) {
            return TileProvider.NO_TILE;
        }
        return new Tile(512, 512, tile);
    }

    private static byte[] mergeBitmaps(Bitmap[] parts, CompressFormat format) {
        boolean allNulls = true;
        for (Bitmap bitmap : parts) {
            if (bitmap != null) {
                allNulls = false;
                break;
            }
        }
        if (allNulls) {
            return null;
        }
        Bitmap tileBitmap = Bitmap.createBitmap(512, 512, Config.ARGB_8888);
        Canvas canvas = new Canvas(tileBitmap);
        Paint paint = new Paint();
        for (int i = 0; i < parts.length; i++) {
            if (parts[i] == null) {
                parts[i] = Bitmap.createBitmap(256, 256, Config.ARGB_8888);
            }
            canvas.drawBitmap(parts[i], (float) (parts[i].getWidth() * (i % 2)), (float) (parts[i].getHeight() * (i / 2)), paint);
        }
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        tileBitmap.compress(format, 100, stream);
        return stream.toByteArray();
    }

    private static Bitmap getBitmapFromURL(String urlString) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(urlString).openConnection();
            connection.setDoInput(true);
            connection.connect();
            return BitmapFactory.decodeStream(connection.getInputStream());
        } catch (IOException e) {
            return null;
        }
    }
}
