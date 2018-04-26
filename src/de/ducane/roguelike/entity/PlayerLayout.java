package de.ducane.roguelike.entity;

import de.androbin.rpg.dir.*;
import de.androbin.rpg.gfx.sheet.*;
import java.awt.*;

public final class PlayerLayout implements Sheet.Layout<Player> {
  @ Override
  public Point locate( final Player player ) {
    final int x = (int) ( player.move.getModProgress() * 3 );
    final int y = player.orientation.first.ordinal();
    
    return new Point( x, y );
  }
}