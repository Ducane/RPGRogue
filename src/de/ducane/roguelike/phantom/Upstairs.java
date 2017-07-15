package de.ducane.roguelike.phantom;

import de.androbin.rpg.phantom.*;
import de.ducane.roguelike.dark.*;
import de.ducane.roguelike.entity.*;
import de.ducane.roguelike.item.*;
import de.ducane.roguelike.screen.*;
import java.awt.*;

public final class Upstairs extends RoguePhantom {
  public Upstairs( final PhantomData data, final Point pos, final MovingDark dark ) {
    super( data, pos, dark );
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