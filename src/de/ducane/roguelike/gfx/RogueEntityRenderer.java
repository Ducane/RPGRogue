package de.ducane.roguelike.gfx;

import static de.ducane.util.AWTUtil.*;
import de.androbin.rpg.dir.*;
import de.androbin.rpg.gfx.*;
import de.ducane.roguelike.entity.*;
import de.ducane.roguelike.entity.RogueEntity.*;
import de.ducane.roguelike.gfx.dark.*;
import java.awt.*;
import java.awt.geom.*;

public final class RogueEntityRenderer extends SimpleEntityRenderer<RogueEntity> {
  private final MovingDark dark;
  
  public RogueEntityRenderer( final MovingDark dark ) {
    this.dark = dark;
  }
  
  @ Override
  public void render( final Graphics2D g0, final RogueEntity entity,
      final Point2D.Float pos, final Rectangle2D.Float view, final float scale ) {
    Graphics2D g = g0;
    
    if ( entity.data.type.toString().startsWith( "rogue/mob" ) ) {
      g = dark.clip( g0 );
    }
    
    final Rectangle2D.Float bounds = getBounds( entity );
    
    bounds.x *= scale;
    bounds.y *= scale;
    
    final DamageHandle damage = entity.damage;
    
    if ( damage.getCurrent() != null ) {
      final DirectionPair dir = entity.orientation;
      
      final float d = -0.25f * (float) Math.sin( damage.getProgress() * Math.PI );
      
      pos.x += d * dir.dx();
      pos.y += d * dir.dy();
    }
    
    super.render( g, entity, pos, view, scale );
    
    if ( damage.getCurrent() != null ) {
      g.setFont( new Font( "Determination Mono", Font.PLAIN, (int) ( scale * 0.6f ) ) );
      
      final int damageValue = damage.getCurrent().getKey();
      final String damageText = String.valueOf( -damageValue );
      
      final FontMetrics fm = g.getFontMetrics();
      
      final float x = bounds.x + ( scale - fm.stringWidth( damageText ) ) * 0.5f;
      final float y = bounds.y + ( scale - fm.stringWidth( damageText ) ) * 0.5f;
      
      drawBorderedString( g, damageText, x, y - 0.5f * scale
          * (float) Math.sin( damage.getProgress() * Math.PI ),
          scale * 0.05f, Color.BLACK, Color.WHITE );
    }
  }
}