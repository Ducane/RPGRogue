package de.ducane.roguelike.obj;

import de.ducane.roguelike.entity.*;
import de.ducane.roguelike.item.*;
import de.ducane.roguelike.level.*;

public final class Upstairs extends GameObject {
  public Upstairs() {
    super( "upstairs" );
  }
  
  @ Override
  public void onPlayerEntered( final Level level, final Player player ) {
    if ( player.getInventory().contains( Items.getItem( "AmuletOfYendor" ) ) ) {
      level.screen.requestPreviousFloor();
    }
  }
}