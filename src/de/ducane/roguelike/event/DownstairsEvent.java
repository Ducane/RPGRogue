package de.ducane.roguelike.event;

import de.androbin.rpg.entity.*;
import de.androbin.rpg.event.*;
import java.util.logging.*;

public final class DownstairsEvent implements Event {
  public static final Event.Builder BUILDER = args -> {
    final Entity entity = (Entity) args[ 0 ].raw();
    return new DownstairsEvent( entity );
  };
  
  public final Entity entity;
  
  public DownstairsEvent( final Entity entity ) {
    this.entity = entity;
  }
  
  @ Override
  public void log( final Logger logger ) {
    logger.info( "downstairs" );
  }
}