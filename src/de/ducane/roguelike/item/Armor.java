package de.ducane.roguelike.item;

import org.json.simple.*;

public final class Armor extends Item {
  public final int defense;
  
  public Armor( final String name, final JSONObject data ) {
    super( name, data );
    
    this.defense = ( (Number) data.get( "defense" ) ).intValue();
  }
}