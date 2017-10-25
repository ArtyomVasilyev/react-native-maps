package com.airbnb.android.react.maps;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import android.graphics.Paint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

import java.net.MalformedURLException;
import java.net.URL;

import com.google.android.gms.maps.model.Tile;


public class TranslucentUrlTileProvider extends BaseTileProvider {
    private String urlTemplate;
    private Paint opacityPaint = new Paint();

    public TranslucentUrlTileProvider(int width, int height, String urlTemplate, float opacity) {
        super(width, height);
        this.urlTemplate = urlTemplate;
        this.setOpacity(opacity);
    }

    /**
     * Sets the desired opacity of map {@link Tile}s, as a percentage where 0 is invisible and 1 is completely opaque.
     * @param opacity The desired opacity of map {@link Tile}s (as float between 0 and 1, inclusive)
     */
    public void setOpacity(float opacity) {
        int alpha = Math.round(opacity * 255);
        opacityPaint.setAlpha(alpha);
    }

    public void setUrlTemplate(String urlTemplate) {
        this.urlTemplate = urlTemplate;
    }

    @Override
    public Tile getTile(int x, int y, int zoom) {
        URL tileUrl = getTileUrl(x, y, zoom);

        Tile tile = null;
        ByteArrayOutputStream stream = null;

        try {
            Bitmap image = BitmapFactory.decodeStream(tileUrl.openConnection().getInputStream());
            image = adjustOpacity(image);

            stream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.PNG, 100, stream);

            byte[] byteArray = stream.toByteArray();

            tile = new Tile(256, 256, byteArray);
        } catch(IOException e) {
            e.printStackTrace();
        } finally {
            if(stream != null) {
                try {
                    stream.close();
                } catch(IOException e) {}
            }
        }

        return tile;
    }

    @Override
    public synchronized URL getTileUrl(int x, int y, int zoom) {

      String s = this.urlTemplate
          .replace("{x}", Integer.toString(x))
          .replace("{y}", Integer.toString(y))
          .replace("{z}", Integer.toString(zoom));
      URL url;
      try {
        url = new URL(s);
      } catch (MalformedURLException e) {
        throw new AssertionError(e);
      }
      return url;
    }

    /**
     * Helper method that adjusts the given {@link Bitmap}'s opacity to the opacity
     *
     * @param bitmap The {@link Bitmap} whose opacity to adjust
     * @return A new {@link Bitmap} with an adjusted opacity
     *
     */
    private Bitmap adjustOpacity(Bitmap bitmap) {
        Bitmap adjustedBitmap = Bitmap.createBitmap(256, 256, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(adjustedBitmap);
        canvas.drawBitmap(bitmap, 0, 0, opacityPaint);

        return adjustedBitmap;
    }
}