package de.ducane.roguelike.gfx;

import static de.androbin.gfx.util.GraphicsUtil.*;
import de.androbin.rpg.gfx.*;
import de.androbin.rpg.tile.*;
import de.ducane.roguelike.gfx.dark.*;
import de.ducane.roguelike.item.*;
import de.ducane.roguelike.level.*;
import java.awt.*;
import java.awt.geom.*;

public final class RogueTileRenderer extends SimpleTileRenderer<Tile> {
  private final MovingDark dark;
  
  public RogueTileRenderer( final MovingDark dark ) {
    this.dark = dark;
  }
  
  @ Override
  public void render( final Graphics2D g0, final Tile tile,
      final Point2D.Float pos, final float scale ) {
    Graphics2D g = g0;
    
    if ( tile.data.type.equals( Downstairs.TYPE ) ) {
      render( g, Downstairs.MOCK, pos, scale );
      g = dark.clip( g0 );
    }
    
    super.render( g, tile, pos, scale );
    
    final Item item = ( (RogueTile) tile ).getItem();
    
    if ( item != null ) {
      final Graphics2D g2 = dark.clip( g );
      final Point2D.Float pos0 = new Point2D.Float( pos.x * scale, pos.y * scale );
      drawImage( g2, item.image, pos0, scale, scale );
    }
  }
}