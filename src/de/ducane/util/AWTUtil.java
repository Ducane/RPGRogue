package de.ducane.util;

import java.awt.*;
import java.util.*;
import java.util.List;

public final class AWTUtil {
  private AWTUtil() {
  }
  
  public static void drawBorderedString( final Graphics2D g, final String string,
      final float x, final float y, final float shift, final Color border,
      final Color color ) {
    g.setColor( border );
    g.drawString( string, x - shift, y - shift );
    g.drawString( string, x - shift, y + shift );
    g.drawString( string, x + shift, y - shift );
    g.drawString( string, x + shift, y + shift );
    
    g.setColor( color );
    g.drawString( string, x, y );
  }
  
  private static int trimIndex( final String text, final FontMetrics fm, final int width ) {
    int widthLeft = width;
    
    for ( int i = 0; i < text.length(); i++ ) {
      widthLeft -= fm.charWidth( text.charAt( i ) );
      
      if ( widthLeft <= 0 ) {
        final int index = text.lastIndexOf( ' ', i );
        return index == -1 ? i : index;
      }
    }
    
    return text.length();
  }
  
  public static List<String> wrapLines( final String text, final FontMetrics fm, final int width ) {
    final List<String> lines = new LinkedList<>();
    String textLeft = text;
    
    while ( fm.stringWidth( textLeft ) > width ) {
      final int i = trimIndex( textLeft, fm, width );
      lines.add( textLeft.substring( 0, i ) );
      textLeft = textLeft.substring( i ).trim();
    }
    
    lines.add( textLeft );
    return lines;
  }
}