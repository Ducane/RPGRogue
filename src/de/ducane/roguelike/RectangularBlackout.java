package de.ducane.roguelike;

import static de.androbin.gfx.util.GraphicsUtil.*;
import java.awt.*;
import java.awt.geom.*;

public final class RectangularBlackout extends Blackout {
  private final float width;
  private final float height;
  
  public RectangularBlackout( final Color color, final float width, final float height ) {
    super( color );
    
    this.width = width;
    this.height = height;
  }
  
  @ Override
  public boolean contains( final Point2D.Float c, final Point2D.Float p ) {
    return p.x >= c.x && p.x < c.x + width && p.y >= c.y && p.y < c.y + height;
  }
  
  @ Override
  public void darken( final Graphics2D g, final float x, final float y,
      final float w, final float h ) {
    g.setColor( color );
    
    fillRect( g, 0, 0, w, y );
    fillRect( g, 0, y, x, height );
    fillRect( g, x + width, y, w - x - width, height );
    fillRect( g, 0, y + height, w, h - y - height );
  }
}