package de.ducane.roguelike.entity;

import de.ducane.roguelike.dark.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;

public final class MobRenderer extends EntityRenderer {
  private final MovingDark dark;
  
  public MobRenderer( final Mob mob, final BufferedImage[][] animation, final MovingDark dark ) {
    super( mob, animation );
    this.dark = dark;
  }
  
  @ Override
  public void render( final Graphics2D g ) {
    final Point2D.Float pos = entity.getFloatPos();
    final Point2D.Float center = new Point2D.Float( pos.x + 0.5f, pos.y + 0.5f );
    
    if ( !dark.contains( center ) ) {
      return;
    }
    
    super.render( g );
  }
}