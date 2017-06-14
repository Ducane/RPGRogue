package de.ducane.roguelike.obj;

import de.androbin.rpg.obj.*;
import de.ducane.roguelike.dark.*;
import de.ducane.roguelike.entity.*;
import de.ducane.roguelike.level.*;
import java.awt.*;

public final class Downstairs extends RogueObject {
  public Downstairs( final GameObjectData data, final Point pos, final MovingDark dark ) {
    super( data, pos, dark );
  }
  
  @ Override
  public void onPlayerEntered( final Level level, final Player player ) {
    level.screen.requestNextFloor();
  }
}