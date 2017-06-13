package de.ducane.roguelike.entity;

import de.androbin.rpg.*;
import de.ducane.roguelike.item.*;
import de.ducane.roguelike.level.*;
import de.ducane.roguelike.screen.*;
import java.awt.*;
import java.awt.geom.*;

public class Mob extends RogueEntity {
  public final MobType type;
  private Item item;
  
  public Mob( final PlayScreen screen, final Level level, final MobType type, final Point pos ) {
    super( screen, level, pos );
    
    this.type = type;
    baseStats.set( type.initialStats );
    
    renderer = new MobRenderer( this );
    
    viewDir = Direction.DOWN;
  }
  
  public void calcDirection( final Entity entity ) {
    final Point2D.Float pos = getFloatPos();
    final Point2D.Float pos2 = entity.getFloatPos();
    
    final float dx = pos2.x - pos.x;
    final float dy = pos2.y - pos.y;
    
    final Direction dirX = dx < 0f ? Direction.LEFT : Direction.RIGHT;
    final Direction dirY = dy < 0f ? Direction.UP : Direction.DOWN;
    
    final boolean a = Math.abs( dx ) > Math.abs( dy );
    moveRequestDir = a && canMove( dirX ) || !a && !canMove( dirY ) ? dirX : dirY;
  }
  
  @ Override
  public void collectItem( final Item item ) {
    this.item = item;
  }
  
  public boolean hasItem() {
    return item != null;
  }
  
  @ Override
  public float moveSpeed() {
    return 2f;
  }
  
  @ Override
  public void update( final float delta, final RPGScreen screen ) {
    super.update( delta, screen );
    
    if ( isAttacking() ) {
      attackProgress += delta;
      
      if ( attackProgress >= 1f ) {
        damaging = true;
        
        if ( this.screen.canAttack( this, viewDir ) ) {
          attack( viewDir );
        }
        
        attacking = false;
        attackProgress = 0f;
      }
    }
    
    if ( damaging ) {
      damageProgress += delta;
      
      if ( damageProgress >= 1f ) {
        damaging = false;
        damageProgress = 0f;
      }
    }
  }
}