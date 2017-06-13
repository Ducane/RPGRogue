package de.ducane.roguelike.obj;

import de.androbin.rpg.obj.*;
import de.ducane.roguelike.entity.*;
import de.ducane.roguelike.level.*;

public abstract class RogueObject extends GameObject {
  protected RogueObject( final GameObjectData data ) {
    super( data );
  }
  
  public abstract void onPlayerEntered( Level level, Player player );
}