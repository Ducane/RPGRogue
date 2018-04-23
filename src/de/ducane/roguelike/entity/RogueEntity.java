package de.ducane.roguelike.entity;

import de.androbin.rpg.*;
import de.androbin.rpg.entity.*;
import java.awt.*;
import java.util.*;
import java.util.concurrent.*;
import javafx.util.*;

public abstract class RogueEntity extends Entity {
  public final RogueEntityData data;
  
  protected final Stats baseStats;
  
  public final AttackHandle attack;
  public final DamageHandle damage;
  
  public RogueEntity( final RogueEntityData data, final int id ) {
    super( data, id );
    this.data = data;
    
    baseStats = new Stats( data.stats );
    
    attack = new AttackHandle();
    damage = new DamageHandle();
  }
  
  public Stats getStats() {
    return baseStats;
  }
  
  public boolean isDead( final boolean decay ) {
    if ( decay ) {
      return baseStats.hp == 0;
    } else {
      int totalDamage = 0;
      
      if ( damage.hasCurrent() ) {
        totalDamage += damage.getCurrent().getKey();
      }
      
      if ( damage.hasNext() ) {
        totalDamage += damage.getNext().getKey();
      }
      
      return baseStats.hp <= totalDamage;
    }
  }
  
  protected abstract void onDamage( final int damage, final Object source );
  
  @ Override
  public void update( final float delta ) {
    super.update( delta );
    attack.update( delta );
    damage.update( delta );
  }
  
  public final class AttackHandle extends Handle<Boolean, RogueEntity> {
    private final RogueEntity entity = RogueEntity.this;
    
    @ Override
    public boolean canHandle( final Boolean arg ) {
      return getTargetPoint() != null;
    }
    
    @ Override
    protected RogueEntity doHandle( final Boolean arg ) {
      final RogueEntity target = getTarget();
      
      if ( target == null ) {
        return null;
      }
      
      final Random random = ThreadLocalRandom.current();
      
      final Stats stats = getStats();
      final Stats stats2 = target.getStats();
      
      final int minDamage = Math.max( stats.attack - stats2.defense, 0 );
      final int maxDamage = minDamage + random.nextInt( stats.level() + 1 );
      
      final int damage = random.nextInt( maxDamage - minDamage + 1 ) + minDamage;
      target.damage.makeNext( new Pair<>( damage, entity ) );
      
      return target.isDead( false ) ? target : null;
    }
    
    private RogueEntity getTarget() {
      return (RogueEntity) world.getEntity( true, getTargetPoint() );
    }
    
    private Point getTargetPoint() {
      if ( move.hasCurrent() ) {
        return orientation.from( orientation.from( pos ) );
      } else {
        return orientation.from( pos );
      }
    }
  }
  
  public final class DamageHandle extends Handle<Pair<Integer, Object>, Void> {
    public DamageHandle() {
      requestCallback = ( requested, success ) -> onDamage(
          requested.getKey(), requested.getValue() );
    }
    
    @ Override
    protected Void doHandle( final Pair<Integer, Object> arg ) {
      baseStats.hp = Math.max( baseStats.hp - arg.getKey(), 0 );
      return null;
    }
    
    @ Override
    public void makeNext( final Pair<Integer, Object> arg ) {
      final int current = hasNext() ? getNext().getKey() : 0;
      super.makeNext( new Pair<>( current + arg.getKey(), arg.getValue() ) );
    }
  }
}