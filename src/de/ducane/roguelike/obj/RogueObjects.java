package de.ducane.roguelike.obj;

import de.androbin.rpg.obj.*;
import de.ducane.roguelike.screen.*;
import java.awt.*;
import java.util.*;

public final class RogueObjects {
  private static final Map<String, RogueObject.Builder> BUILDERS = new HashMap<>();
  
  static {
    BUILDERS.put( "downstairs", Downstairs::new );
    BUILDERS.put( "upstairs", Upstairs::new );
  }
  
  private RogueObjects() {
  }
  
  public static GameObject create( final PlayScreen screen, final GameObjectData data, final Point pos ) {
    final RogueObject.Builder builder = BUILDERS.get( data.name );
    return builder.build( screen, data, pos );
  }
}