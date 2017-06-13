package de.ducane.roguelike.obj;

import de.androbin.rpg.obj.*;
import de.ducane.roguelike.entity.*;
import de.ducane.roguelike.item.*;
import de.ducane.roguelike.level.*;
import de.ducane.roguelike.screen.*;
import java.awt.*;

public final class Upstairs extends RogueObject {
  public Upstairs( final PlayScreen screen, final GameObjectData data, final Point pos ) {
    super( screen, data, pos );
  }
  
  @ Override
  public void onPlayerEntered( final Level level, final Player player ) {
    if ( player.getInventory().contains( Items.getItem( "AmuletOfYendor" ) ) ) {
      level.screen.requestPreviousFloor();
    }
  }
}