package de.ducane.roguelike.phantom;

import de.androbin.rpg.entity.*;
import java.util.*;

public final class RoguePhantoms {
  private static final Map<String, RoguePhantom.Builder> BUILDERS = new HashMap<>();
  
  static {
    BUILDERS.put( "upstairs", Upstairs::new );
  }
  
  private RoguePhantoms() {
  }
  
  public static RoguePhantom create( final EntityData data, final int id ) {
    final RoguePhantom.Builder builder = BUILDERS.get( data.name );
    return builder.build( data, id );
  }
}