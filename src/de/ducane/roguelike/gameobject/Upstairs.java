package de.ducane.roguelike.gameobject;

import de.ducane.roguelike.entity.*;
// import de.ducane.roguelike.item.*;
import de.ducane.roguelike.level.*;

public class Upstairs extends GameObject {
  public Upstairs() {
    super( "upstairs" );
  }
  
  @ Override
  public void onPlayerEntered( final Level level, final Player player ) {
    // if ( player.getInventory().contains( Item.getItem( "AmuletOfYendor" ) ) )
    // TODO implement upstairs
  }
}