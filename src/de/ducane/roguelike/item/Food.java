package de.ducane.roguelike.item;

import de.androbin.json.*;

public final class Food extends Item {
  public final int hp;
  
  public Food( final String name, final XObject data ) {
    super( name, data );
    
    this.hp = data.get( "hp" ).asInt();
  }
}