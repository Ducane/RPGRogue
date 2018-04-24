package de.ducane.roguelike.entity;

import de.androbin.rpg.gfx.sheet.*;
import java.awt.*;

public final class MobLayout implements Sheet.Layout<Mob> {
  @ Override
  public Point locate( final Mob mob ) {
    final int x = (int) ( mob.move.getModProgress() * 2 );
    final int y = mob.orientation.ordinal();
    
    return new Point( x, y );
  }
}