package de.ducane.roguelike.entity;

import static de.androbin.gfx.util.GraphicsUtil.*;
import static de.ducane.util.AWTUtil.*;
import de.androbin.rpg.*;
import de.androbin.rpg.gfx.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;

public class RogueEntityRenderer extends EntityRenderer {
  public RogueEntityRenderer( final RogueEntity entity, final BufferedImage[][] animation ) {
    super( entity, animation );
  }
  
  @ Override
  public void render( final Graphics2D g ) {
    final Rectangle2D.Float bounds = getBounds();
    
    bounds.x *= scale;
    bounds.y *= scale;
    bounds.width *= scale;
    bounds.height *= scale;
    
    final RogueEntity entity = (RogueEntity) this.entity;
    
    final int i = entity.viewDir.ordinal();
    
    if ( entity.damage.hasCurrent() ) {
      final Direction dir = entity.viewDir;
      
      final float d = -0.25f * scale * (float) Math.sin( entity.damage.getProgress() * Math.PI );
      
      bounds.x += d * dir.dx;
      bounds.y += d * dir.dy;
      
      drawImage( g, animation[ i ][ 0 ], bounds );
    } else {
      final int j = (int) ( entity.move.getProgress() * animation[ i ].length );
      drawImage( g, animation[ i ][ j ], bounds );
    }
    
    if ( entity.damage.hasCurrent() ) {
      g.setFont( new Font( "Determination Mono", Font.PLAIN, (int) ( scale * 0.6f ) ) );
      
      final int damage = entity.damage.getCurrent().getKey();
      final String damageText = String.valueOf( -damage );
      
      final FontMetrics fm = g.getFontMetrics();
      
      final float x = bounds.x + ( scale - fm.stringWidth( damageText ) ) * 0.5f;
      final float y = bounds.y + ( scale - fm.stringWidth( damageText ) ) * 0.5f;
      
      drawBorderedString( g, damageText, x, y - 0.5f * scale
          * (float) Math.sin( entity.damage.getProgress() * Math.PI ),
          scale * 0.05f, Color.BLACK, Color.WHITE );
    }
  }
}