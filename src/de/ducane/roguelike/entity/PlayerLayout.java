package de.ducane.roguelike.entity;

import de.androbin.rpg.dir.*;
import de.androbin.rpg.gfx.sheet.*;
import java.awt.*;

public final class PlayerLayout implements Sheet.Layout<Player> {
  @ Override
  public Point locate( final Player player ) {
    final DirectionPair dir = player.orientation;
    
    final int x;
    final int y;
    
    if ( dir.second == null ) {
      x = (int) ( player.move.getProgress() * 6 );
      y = dir.first.ordinal();
    } else {
      final int a = dir.first.ordinal();
      final int b = dir.second.ordinal();
      x = (int) ( player.move.getProgress() * 12 ) + 6;
      y = a == ( b + 1 ) % 4 ? a : b;
    }
    
    return new Point( x, y );
  }
}