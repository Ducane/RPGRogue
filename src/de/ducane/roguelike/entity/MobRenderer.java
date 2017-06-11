package de.ducane.roguelike.entity;

import static de.androbin.gfx.util.GraphicsUtil.*;
import static de.ducane.util.AWTUtil.*;
import de.androbin.rpg.*;
import de.androbin.rpg.gfx.*;
import de.ducane.roguelike.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;

public final class MobRenderer extends Renderer {
  private final Mob mob;
  
  public MobRenderer( final Mob mob ) {
    this.mob = mob;
  }
  
  @ Override
  public Rectangle2D.Float getBounds() {
    return new Rectangle2D.Float( 0f, 0f, 1f, 1f );
  }
  
  @ Override
  public void render( final Graphics2D g ) {
    final Rectangle2D.Float bounds = getBounds();
    
    final Blackout blackout = mob.screen.getBlackout();
    final Point2D.Float c = mob.screen.getBlackoutPos();
    
    final Point2D.Float posRaw = mob.getFloatPos();
    
    final float posX = ( posRaw.x + 0.5f ) * bounds.width;
    final float posY = ( posRaw.y + 0.5f ) * bounds.height;
    
    final Point2D.Float pos = new Point2D.Float( posX, posY );
    
    if ( !blackout.contains( c, pos ) ) {
      return;
    }
    
    final BufferedImage[][] moveAnimation = mob.type.moveAnimation;
    final int i = mob.getViewDir().ordinal();
    
    if ( mob.attacking ) {
      final Direction dir = mob.getViewDir();
      
      final float d = (float) Math.sin( mob.attackProgress * Math.PI ) * 0.35f;
      
      final float dx = d * dir.dx * bounds.x;
      final float dy = d * dir.dy * bounds.y;
      
      g.translate( dx, dy );
      drawImage( g, moveAnimation[ i ][ 0 ], bounds );
      g.translate( -dx, -dy );
    } else {
      final int j = (int) ( mob.getMoveProgress() * moveAnimation[ 0 ].length );
      drawImage( g, moveAnimation[ i ][ j ], bounds );
    }
    
    if ( mob.damaging ) {
      g.setFont( new Font( "Determination Mono", 0, (int) ( bounds.height * 0.6f ) ) );
      
      final String damage = String.valueOf( mob.damage );
      
      final FontMetrics fm = g.getFontMetrics();
      
      final float x = bounds.x + ( bounds.width - fm.stringWidth( damage ) ) * 0.5f;
      final float y = bounds.y + ( bounds.height - fm.stringWidth( damage ) ) * 0.5f;
      
      final float maxY = y + 0.5f * bounds.height;
      
      drawBorderedString( g, String.valueOf( damage ), x,
          (int) ( y - Math.sin( mob.damageProgress * Math.PI ) * ( maxY - y ) ),
          bounds.height * 0.05f, Color.BLACK, Color.WHITE );
    }
  }
}