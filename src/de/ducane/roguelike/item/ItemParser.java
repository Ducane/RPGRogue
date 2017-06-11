package de.ducane.roguelike.item;

import de.androbin.gfx.util.*;
import de.androbin.util.*;
import java.awt.image.*;
import org.json.simple.*;

public final class ItemParser {
  private ItemParser() {
  }
  
  public static Item parse( final String name ) {
    final JSONObject data = (JSONObject) JSONUtil.parseJSON( "item/" + name + ".json" ).get();
    
    final String type = (String) data.get( "type" );
    
    switch ( type ) {
      case "weapon":
        return parseWeapon( name, data );
      case "accessoire":
        return parseAcessoire( name, data );
      case "armor":
        return parseArmor( name, data );
      case "food":
        return parseFood( name, data );
    }
    
    return null;
  }
  
  private static Weapon parseWeapon( final String name, final JSONObject data ) {
    final String description = (String) data.get( "description" );
    final BufferedImage image = ImageUtil.loadImage( "item/" + name + ".png" );
    
    final int attack = ( (Number) data.get( "attack" ) ).intValue();
    
    return new Weapon( name, description, attack, image );
  }
  
  private static Armor parseArmor( final String name, final JSONObject data ) {
    final String description = (String) data.get( "description" );
    final BufferedImage image = ImageUtil.loadImage( "item/" + name + ".png" );
    
    final int defense = ( (Number) data.get( "defense" ) ).intValue();
    
    return new Armor( name, description, defense, image );
  }
  
  private static Accessoire parseAcessoire( final String name, final JSONObject data ) {
    final String description = (String) data.get( "description" );
    final BufferedImage image = ImageUtil.loadImage( "item/" + name + ".png" );
    
    final int hp = ( (Number) data.get( "hp" ) ).intValue();
    
    return new Accessoire( name, description, hp, image );
  }
  
  private static Food parseFood( final String name, final JSONObject data ) {
    final String description = (String) data.get( "description" );
    final BufferedImage image = ImageUtil.loadImage( "item/" + name + ".png" );
    
    final int hp = ( (Number) data.get( "hp" ) ).intValue();
    
    return new Food( name, description, hp, image );
  }
}