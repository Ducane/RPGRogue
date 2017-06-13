package de.ducane.util;

import java.awt.*;
import java.io.*;
import java.net.*;

public final class FontUtil {
  private FontUtil() {
  }
  
  public static boolean installFont( final GraphicsEnvironment ge, final String path ) {
    final URL res = ClassLoader.getSystemResource( "font/" + path );
    
    if ( res == null ) {
      return false;
    }
    
    try ( final InputStream stream = res.openStream() ) {
      ge.registerFont( Font.createFont( Font.TRUETYPE_FONT, stream ) );
      return true;
    } catch ( final FontFormatException | IOException e ) {
      return false;
    }
  }
}