package de.ducane.roguelike.dark;

import static de.androbin.gfx.util.GraphicsUtil.*;
import java.awt.*;
import java.awt.geom.*;

public final class RectDark implements Dark {
  private final float width;
  private final float height;
  
  public RectDark( final float width, final float height ) {
    this.width = width;
    this.height = height;
  }
  
  @ Override
  public void clip( final Graphics2D g, final float x, final float y ) {
    g.clip( new Rectangle2D.Float( x, y, width, height ) );
  }
  
  @ Override
  public boolean contains( final Point2D.Float c, final Point2D.Float p ) {
    return p.x >= c.x && p.x < c.x + width && p.y >= c.y && p.y < c.y + height;
  }
  
  @ Override
  public void darken( final Graphics2D g, final Color c, final float x, final float y,
      final float w, final float h ) {
    g.setColor( c );
    fillRect( g, 0, 0, w, y );
    fillRect( g, 0, y, x, height );
    fillRect( g, x + width, y, w - x - width, height );
    fillRect( g, 0, y + height, w, h - y - height );
  }
}