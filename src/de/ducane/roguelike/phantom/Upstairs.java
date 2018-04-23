package de.ducane.roguelike.phantom;

import de.androbin.rpg.entity.*;
import de.ducane.roguelike.entity.*;
import de.ducane.roguelike.item.*;
import de.ducane.roguelike.screen.*;

public final class Upstairs extends RoguePhantom {
  public Upstairs( final EntityData data, final int id ) {
    super( data, id );
  }
  
  @ Override
  public void onPlayerEntered( final PlayScreen screen ) {
    final Player player = screen.getPlayer();
    final Item amulet = Items.get( "AmuletOfYendor" );
    
    if ( player.inventory.contains( amulet ) ) {
      screen.requestPreviousFloor();
    }
  }
}