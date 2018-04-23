package de.ducane.roguelike.level;

import de.androbin.rpg.tile.*;
import de.ducane.roguelike.item.*;

public class RogueTile extends Tile {
  private Item item;
  
  public RogueTile( final TileData data ) {
    super( data );
  }
  
  public Item getItem() {
    return item;
  }
  
  public void setItem( final Item item ) {
    this.item = item;
  }
}