package de.ducane.roguelike.event;

import de.androbin.rpg.entity.*;
import de.androbin.rpg.event.*;

public final class DownstairsEvent extends Event {
  public static final Event.Builder BUILDER = args -> {
    final Entity entity = (Entity) args[ 0 ];
    return new DownstairsEvent( entity );
  };
  
  public final Entity entity;
  
  public DownstairsEvent( final Entity entity ) {
    this.entity = entity;
  }
  
  @ Override
  public String getMessage() {
    return null;
  }
}