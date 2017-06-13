package de.ducane.roguelike.item;

import java.util.*;

public final class Items {
  public static final Map<String, Item> ITEMS = new HashMap<>();
  
  private Items() {
  }
  
  public static Item getItem( final String type ) {
    return ITEMS.computeIfAbsent( type, ItemParser::parse );
  }
}