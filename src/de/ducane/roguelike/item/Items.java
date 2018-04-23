package de.ducane.roguelike.item;

import de.androbin.json.*;
import java.util.*;

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
  
  private static Item create( final String name ) {
    final XObject data = JSONUtil.readJSON( "item/" + name + ".json" ).get().asObject();
    return create( name, data );
  }
  
  private static Item create( final String name, final XObject data ) {
    final String type = data.get( "type" ).asString();
    final Item.Builder builder = BUILDERS.get( type );
    return builder.build( name, data );
  }
  
  public static Item get( final String type ) {
    return CACHE.computeIfAbsent( type, Items::create );
  }
}