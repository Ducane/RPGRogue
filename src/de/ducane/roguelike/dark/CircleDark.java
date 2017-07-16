package de.ducane.roguelike.dark;

import static de.androbin.gfx.util.GraphicsUtil.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;

public final class CircleDark implements Dark {
  private final float r;
  private Color color;
  private BufferedImage mask;
  
  public CircleDark( final float radius ) {
    this.r = radius;
  }
  
  @ Override
  public void clip( final Graphics2D g, final float x, final float y ) {
    g.clip( new Ellipse2D.Float( x - r, y - r, 2 * r, 2 * r ) );
  }
  
  @ Override
  public boolean contains( final Point2D.Float c, final Point2D.Float p ) {
    final float dx = p.x - c.x;
    final float dy = p.y - c.y;
    return dx * dx + dy * dy <= r * r;
  }
  
  @ Override
  public void darken( final Graphics2D g, final Color c, final float x, final float y,
      final float w, final float h ) {
    if ( mask == null || color != c ) {
      mask = prepareMask( c, r );
      color = c;
    }
    
    g.setColor( c );
    fillRect( g, 0, 0, w, y - r );
    fillRect( g, 0, y - r, x - r, r * 2 );
    drawImage( g, mask, x - r, y - r );
    fillRect( g, x + r, y - r, w - x - r, r * 2 );
    fillRect( g, 0, y + r, w, h - y - r );
  }
  
  private static BufferedImage prepareMask( final Color color, final float r ) {
    final int res = (int) ( 2 * r );
    final BufferedImage mask = new BufferedImage( res, res, BufferedImage.TYPE_INT_ARGB );
    
    final int rgba = color.getRGB();
    
    for ( int y = 0; y < mask.getHeight(); y++ ) {
      for ( int x = 0; x < mask.getHeight(); x++ ) {
        if ( Math.pow( x - r, 2 ) + Math.pow( y - r, 2 ) > r * r ) {
          mask.setRGB( x, y, rgba );
        }
      }
    }
    
    return mask;
  }
}