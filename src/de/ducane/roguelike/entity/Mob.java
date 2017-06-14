package de.ducane.roguelike.entity;

import de.androbin.rpg.*;
import de.ducane.roguelike.item.*;
import de.ducane.roguelike.level.*;
import de.ducane.roguelike.screen.*;
import java.awt.*;
import java.awt.geom.*;

public final class Mob extends RogueEntity {
  public final MobType type;
  private Item item;
  
  public Mob( final PlayScreen screen, final Level level, final MobType type, final Point pos ) {
    super( screen, level, pos );
    
    this.type = type;
    baseStats.set( type.initialStats );
    
    renderer = new MobRenderer( this, type.animation, screen.dark );
    
    viewDir = Direction.DOWN;
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
  public boolean requestDamage( final int damage, final Object source ) {
    final boolean dead = super.requestDamage( damage, source );
    
    if ( source instanceof Entity ) {
      final Entity entity = (Entity) source;
      viewDir = aim( entity, false );
      
      if ( !dead ) {
        requestAttack();
      }
    }
    
    return dead;
  }
  
  public void setItem( final Item item ) {
    this.item = item;
  }
}