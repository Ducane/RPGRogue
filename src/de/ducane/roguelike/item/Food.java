package de.ducane.roguelike.item;

import org.json.simple.*;

public final class Food extends Item {
  public final int hp;
  
  public Food( final String name, final JSONObject data ) {
    super( name, data );
    
    this.hp = ( (Number) data.get( "hp" ) ).intValue();
  }
}