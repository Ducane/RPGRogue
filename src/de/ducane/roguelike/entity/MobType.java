package de.ducane.roguelike.entity;

import java.awt.image.*;
import java.util.*;

public final class MobType {
  public static final Map<String, MobType> TYPES = new HashMap<>();
  
  public BufferedImage[][] moveAnimation;
  
  public final Stats initialStats;
  
  public final String name;
  
  public MobType( final String name, final BufferedImage[][] images, final Stats initialStats ) {
    this.name = name;
    this.initialStats = initialStats;
    this.moveAnimation = images;
  }
  
  public static MobType get( final String type ) {
    if ( TYPES.containsKey( type ) ) {
      return TYPES.get( type );
    } else {
      final MobType tile = MobTypeParser.parse( type );
      TYPES.put( type, tile );
      return tile;
    }
  }
}