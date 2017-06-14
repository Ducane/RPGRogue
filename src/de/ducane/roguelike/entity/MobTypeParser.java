package de.ducane.roguelike.entity;

import static de.androbin.util.JSONUtil.*;
import de.androbin.gfx.util.*;
import de.androbin.rpg.*;
import de.androbin.util.txt.*;
import java.awt.image.*;
import org.json.simple.*;

public final class MobTypeParser {
  private MobTypeParser() {
  }
  
  public static MobType parse( final String type ) {
    final JSONObject data = (JSONObject) parseJSON( "mob/" + type + ".json" ).get();
    
    final Stats initialStats = new Stats();
    
    initialStats.attack = ( (Number) data.get( "attack" ) ).intValue();
    initialStats.defense = ( (Number) data.get( "defense" ) ).intValue();
    
    initialStats.hp = ( (Number) data.get( "hp" ) ).intValue();
    // initialStats.exp = ( (Number) data.get( "exp" ) ).intValue();
    // TODO set exp in json files
    
    final Direction[] directions = Direction.values();
    final BufferedImage[][] animation = new BufferedImage[ directions.length ][ 2 ];
    
    for ( int i = 0; i < directions.length; i++ ) {
      final String dir = CaseUtil.toProperCase( directions[ i ].name() );
      final BufferedImage image = ImageUtil.loadImage( "mob/" + type + dir + ".png" );
      
      for ( int j = 0; j < animation[ i ].length; j++ ) {
        animation[ i ][ j ] = image.getSubimage( j * 16, 0, 16, 16 );
      }
    }
    
    return new MobType( type, animation, initialStats );
  }
}