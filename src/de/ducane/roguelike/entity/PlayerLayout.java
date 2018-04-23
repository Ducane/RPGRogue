package de.ducane.roguelike.entity;

import java.awt.*;
import de.androbin.rpg.entity.*;
import de.androbin.rpg.gfx.sheet.*;

public final class PlayerLayout implements Sheet.Layout<Entity> {
  @ Override
  public Point locate( final Entity entity ) {
    final int x = (int) ( entity.move.getModProgress() * 3 );
    final int y = entity.orientation.ordinal();
    
    return new Point( x, y );
  }
}