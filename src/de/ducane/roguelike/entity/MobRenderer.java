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
    final Point2D.Float posRaw = entity.getFloatPos();
    
    final float posX = ( posRaw.x + 0.5f ) * scale;
    final float posY = ( posRaw.y + 0.5f ) * scale;
    
    final Point2D.Float pos = new Point2D.Float( posX, posY );
    
    if ( !dark.contains( pos ) ) {
      return;
    }
    
    super.render( g );
  }
}