package de.ducane.roguelike.entity;

import de.androbin.json.*;
import de.androbin.rpg.*;
import java.util.*;

public final class RogueEntites {
  public static final Map<Ident, RogueEntityData> DATA = new HashMap<>();
  
  private RogueEntites() {
  }
  
  private static RogueEntityData createData( final Ident type ) {
    final XObject data = JSONUtil.readJSONObject( "entity/" + type + ".json" );
    return new RogueEntityData( type, data );
  }
  
  public static RogueEntityData getData( final Ident type ) {
    return DATA.computeIfAbsent( type, RogueEntites::createData );
  }
}