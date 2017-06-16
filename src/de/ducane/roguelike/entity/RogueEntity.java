package de.ducane.roguelike.entity;

import de.androbin.rpg.*;
import de.ducane.roguelike.level.*;
import java.awt.*;
import java.util.*;
import java.util.concurrent.*;

public abstract class RogueEntity extends Entity {
  public final RogueEntityData data;
  
  protected final Stats baseStats;
  
  protected boolean attacking;
  protected float attackProgress;
  
  protected boolean damaging;
  protected float damageProgress;
  protected int damage;
  protected Object damageSource;
  
  public RogueEntity( final Level level, final RogueEntityData data, final Point pos ) {
    super( level, pos );
    
    this.data = data;
    
    baseStats = new Stats( data.stats );
    
    moveCallback = () -> ( (Level) world ).onEntityMoved( this );
  }
  
  protected void attack() {
    final Point pos = getPos();
    final Point target = new Point( pos.x + viewDir.dx, pos.y + viewDir.dy );
    final RogueEntity entity = (RogueEntity) world.getEntity( target );
    
    if ( entity == null ) {
      return;
    }
    
    final Random random = ThreadLocalRandom.current();
    
    final Stats stats = getStats();
    final Stats stats2 = entity.getStats();
    
    final int minDamage = Math.max( stats.attack - stats2.defense, 0 );
    final int maxDamage = minDamage + random.nextInt( stats.level() + 1 );
    
    final int damage = random.nextInt( maxDamage - minDamage + 1 ) + minDamage;
    entity.requestDamage( damage, this );
  }
  
  private boolean canAttack( final Direction dir ) {
    final Point pos = getPos();
    
    final int x = pos.x + dir.dx;
    final int y = pos.y + dir.dy;
    
    return world.getEntity( new Point( x, y ) ) != null;
  }
  
  public Stats getStats() {
    return baseStats;
  }
  
  public boolean isDead() {
    return baseStats.hp == 0;
  }
  
  public boolean requestAttack() {
    if ( !canAttack( viewDir ) ) {
      return false;
    }
    
    attacking = true;
    return true;
  }
  
  public boolean requestDamage( final int damage, final Object source ) {
    this.damage += damage;
    damaging = true;
    return baseStats.hp - this.damage <= 0;
  }
  
  private void takeDamage() {
    baseStats.hp = Math.max( baseStats.hp - damage, 0 );
  }
  
  @ Override
  public void update( final float delta ) {
    super.update( delta );
    
    if ( attacking ) {
      attackProgress += delta;
      
      if ( attackProgress >= 1f ) {
        attack();
        
        attacking = false;
        attackProgress = 0f;
      }
    }
    
    if ( damaging ) {
      damageProgress += delta;
      
      if ( damageProgress >= 1f ) {
        takeDamage();
        
        damaging = false;
        damage = 0;
        damageProgress = 0f;
      }
    }
  }
}