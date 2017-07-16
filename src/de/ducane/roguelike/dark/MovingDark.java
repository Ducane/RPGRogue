package de.ducane.roguelike.dark;

import java.awt.*;
import java.awt.geom.*;
import java.util.function.*;

public final class MovingDark {
  public Dark dark;
  public Supplier<Point2D.Float> pos;
  
  private final Color color;
  private final float scale;
  
  public int width;
  public int height;
  
  public MovingDark( final Color color, final float scale ) {
    this.color = color;
    this.scale = scale;
  }
  
  private Point2D.Float calcPos() {
    final Point2D.Float pos0 = pos.get();
    return new Point2D.Float( pos0.x * scale, pos0.y * scale );
  }
  
  public Graphics2D clip( final Graphics2D g ) {
    final Graphics2D g2 = (Graphics2D) g.create();
    final Point2D.Float pos = calcPos();
    dark.clip( g2, pos.x, pos.y );
    return g2;
  }
  
  public boolean contains( final Point2D.Float p0 ) {
    final Point2D.Float pos = calcPos();
    final Point2D.Float p1 = new Point2D.Float( p0.x * scale, p0.y * scale );
    return dark.contains( pos, p1 );
  }
  
  public void darken( final Graphics2D g, final Point2D.Float p ) {
    final Point2D.Float pos = calcPos();
    dark.darken( g, color, pos.x + p.x, pos.y + p.y, width, height );
  }
}