package de.ducane.roguelike.entity;

import static de.androbin.gfx.util.GraphicsUtil.*;
import static de.ducane.util.AWTUtil.*;
import de.androbin.rpg.*;
import de.androbin.rpg.gfx.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;

public class EntityRenderer extends Renderer {
  protected final RogueEntity entity;
  protected final BufferedImage[][] animation;
  
  public EntityRenderer( final RogueEntity entity, final BufferedImage[][] animation ) {
    this.entity = entity;
    this.animation = animation;
  }
  
  @ Override
  public Rectangle2D.Float getBounds() {
    return new Rectangle2D.Float( 0f, 0f, scale, scale );
  }
  
  @ Override
  public void render( final Graphics2D g ) {
    final int i = entity.viewDir.ordinal();
    
    if ( entity.damage.hasCurrent() ) {
      final Direction dir = entity.viewDir;
      
      final float d = -0.25f * scale * (float) Math.sin( entity.damage.getProgress() * Math.PI );
      
      final float dx = d * dir.dx;
      final float dy = d * dir.dy;
      
      drawImage( g, animation[ i ][ 0 ], dx, dy, scale, scale );
    } else {
      final int j = (int) ( entity.move.getProgress() * animation[ i ].length );
      drawImage( g, animation[ i ][ j ], 0f, 0f, scale, scale );
    }
    
    if ( entity.damage.hasCurrent() ) {
      g.setFont( new Font( "Determination Mono", Font.PLAIN, (int) ( scale * 0.6f ) ) );
      
      final int damage = entity.damage.getCurrent().getKey();
      final String damageText = String.valueOf( -damage );
      
      final FontMetrics fm = g.getFontMetrics();
      
      final float x = ( scale - fm.stringWidth( damageText ) ) * 0.5f;
      final float y = ( scale - fm.stringWidth( damageText ) ) * 0.5f;
      
      final float maxY = y + 0.5f * scale;
      
      drawBorderedString( g, damageText, x, y + ( y - maxY )
          * (float) Math.sin( entity.damage.getProgress() * Math.PI ),
          scale * 0.05f, Color.BLACK, Color.WHITE );
    }
  }
}