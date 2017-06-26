package de.ducane.roguelike.entity;

import de.androbin.rpg.*;
import java.awt.*;
import java.util.*;
import java.util.concurrent.*;
import javafx.util.*;

public abstract class RogueEntity extends Entity {
  public final RogueEntityData data;
  
  protected final Stats baseStats;
  
  public final Handle<Boolean, RogueEntity> attack;
  public final Handle<Pair<Integer, Object>, Void> damage;
  
  public RogueEntity( final RogueEntityData data ) {
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
      
      if ( damage.hasRequested() ) {
        totalDamage += damage.getRequested().getKey();
      }
      
      return baseStats.hp <= totalDamage;
    }
  }
  
  protected abstract void onDamage( final int damage, final Object source );
  
  @ Override
  public void updateStrong( final RPGScreen master ) {
    super.updateStrong( master );
    attack.updateStrong( master );
    damage.updateStrong( master );
  }
  
  @ Override
  public void updateWeak( final float delta ) {
    super.updateWeak( delta );
    attack.updateWeak( delta );
    damage.updateWeak( delta );
  }
  
  private final class AttackHandle extends Handle<Boolean, RogueEntity> {
    private final RogueEntity entity = RogueEntity.this;
    
    @ Override
    public boolean canHandle( final Boolean arg ) {
      return getTargetPoint() != null;
    }
    
    @ Override
    protected RogueEntity doHandle( final RPGScreen screen, final Boolean arg ) {
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
      return (RogueEntity) world.getEntity( getTargetPoint() );
    }
    
    private Point getTargetPoint() {
      if ( move.hasCurrent() ) {
        return viewDir.from( viewDir.from( getPos() ) );
      } else {
        return viewDir.from( getPos() );
      }
    }
  }
  
  private final class DamageHandle extends Handle<Pair<Integer, Object>, Void> {
    public DamageHandle() {
      requestCallback = ( requested, success ) -> onDamage(
          requested.getKey(), requested.getValue() );
    }
    
    @ Override
    protected Void doHandle( final RPGScreen screen, final Pair<Integer, Object> arg ) {
      baseStats.hp = Math.max( baseStats.hp - arg.getKey(), 0 );
      return null;
    }
    
    @ Override
    public void request( final Pair<Integer, Object> arg ) {
      final int current = hasRequested() ? getRequested().getKey() : 0;
      super.request( new Pair<>( current + arg.getKey(), arg.getValue() ) );
    }
  }
}