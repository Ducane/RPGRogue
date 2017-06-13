package de.ducane.roguelike.entity;

import de.androbin.rpg.*;
import de.ducane.roguelike.level.*;
import de.ducane.roguelike.screen.*;
import java.awt.*;
import java.util.*;
import java.util.concurrent.*;

public abstract class RogueEntity extends Entity {
  protected final PlayScreen screen;
  
  protected final Stats baseStats;
  
  protected boolean attacking;
  protected float attackProgress;
  
  protected boolean damaging;
  protected float damageProgress;
  protected int damage;
  protected Object damageSource;
  
  public RogueEntity( final PlayScreen screen, final Level level, final Point pos ) {
    super( level, pos );
    
    this.screen = screen;
    
    this.baseStats = new Stats();
    
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
    final int maxDamage = minDamage + random.nextInt( stats.stage + 1 );
    
    final int damage = random.nextInt( maxDamage - minDamage + 1 ) + minDamage;
    entity.requestDamage( damage, this );
  }
  
  public Stats getStats() {
    return baseStats;
  }
  
  public boolean isDead() {
    return baseStats.hp == 0;
  }
  
  public boolean requestAttack() {
    if ( !screen.canAttack( this, viewDir ) ) {
      return false;
    }
    
    attacking = true;
    return true;
  }
  
  public void requestDamage( final int damage, final Object source ) {
    this.damage += damage;
    damaging = true;
  }
  
  private void takeDamage() {
    baseStats.hp = Math.max( baseStats.hp - damage, 0 );
  }
  
  @ Override
  public void update( final float delta, final RPGScreen screen ) {
    super.update( delta, screen );
    
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