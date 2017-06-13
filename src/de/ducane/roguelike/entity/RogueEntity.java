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
  
  protected final Stats stats;
  
  protected int damage;
  
  protected boolean attacking;
  
  protected float moveProgress;
  protected float attackProgress;
  protected float damageProgress;
  
  protected boolean damaging;
  
  public RogueEntity( final PlayScreen screen, final Level level, final Point pos ) {
    super( level, pos );
    
    this.screen = screen;
    
    this.stats = new Stats();
    
    moveCallback = result -> ( (Level) world ).onEntityMoved( this );
  }
  
  public void attack( final Direction viewDir ) {
    final Point pos = getPos();
    final Point target = new Point( pos.x + viewDir.dx, pos.y + viewDir.dy );
    final RogueEntity entity = (RogueEntity) world.getEntity( target );
    
    if ( entity == null ) {
      return;
    }
    
    final Random random = ThreadLocalRandom.current();
    
    final int minDamage = getAttack() - entity.getDef();
    final int maxDamage = minDamage + random.nextInt( stats.stage + 1 );
    
    damage = random.nextInt( maxDamage - minDamage + 1 ) + minDamage;
    entity.takeDamage( damage );
  }
  
  public abstract void collectItem( final Item item );
  
  public int getDef() {
    return stats.defense;
  }
  
  public int getAttack() {
    return stats.attack;
  }
  
  public int getExp() {
    return stats.exp;
  }
  
  public int getMaxHp() {
    return stats.maxHp;
  }
  
  public int getHp() {
    return stats.hp;
  }
  
  public int getStage() {
    return stats.stage;
  }
  
  public boolean isAttacking() {
    return attacking;
  }
  
  public boolean isDamaging() {
    return damaging;
  }
  
  public boolean isDead() {
    return stats.hp == 0;
  }
  
  public void requestAttack() {
    attacking = true;
  }
  
  public void setHp( final int hp ) {
    stats.hp = hp;
  }
  
  public void takeDamage( final int damage ) {
    stats.hp = Math.max( 0, stats.hp - damage );
  }
}