package de.ducane.roguelike.item;

import de.androbin.json.*;

public final class Armor extends Item {
  public final int defense;
  
  public Armor( final String name, final XObject data ) {
    super( name, data );
    
    this.defense = data.get( "defense" ).asInt();
  }
}