package de.ducane.roguelike.obj;

import de.androbin.rpg.obj.*;
import de.ducane.roguelike.dark.*;
import java.awt.*;
import java.util.*;

public final class RogueObjects {
  private static final Map<String, RogueObject.Builder> BUILDERS = new HashMap<>();
  
  static {
    BUILDERS.put( "upstairs", Upstairs::new );
  }
  
  private RogueObjects() {
  }
  
  public static GameObject create( final GameObjectData data, final Point pos, final MovingDark dark ) {
    final RogueObject.Builder builder = BUILDERS.get( data.name );
    return builder.build( data, pos, dark );
  }
}