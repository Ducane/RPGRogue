package de.ducane.roguelike.item;

import de.androbin.util.*;
import java.util.*;
import org.json.simple.*;

public final class Items {
  public static final Map<String, Item> CACHE = new HashMap<>();
  
  private Items() {
  }
  
  public static Item create( final String name ) {
    final JSONObject data = (JSONObject) JSONUtil.parseJSON( "item/" + name + ".json" ).get();
    
    final String type = (String) data.get( "type" );
    
    switch ( type ) {
      case "none":
        return new Item( name, data );
      case "accessoire":
        return new Accessoire( name, data );
      case "armor":
        return new Armor( name, data );
      case "food":
        return new Food( name, data );
      case "weapon":
        return new Weapon( name, data );
    }
    
    return null;
  }
  
  public static Item getItem( final String type ) {
    return CACHE.computeIfAbsent( type, Items::create );
  }
}