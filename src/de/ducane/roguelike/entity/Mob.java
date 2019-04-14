package de.ducane.roguelike.entity;

import de.androbin.rpg.dir.*;
import de.androbin.rpg.entity.*;
import de.ducane.roguelike.item.*;
import de.ducane.roguelike.level.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.List;

public final class Mob extends RogueEntity {
  private Item item;
  
  public Mob( final RogueEntityData data, final int id ) {
    super( data, id );
    
    move.onHandle = dir -> {
      final Level level = (Level) getSpot().world;
      final List<Point> inner = getBounds().inner( dir.first );
      
      inner.forEach( pos -> {
        if ( item == null ) {
          item = level.takeItem( pos );
        }
      } );
    };
    move.speed = 2f;
  }
  
  public DirectionPair aim( final Entity entity, final boolean moving ) {
    final Point2D.Float src = getFloatBounds().center();
    final Point2D.Float dst = entity.getFloatBounds().center();
    
    final float dx = dst.x - src.x;
    final float dy = dst.y - src.y;
    
    return Directions.aim( dx, dy );
  }
  
  public Item getItem() {
    return item;
  }
  
  @ Override
  protected void onDamage( final int damage, final Agent source ) {
    orientation = aim( source, false );
    
    if ( !isDead( false ) ) {
      attack.request( true );
    }
  }
  
  public void setItem( final Item item ) {
    this.item = item;
  }
}