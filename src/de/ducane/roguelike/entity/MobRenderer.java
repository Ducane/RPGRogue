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
    // TODO(Androbin) Clarify units
    return new Rectangle2D.Float( 0f, 0f, scale, scale );
  }
  
  @ Override
  public void render( final Graphics2D g ) {
    final Blackout blackout = mob.screen.getBlackout();
    final Point2D.Float c = mob.screen.getBlackoutPos();
    
    final Point2D.Float posRaw = mob.getFloatPos();
    
    final float posX = ( posRaw.x + 0.5f ) * scale;
    final float posY = ( posRaw.y + 0.5f ) * scale;
    
    final Point2D.Float pos = new Point2D.Float( posX, posY );
    
    if ( !blackout.contains( c, pos ) ) {
      return;
    }
    
    final BufferedImage[][] moveAnimation = mob.type.moveAnimation;
    final int i = mob.getViewDir().ordinal();
    
    if ( mob.attacking ) {
      final Direction dir = mob.getViewDir();
      
      final float d = -0.35f * scale * (float) Math.sin( mob.attackProgress * Math.PI );
      
      final float dx = d * dir.dx;
      final float dy = d * dir.dy;
      
      drawImage( g, moveAnimation[ i ][ 0 ], dx, dy, scale, scale );
    } else {
      final int j = (int) ( mob.getMoveProgress() * moveAnimation[ 0 ].length );
      drawImage( g, moveAnimation[ i ][ j ], 0f, 0f, scale, scale );
    }
    
    if ( mob.damaging ) {
      g.setFont( new Font( "Determination Mono", 0, (int) ( scale * 0.6f ) ) );
      
      final String damage = String.valueOf( -mob.damage );
      
      final FontMetrics fm = g.getFontMetrics();
      
      final float x = ( scale - fm.stringWidth( damage ) ) * 0.5f;
      final float y = ( scale - fm.stringWidth( damage ) ) * 0.5f;
      
      final float maxY = y + 0.5f * scale;
      
      drawBorderedString( g, String.valueOf( damage ), x,
          (int) ( y - Math.sin( mob.damageProgress * Math.PI ) * ( maxY - y ) ),
          scale * 0.05f, Color.BLACK, Color.WHITE );
    }
  }
}