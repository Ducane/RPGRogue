package de.ducane.roguelike.level;

import de.androbin.rpg.tile.*;
import de.ducane.roguelike.item.*;

public class RogueTile extends Tile {
  private final TileData data;
  private Item item;
  
  public RogueTile( final TileData data ) {
    this.data = data;
  }
  
  @ Override
  public TileData getData() {
    return data;
  }
  
  public Item getItem() {
    return item;
  }
  
  public void setItem( final Item item ) {
    this.item = item;
  }
}