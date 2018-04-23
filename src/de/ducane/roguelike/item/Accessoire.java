package de.ducane.roguelike.item;

import de.androbin.json.*;

public final class Accessoire extends Item {
  public final int hp;
  
  public Accessoire( final String name, final XObject data ) {
    super( name, data );
    
    this.hp = data.get( "hp" ).asInt();
  }
}