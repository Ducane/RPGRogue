package de.ducane.roguelike.item;

import de.androbin.util.*;
import java.util.*;
import org.json.simple.*;

public final class Items {
  private static final Map<String, Item> CACHE = new HashMap<>();
  public static final Map<String, Item.Builder> BUILDERS = new HashMap<>();
  
  static {
    BUILDERS.put( "none", Item::new );
    BUILDERS.put( "accessoire", Accessoire::new );
    BUILDERS.put( "armor", Armor::new );
    BUILDERS.put( "food", Food::new );
    BUILDERS.put( "weapon", Weapon::new );
  }
  
  private Items() {
  }
  
  public static Item create( final String name ) {
    final JSONObject data = (JSONObject) JSONUtil.parseJSON( "item/" + name + ".json" )
        .orElseGet( JSONObject::new );
    return create( name, data );
  }
  
  public static Item create( final String name, final JSONObject data ) {
    final String type = (String) data.get( "type" );
    final Item.Builder builder = BUILDERS.get( type );
    return builder.build( name, data );
  }
  
  public static Item getItem( final String type ) {
    return CACHE.computeIfAbsent( type, Items::create );
  }
}