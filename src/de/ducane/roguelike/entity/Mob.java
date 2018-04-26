package de.ducane.roguelike.entity;

import de.androbin.rpg.*;
import de.androbin.rpg.dir.*;
import de.androbin.rpg.entity.*;
import de.ducane.roguelike.item.*;
import de.ducane.roguelike.level.*;
import java.awt.*;
import java.awt.geom.*;

public final class Mob extends RogueEntity {
  private Item item;
  
  public Mob( final RogueEntityData data ) {
    super( data, 0 );
    
    move.onHandle = dir -> {
      final Level level = (Level) world;
      final Rectangle extent = dir.first.inner( getBounds() );
      
      LoopUtil.forEach( extent, pos -> {
        if ( item == null ) {
          item = level.takeItem( pos );
        }
      } );
    };
    move.speed = 2f;
  }
  
  public DirectionPair aim( final Entity entity, final boolean moving ) {
    final Point2D.Float pos = getFloatPos();
    final Point2D.Float pos2 = entity.getFloatPos();
    
    final float dx = pos2.x - pos.x;
    final float dy = pos2.y - pos.y;
    
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