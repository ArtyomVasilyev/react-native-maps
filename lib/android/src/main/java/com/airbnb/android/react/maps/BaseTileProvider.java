package com.airbnb.android.react.maps;

import com.google.android.gms.maps.model.Tile;
import com.google.android.gms.maps.model.TileProvider;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

abstract class BaseTileProvider implements TileProvider {
  private final int x;
  private final int y;

  public BaseTileProvider(int x, int y) {
    this.x = x;
    this.y = y;
  }

  public abstract URL getTileUrl(int x, int y, int zoom);

  public Tile getTile(int x, int y, int zoom) {
    URL url = this.getTileUrl(x, y, zoom);
    if(url == null) {
      return NO_TILE;
    } else {
      Tile tile;
      try {
        tile = new Tile(this.x, this.y, getByteArrayFromInput(url.openStream()));
      } catch (IOException ex) {
        tile = null;
      }

      return tile;
    }
  }

  private static byte[] getByteArrayFromInput(InputStream input) throws IOException {
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    read(input, output);
    return output.toByteArray();
  }

  private static long read(InputStream input, OutputStream output) throws IOException {
    byte[] bytes = new byte[4096];
    long overall = 0L;

    while(true) {
      int length = input.read(bytes);
      if(length == -1) {
        return overall;
      }

      output.write(bytes, 0, length);
      overall += (long)length;
    }
  }
}
