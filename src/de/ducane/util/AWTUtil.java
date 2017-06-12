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
  
  public static int trimIndex( final String l, final FontMetrics fm, final int width ) {
    int pw = 0;
    
    for ( int i = 0; i < l.length(); i++ ) {
      pw += fm.charWidth( l.charAt( i ) );
      
      if ( pw > width ) {
        return l.lastIndexOf( ' ', i );
      }
    }
    
    return -1;
  }
  
  public static List<String> wrapLines( final String label, final FontMetrics fm,
      final int width ) {
    final List<String> lines = new LinkedList<>();
    String line = label;
    
    while ( fm.stringWidth( line ) > width ) {
      final int i = trimIndex( line, fm, width );
      lines.add( line.substring( 0, i ) );
      line = line.substring( i + 1 );
    }
    
    lines.add( line );
    return lines;
  }
}