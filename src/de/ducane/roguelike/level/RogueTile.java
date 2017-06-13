package de.ducane.roguelike.level;

import static de.androbin.gfx.util.GraphicsUtil.*;
import de.androbin.rpg.tile.*;
import de.ducane.roguelike.*;
import de.ducane.roguelike.item.*;
import de.ducane.roguelike.screen.*;
import java.awt.*;
import java.awt.geom.*;

public final class RogueTile extends Tile {
  private final PlayScreen screen;
  
  private Item item;
  
  public RogueTile( final PlayScreen screen, final TileData data ) {
    super( data );
    this.screen = screen;
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
    
    final Blackout blackout = screen.getBlackout();
    final Point2D.Float c = screen.getBlackoutPos();
    
    final Point2D.Float center = new Point2D.Float(
        ( pos.x + 0.5f ) * scale, ( pos.y + 0.5f ) * scale );
    
    if ( !blackout.contains( c, center ) ) {
      return;
    }
    
    final Point2D.Float pos0 = new Point2D.Float( pos.x * scale, pos.y * scale );
    
    final Item item = getItem();
    
    if ( item != null ) {
      drawImage( g, item.image, pos0, scale, scale );
    }
  }
}