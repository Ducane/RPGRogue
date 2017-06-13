package de.ducane.roguelike.obj;

import de.androbin.rpg.obj.*;
import de.ducane.roguelike.entity.*;
import de.ducane.roguelike.level.*;

public final class Downstairs extends RogueObject {
  public Downstairs( final GameObjectData data ) {
    super( data );
  }
  
  @ Override
  public void onPlayerEntered( final Level level, final Player player ) {
    level.screen.requestNextFloor();
  }
}