package de.ducane.roguelike.item;

import org.json.simple.*;

public final class Accessoire extends Item {
  public final int hp;
  
  public Accessoire( final String name, final JSONObject data ) {
    super( name, data );
    
    this.hp = ( (Number) data.get( "hp" ) ).intValue();
  }
}