package de.ducane.roguelike.entity;

import static de.androbin.gfx.util.GraphicsUtil.*;
import static de.ducane.util.AWTUtil.*;
import de.androbin.rpg.gfx.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;

public final class PlayerRenderer extends Renderer {
  private final Player player;
  
  public PlayerRenderer( final Player player ) {
    this.player = player;
  }
  
  @ Override
  public Rectangle2D.Float getBounds() {
    // TODO(Androbin) Clarify units
    return new Rectangle2D.Float( 0f, 0f, scale, scale );
  }
  
  @ Override
  public void render( final Graphics2D g ) {
    final BufferedImage[][] moveAnimation = player.moveAnimation;
    final int i = player.getViewDir().ordinal();
    final int j = (int) ( player.getMoveProgress() * moveAnimation[ 0 ].length );
    drawImage( g, moveAnimation[ i ][ j ], 0f, 0f, scale, scale );
    
    if ( player.damaging ) {
      g.setFont( new Font( "Determination Mono", Font.PLAIN, (int) ( scale * 0.6f ) ) );
      
      final String damage = String.valueOf( player.damage );
      
      final FontMetrics fm = g.getFontMetrics();
      
      final float x = ( scale - fm.stringWidth( damage ) ) * 0.5f;
      final float y = ( scale - fm.stringWidth( damage ) ) * 0.5f;
      
      final float maxY = y + 0.5f * scale;
      
      drawBorderedString( g, String.valueOf( damage ), x, y + ( y - maxY )
          * (float) Math.sin( player.damageProgress * Math.PI ),
          scale * 0.05f, Color.BLACK, Color.WHITE );
    }
  }
}