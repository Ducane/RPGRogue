package de.ducane.roguelike.gfx;

import static de.ducane.util.AWTUtil.*;
import de.androbin.rpg.*;
import de.androbin.rpg.dir.*;
import de.androbin.rpg.entity.*;
import de.androbin.rpg.gfx.*;
import de.ducane.roguelike.entity.*;
import de.ducane.roguelike.entity.RogueEntity.*;
import de.ducane.roguelike.gfx.dark.*;
import java.awt.*;
import java.awt.geom.*;

public class RogueEntityRenderer extends SimpleEntityRenderer<Entity> {
  private final MovingDark dark;
  
  public RogueEntityRenderer( final MovingDark dark ) {
    this.dark = dark;
  }
  
  @ Override
  public void render( final Graphics2D g0, final Entity entity,
      final Point2D.Float pos, final float scale ) {
    final Ident type = entity.data.type;
    
    if ( type.firstElement().equals( "phantom" ) ) {
      super.render( g0, entity, pos, scale );
      return;
    }
    
    Graphics2D g = g0;
    
    if ( entity.data.type.firstElement().equals( "mob" ) ) {
      g = dark.clip( g0 );
    }
    
    final Rectangle2D.Float bounds = getBounds( entity );
    
    bounds.x *= scale;
    bounds.y *= scale;
    
    final DamageHandle damage = ( (RogueEntity) entity ).damage;
    
    if ( damage.hasCurrent() ) {
      final Direction dir = entity.orientation;
      
      final float d = -0.25f * (float) Math.sin( damage.getModProgress() * Math.PI );
      
      pos.x += d * dir.dx;
      pos.y += d * dir.dy;
    }
    
    super.render( g, entity, pos, scale );
    
    if ( damage.hasCurrent() ) {
      g.setFont( new Font( "Determination Mono", Font.PLAIN, (int) ( scale * 0.6f ) ) );
      
      final int damageValue = damage.getCurrent().getKey();
      final String damageText = String.valueOf( -damageValue );
      
      final FontMetrics fm = g.getFontMetrics();
      
      final float x = bounds.x + ( scale - fm.stringWidth( damageText ) ) * 0.5f;
      final float y = bounds.y + ( scale - fm.stringWidth( damageText ) ) * 0.5f;
      
      drawBorderedString( g, damageText, x, y - 0.5f * scale
          * (float) Math.sin( damage.getModProgress() * Math.PI ),
          scale * 0.05f, Color.BLACK, Color.WHITE );
    }
  }
}