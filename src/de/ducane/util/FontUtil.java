package de.ducane.util;

import java.awt.*;
import java.io.*;
import java.net.*;

public final class FontUtil {
  private FontUtil() {
  }
  
  public static void installFont( final GraphicsEnvironment ge, final String path ) {
    final URL res = ClassLoader.getSystemResource( "fonts/" + path );
    
    if ( res == null ) {
      return;
    }
    
    try ( final InputStream stream = res.openStream() ) {
      ge.registerFont( Font.createFont( Font.TRUETYPE_FONT, stream ) );
    } catch ( final FontFormatException | IOException ignore ) {
    }
  }
}