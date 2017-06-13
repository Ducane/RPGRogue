package de.ducane.roguelike.entity;

import de.ducane.roguelike.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;

public final class MobRenderer extends EntityRenderer {
  public MobRenderer( final Mob mob, final BufferedImage[][] animation ) {
    super( mob, animation );
  }
  
  @ Override
  public void render( final Graphics2D g ) {
    final Blackout blackout = entity.screen.getBlackout();
    final Point2D.Float c = entity.screen.getBlackoutPos();
    
    final Point2D.Float posRaw = entity.getFloatPos();
    
    final float posX = ( posRaw.x + 0.5f ) * scale;
    final float posY = ( posRaw.y + 0.5f ) * scale;
    
    final Point2D.Float pos = new Point2D.Float( posX, posY );
    
    if ( !blackout.contains( c, pos ) ) {
      return;
    }
    
    super.render( g );
  }
}