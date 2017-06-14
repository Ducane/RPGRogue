package de.ducane.roguelike.dark;

import java.awt.*;
import java.awt.geom.*;
import java.util.function.*;

public final class MovingDark {
  public Dark dark;
  public Supplier<Point2D.Float> pos;
  
  public Color color;
  public int width;
  public int height;
  
  public boolean contains( final Point2D.Float p ) {
    return dark.contains( pos.get(), p );
  }
  
  public void darken( final Graphics2D g, final Point2D.Float p ) {
    final Point2D.Float pos = this.pos.get();
    dark.darken( g, color, pos.x + p.x, pos.y + p.y, width, height );
  }
}