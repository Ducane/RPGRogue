package de.ducane.roguelike.event.handler;

import de.androbin.rpg.entity.*;
import de.androbin.rpg.event.*;
import de.androbin.rpg.overlay.*;
import de.ducane.roguelike.*;
import de.ducane.roguelike.entity.*;
import de.ducane.roguelike.event.*;

public final class DownstairsEventHandler implements Event.Handler<RogueMaster, DownstairsEvent> {
  @ Override
  public Overlay handle( final RogueMaster master, final DownstairsEvent event ) {
    final Entity entity = event.entity;
    final Player player = master.getPlayer();
    
    if ( entity == player && !player.isRunning() ) {
      master.nextFloor();
    }
    
    return null;
  }
}