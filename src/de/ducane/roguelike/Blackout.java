package de.ducane.roguelike;

import java.awt.*;
import java.awt.geom.*;

public abstract class Blackout {
  protected Color color;
  
  protected Blackout( final Color color ) {
    this.color = color;
  }
  
  public abstract boolean contains( Point2D.Float c, Point2D.Float p );
  
  public abstract void darken( Graphics2D g, float x, float y, float w, float h );
  
  public void setColor( final Color color ) {
    this.color = color;
  }
}