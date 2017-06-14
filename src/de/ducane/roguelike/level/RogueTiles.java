package de.ducane.roguelike.level;

import de.androbin.rpg.tile.*;
import de.ducane.roguelike.dark.*;

public final class RogueTiles {
  private RogueTiles() {
  }
  
  public static RogueTile create( final TileData data, final MovingDark dark ) {
    switch ( data.name ) {
      case "downstairs":
        return new Downstairs( data, dark );
    }
    
    return new RogueTile( data, dark );
  }
}