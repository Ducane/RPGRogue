package de.ducane.roguelike.entity;

import de.androbin.rpg.*;
import de.androbin.rpg.entity.*;
import java.awt.*;
import java.util.*;
import java.util.concurrent.*;
import javafx.util.*;

public abstract class RogueEntity extends Agent {
  public final RogueEntityData data;
  
  protected final Stats baseStats;
  
  public final AttackHandle attack;
  public final DamageHandle damage;
  
  public RogueEntity( final RogueEntityData data, final int id ) {
    super( id );
    this.data = data;
    
    baseStats = new Stats( data.stats );
    
    attack = new AttackHandle();
    damage = new DamageHandle();
  }
  
  @ Override
  public RogueEntityData getData() {
    return data;
  }
  
  public Stats getStats() {
    return baseStats;
  }
  
  public boolean isDead( final boolean decay ) {
    if ( decay ) {
      return baseStats.hp == 0;
    } else {
      return baseStats.hp <= damage.getTotal();
    }
  }
  
  protected abstract void onDamage( int damage, Agent source );
  
  @ Override
  public void update( final float delta ) {
    super.update( delta );
    attack.update( delta );
    damage.update( delta );
  }
  
  public final class AttackHandle extends Handle<Boolean, RogueEntity> {
    private final RogueEntity entity = RogueEntity.this;
    
    @ Override
    protected RogueEntity finish( final Boolean arg ) {
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
      target.damage.request( new Pair<>( damage, entity ) );
      
      return target.isDead( false ) ? target : null;
    }
    
    private RogueEntity getTarget() {
      return (RogueEntity) getSpot().world.entities.get( true, getTargetPoint() );
    }
    
    private Point getTargetPoint() {
      final Point pos = getSpot().getPos();
      
      if ( move.getCurrent() == null ) {
        return orientation.from( pos );
      } else {
        return orientation.from( pos, 2 );
      }
    }
  }
  
  public final class DamageHandle extends Handle<Pair<Integer, Agent>, Void> {
    public DamageHandle() {
      onPrepare = ( requested, success ) -> onDamage(
          requested.getKey(), requested.getValue() );
    }
    
    @ Override
    protected Void finish( final Pair<Integer, Agent> arg ) {
      baseStats.hp = Math.max( baseStats.hp - arg.getKey(), 0 );
      return null;
    }
    
    public int getTotal() {
      int total = 0;
      
      if ( getCurrent() != null ) {
        total += getCurrent().getKey();
      }
      
      if ( getNext() != null ) {
        total += getNext().getKey();
      }
      
      return total;
    }
    
    @ Override
    public void request( final Pair<Integer, Agent> arg ) {
      final int current = getNext() == null ? 0 : getNext().getKey();
      super.request( new Pair<>( current + arg.getKey(), arg.getValue() ) );
    }
  }
}