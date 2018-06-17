package de.ducane.util;

import de.androbin.io.*;
import java.awt.*;
import java.io.*;
import java.nio.file.*;

public final class FontUtil {
  private FontUtil() {
  }
  
  public static boolean installFont( final GraphicsEnvironment ge, final String path ) {
    final Path file = DynamicClassLoader.getPath( "font/" + path );
    
    if ( file == null ) {
      return false;
    }
    
    try ( final InputStream stream = Files.newInputStream( file ) ) {
      ge.registerFont( Font.createFont( Font.TRUETYPE_FONT, stream ) );
    } catch ( final FontFormatException | IOException e ) {
      return false;
    }
    
    return true;
  }
}