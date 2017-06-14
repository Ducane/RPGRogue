package de.ducane.roguelike.dark;

import java.awt.*;
import java.awt.geom.*;

public interface Dark {
  boolean contains( Point2D.Float c, Point2D.Float p );
  
  void darken( Graphics2D g, Color c, float x, float y, float w, float h );
}