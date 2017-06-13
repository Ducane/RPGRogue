package de.ducane.roguelike.obj;

import de.androbin.rpg.obj.*;
import de.ducane.roguelike.entity.*;
import de.ducane.roguelike.level.*;
import de.ducane.roguelike.screen.*;
import java.awt.*;

public final class Downstairs extends RogueObject {
  public Downstairs( final PlayScreen screen, final GameObjectData data, final Point pos ) {
    super( screen, data, pos );
  }
  
  @ Override
  public void onPlayerEntered( final Level level, final Player player ) {
    level.screen.requestNextFloor();
  }
}