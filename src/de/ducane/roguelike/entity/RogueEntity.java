package de.ducane.roguelike.entity;

import de.androbin.rpg.*;
import de.ducane.roguelike.item.*;
import de.ducane.roguelike.level.*;
import de.ducane.roguelike.screen.*;
import java.awt.*;
import java.util.*;
import java.util.concurrent.*;

public abstract class RogueEntity extends Entity {
  protected final PlayScreen screen;
  
  protected final Stats baseStats;
  
  protected int damage;
  
  protected boolean attacking;
  
  protected float moveProgress;
  protected float attackProgress;
  protected float damageProgress;
  
  protected boolean damaging;
  
  public RogueEntity( final PlayScreen screen, final Level level, final Point pos ) {
    super( level, pos );
    
    this.screen = screen;
    
    this.baseStats = new Stats();
    
    moveCallback = () -> ( (Level) world ).onEntityMoved( this );
  }
  
  public void attack( final Direction viewDir ) {
    final Point pos = getPos();
    final Point target = new Point( pos.x + viewDir.dx, pos.y + viewDir.dy );
    final RogueEntity entity = (RogueEntity) world.getEntity( target );
    
    if ( entity == null ) {
      return;
    }
    
    final Random random = ThreadLocalRandom.current();
    
    final Stats stats = getStats();
    final Stats stats2 = entity.getStats();
    
    final int minDamage = stats.attack - stats2.defense;
    final int maxDamage = minDamage + random.nextInt( stats.stage + 1 );
    
    damage = random.nextInt( maxDamage - minDamage + 1 ) + minDamage;
    entity.takeDamage( damage );
  }
  
  public abstract void collectItem( Item item );
  
  public Stats getStats() {
    return baseStats;
  }
  
  public boolean isAttacking() {
    return attacking;
  }
  
  public boolean isDamaging() {
    return damaging;
  }
  
  public boolean isDead() {
    return baseStats.hp == 0;
  }
  
  public void requestAttack() {
    attacking = true;
  }
  
  public void takeDamage( final int damage ) {
    baseStats.hp = Math.max( baseStats.hp - damage, 0 );
  }
}