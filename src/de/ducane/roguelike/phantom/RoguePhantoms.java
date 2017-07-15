package de.ducane.roguelike.phantom;

import de.androbin.rpg.phantom.*;
import de.ducane.roguelike.dark.*;
import java.awt.*;
import java.util.*;

public final class RoguePhantoms {
  private static final Map<String, RoguePhantom.Builder> BUILDERS = new HashMap<>();
  
  static {
    BUILDERS.put( "upstairs", Upstairs::new );
  }
  
  private RoguePhantoms() {
  }
  
  public static RoguePhantom create( final PhantomData data, final Point pos, final MovingDark dark ) {
    final RoguePhantom.Builder builder = BUILDERS.get( data.name );
    return builder.build( data, pos, dark );
  }
}