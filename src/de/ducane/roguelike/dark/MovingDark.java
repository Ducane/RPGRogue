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
  
  public boolean contains( final Point2D.Float p0 ) {
    final Point2D.Float pos0 = pos.get();
    final Point2D.Float pos1 = new Point2D.Float( pos0.x * scale, pos0.y * scale );
    final Point2D.Float p1 = new Point2D.Float( p0.x * scale, p0.y * scale );
    return dark.contains( pos1, p1 );
  }
  
  public void darken( final Graphics2D g, final Point2D.Float p ) {
    final Point2D.Float pos0 = pos.get();
    final Point2D.Float pos1 = new Point2D.Float( pos0.x * scale, pos0.y * scale );
    dark.darken( g, color, pos1.x + p.x, pos1.y + p.y, width, height );
  }
}