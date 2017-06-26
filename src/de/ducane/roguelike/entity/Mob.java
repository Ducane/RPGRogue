package de.ducane.roguelike.entity;

import de.androbin.rpg.*;
import de.ducane.roguelike.dark.*;
import de.ducane.roguelike.item.*;
import de.ducane.roguelike.level.*;
import java.awt.*;
import java.awt.geom.*;

public final class Mob extends RogueEntity {
  private Item item;
  
  public Mob( final RogueEntityData data, final MovingDark dark ) {
    super( data );
    
    renderer = new MobRenderer( this, data.animation, dark );
    
    move.callback = ( dir, foo ) -> {
      final Level level = (Level) world;
      final Rectangle extent = dir.inner( getBounds() );
      
      LoopUtil.forEach( extent, pos -> {
        if ( item == null ) {
          item = level.takeItem( pos );
        }
      } );
    };
  }
  
  public Direction aim( final Entity entity, final boolean moving ) {
    final Point2D.Float pos = getFloatPos();
    final Point2D.Float pos2 = entity.getFloatPos();
    
    final float dx = pos2.x - pos.x;
    final float dy = pos2.y - pos.y;
    
    return Directions.aim( dx, dy, dir -> !moving || move.canHandle( dir ) );
  }
  
  public Item getItem() {
    return item;
  }
  
  @ Override
  public float moveSpeed() {
    return 2f;
  }
  
  @ Override
  protected void onDamage( final int damage, final Object source ) {
    if ( source instanceof Entity ) {
      final Entity entity = (Entity) source;
      viewDir = aim( entity, false );
      
      if ( !isDead( false ) ) {
        attack.request( true );
      }
    }
  }
  
  public void setItem( final Item item ) {
    this.item = item;
  }
}