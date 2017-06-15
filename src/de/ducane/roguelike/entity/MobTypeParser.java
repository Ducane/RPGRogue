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
  
  public static MobType parse( final String name ) {
    final JSONObject data = (JSONObject) parseJSON( "mob/" + name + ".json" ).get();
    
    final Stats stats = new Stats();
    
    stats.attack = ( (Number) data.get( "attack" ) ).intValue();
    stats.defense = ( (Number) data.get( "defense" ) ).intValue();
    stats.exp = ( (Number) data.get( "exp" ) ).intValue();
    stats.hp = ( (Number) data.get( "hp" ) ).intValue();
    
    final Direction[] directions = Direction.values();
    final BufferedImage[][] animation = new BufferedImage[ directions.length ][];
    
    for ( int i = 0; i < directions.length; i++ ) {
      final String dir = CaseUtil.toProperCase( directions[ i ].name() );
      final BufferedImage image = ImageUtil.loadImage( "mob/" + name + dir + ".png" );
      
      animation[ i ] = new BufferedImage[ image.getWidth() / 16 ];
      
      for ( int j = 0; j < animation[ i ].length; j++ ) {
        animation[ i ][ j ] = image.getSubimage( j * 16, 0, 16, 16 );
      }
    }
    
    return new MobType( name, animation, stats );
  }
}