package de.ducane.roguelike.level;

import static de.androbin.gfx.util.GraphicsUtil.*;
import de.androbin.rpg.tile.*;
import de.ducane.roguelike.dark.*;
import de.ducane.roguelike.item.*;
import java.awt.*;
import java.awt.geom.*;

public class RogueTile extends Tile {
  protected final MovingDark dark;
  
  private Item item;
  
  public RogueTile( final TileData data, final MovingDark dark ) {
    super( data );
    this.dark = dark;
  }
  
  public Item getItem() {
    return item;
  }
  
  public void setItem( final Item item ) {
    this.item = item;
  }
  
  @ Override
  public void render( final Graphics2D g, final Point pos, final float scale ) {
    super.render( g, pos, scale );
    
    final Item item = getItem();
    
    if ( item != null ) {
      final Graphics2D g2 = dark.clip( g );
      final Point2D.Float pos0 = new Point2D.Float( pos.x * scale, pos.y * scale );
      drawImage( g2, item.image, pos0, scale, scale );
    }
  }
}