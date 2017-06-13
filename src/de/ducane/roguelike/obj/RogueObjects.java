package de.ducane.roguelike.obj;

import de.androbin.rpg.obj.*;
import java.util.*;

public final class RogueObjects {
  private static final Map<String, GameObject.Builder> BUILDERS = new HashMap<>();
  
  static {
    BUILDERS.put( "downstairs", Downstairs::new );
    BUILDERS.put( "upstairs", Upstairs::new );
  }
  
  private RogueObjects() {
  }
  
  public static GameObject create( final GameObjectData data ) {
    final GameObject.Builder builder = BUILDERS.get( data.name );
    return builder.build( data );
  }
}