package de.ducane.roguelike.phantom;

import de.androbin.rpg.entity.*;
import de.ducane.roguelike.*;
import de.ducane.roguelike.entity.*;
import de.ducane.roguelike.item.*;

public final class Upstairs extends RoguePhantom {
  public Upstairs( final EntityData data, final int id ) {
    super( data, id );
  }
  
  @ Override
  public void onPlayerEntered( final RogueMaster master ) {
    final Player player = master.getPlayer();
    final Item amulet = Items.get( "AmuletOfYendor" );
    
    if ( player.inventory.contains( amulet ) ) {
      master.previousFloor();
    }
  }
}