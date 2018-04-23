package de.ducane.roguelike.event.handler;

import de.androbin.rpg.*;
import de.androbin.rpg.entity.*;
import de.androbin.rpg.event.*;
import de.ducane.roguelike.entity.*;
import de.ducane.roguelike.event.*;
import de.ducane.roguelike.screen.*;

public final class DownstairsEventHandler implements Event.Handler<DownstairsEvent> {
  @ Override
  public void handle( final RPGScreen master, final DownstairsEvent event ) {
    final PlayScreen screen = (PlayScreen) master;
    final Entity entity = event.entity;
    final Player player = screen.getPlayer();
    
    if ( entity == player && !player.isRunning() ) {
      screen.requestNextFloor();
    }
  }
}