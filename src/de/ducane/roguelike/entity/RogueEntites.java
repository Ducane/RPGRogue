package de.ducane.roguelike.entity;

import static de.androbin.util.JSONUtil.*;
import java.util.*;
import org.json.simple.*;

public final class RogueEntites {
  public static final Map<String, RogueEntityData> DATA = new HashMap<>();
  
  private RogueEntites() {
  }
  
  private static RogueEntityData createData( final String path ) {
    final JSONObject data = (JSONObject) parseJSON( path + ".json" )
        .orElseGet( JSONObject::new );
    return new RogueEntityData( path, data );
  }
  
  public static RogueEntityData getData( final String path ) {
    return DATA.computeIfAbsent( path, RogueEntites::createData );
  }
}