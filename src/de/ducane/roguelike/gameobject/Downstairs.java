package de.ducane.roguelike.gameobject;

import de.ducane.roguelike.entity.*;
import de.ducane.roguelike.level.*;

public final class Downstairs extends GameObject {
  public Downstairs() {
    super( "downstairs" );
  }
  
  @ Override
  public void onPlayerEntered( final Level level, final Player player ) {
    level.next();
  }
}