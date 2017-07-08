package de.ducane.roguelike.entity;

import de.androbin.gfx.util.*;
import de.androbin.rpg.*;
import de.androbin.util.txt.*;
import java.awt.image.*;
import org.json.simple.*;

public final class RogueEntityData {
  public final String name;
  public final Stats stats;
  public final BufferedImage[][] animation;
  
  public RogueEntityData( final String path, final JSONObject data ) {
    name = path.split( "/" )[ 1 ];
    stats = new Stats( data );
    animation = createSheet( path );
  }
  
  private static BufferedImage[][] createSheet( final String path ) {
    final Direction[] directions = Direction.values();
    final BufferedImage[][] animation = new BufferedImage[ directions.length ][];
    
    for ( int i = 0; i < animation.length; i++ ) {
      final String dir = CaseUtil.toProperCase( directions[ i ].name() );
      final BufferedImage image = ImageUtil.loadImage( path + dir + ".png" );
      
      final int ratio = Math.round( (float) image.getWidth() / image.getHeight() );
      
      final int width = image.getWidth() / ratio;
      final int height = image.getHeight();
      
      animation[ i ] = new BufferedImage[ ratio ];
      
      for ( int j = 0; j < animation[ i ].length; j++ ) {
        animation[ i ][ j ] = image.getSubimage( j * width, 0, width, height );
      }
    }
    
    return animation;
  }
}