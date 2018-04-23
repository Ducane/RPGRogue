package de.ducane.roguelike.item;

import de.androbin.json.*;

public final class Weapon extends Item {
  public final int attack;
  
  public Weapon( final String name, final XObject data ) {
    super( name, data );
    
    this.attack = data.get( "attack" ).asInt();
  }
}