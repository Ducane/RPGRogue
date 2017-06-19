package de.ducane.roguelike.entity;

import de.androbin.rpg.*;
import de.ducane.roguelike.dark.*;
import de.ducane.roguelike.item.*;
import de.ducane.roguelike.level.*;
import java.awt.*;
import java.awt.geom.*;

public final class Mob extends RogueEntity {
  private Item item;
  
  public Mob( final Level level, final RogueEntityData data, final Point pos,
      final MovingDark dark ) {
    super( level, data, pos );
    
    renderer = new MobRenderer( this, data.animation, dark );
  }
  
  public Direction aim( final Entity entity, final boolean move ) {
    final Point2D.Float pos = getFloatPos();
    final Point2D.Float pos2 = entity.getFloatPos();
    
    final float dx = pos2.x - pos.x;
    final float dy = pos2.y - pos.y;
    
    return Directions.aim( dx, dy, dir -> !move || canMove( dir ) );
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
      
      if ( !isDead() ) {
        attack.request( true );
      }
    }
  }
  
  public void setItem( final Item item ) {
    this.item = item;
  }
}