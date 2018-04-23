package de.ducane.roguelike.entity;

import de.androbin.json.*;
import de.androbin.rpg.*;
import de.androbin.rpg.entity.*;

public final class RogueEntityData extends EntityData {
  public final Stats stats;
  
  public RogueEntityData( final Ident type, final XObject props ) {
    super( type, props );
    
    stats = new Stats( props );
  }
}