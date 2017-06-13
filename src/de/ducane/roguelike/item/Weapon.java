package de.ducane.roguelike.item;

import org.json.simple.*;

public final class Weapon extends Item {
  public final int attack;
  
  public Weapon( final String name, final JSONObject data ) {
    super( name, data );
    
    this.attack = ( (Number) data.get( "attack" ) ).intValue();
  }
}