package de.ducane.roguelike.entity;

import de.androbin.rpg.*;
import de.androbin.rpg.entity.*;
import de.androbin.thread.*;
import de.ducane.roguelike.item.*;
import de.ducane.roguelike.level.*;
import java.awt.*;
import java.util.*;
import java.util.concurrent.*;

public final class Player extends RogueEntity {
  public final String name;
  
  public final LockedList<Item> inventory;
  public final Equipment equipment;
  
  private boolean running;
  
  public Player( final RogueEntityData data, final String name ) {
    super( data, 0 );
    
    this.name = name;
    
    inventory = new LockedList<>();
    equipment = new Equipment();
    
    baseStats.attack = 3;
    baseStats.defense = 1;
    baseStats.hp = 30;
    baseStats.maxHp = 30;
    
    move.onHandle = dir -> {
      final Level level = (Level) world;
      
      if ( !running ) {
        final Rectangle extent = dir.first.inner( getBounds() );
        
        LoopUtil.forEach( extent, pos -> {
          final Item item = level.takeItem( pos );
          
          if ( item != null ) {
            inventory.add( item );
          }
        } );
      }
      
      level.onPlayerMoved( this );
    };
    attack.onFinish = ( foo, entity ) -> loot( entity );
    
    setRunning( false );
  }
  
  private void addExp( final int exp ) {
    final int level0 = baseStats.level();
    baseStats.exp += exp;
    final int level1 = baseStats.level();
    
    for ( int i = level0; i < level1; i++ ) {
      levelUp();
    }
  }
  
  private void loot( final RogueEntity entity ) {
    if ( entity == null ) {
      return;
    }
    
    final Stats stats = entity.getStats();
    addExp( stats.exp );
    
    if ( entity instanceof Mob ) {
      final Mob mob = (Mob) entity;
      final Item item = mob.getItem();
      
      if ( item != null ) {
        mob.setItem( null );
        inventory.add( item );
      }
    }
  }
  
  public void eat( final Food food ) {
    final Stats stats = getStats();
    baseStats.hp += Math.min( food.hp, stats.maxHp - stats.hp );
  }
  
  public void equip( final int index ) {
    final Item item = inventory.get( index );
    
    if ( item instanceof Food ) {
      final Stats stats = getStats();
      
      if ( stats.hp < stats.maxHp ) {
        eat( (Food) item );
        inventory.remove( index );
      }
    } else {
      Item current = null;
      
      if ( item instanceof Accessoire ) {
        current = equipment.setAccessoire( (Accessoire) item );
      } else if ( item instanceof Armor ) {
        current = equipment.setArmor( (Armor) item );
      } else if ( item instanceof Weapon ) {
        current = equipment.setWeapon( (Weapon) item );
      }
      
      if ( current == null ) {
        inventory.remove( item );
      } else {
        inventory.set( index, current );
      }
    }
  }
  
  @ Override
  public Stats getStats() {
    final Stats stats = new Stats( super.getStats() );
    equipment.applyTo( stats );
    return stats;
  }
  
  public boolean isRunning() {
    return running;
  }
  
  public void levelUp() {
    final Random random = ThreadLocalRandom.current();
    
    baseStats.attack += random.nextInt( baseStats.level() ) + 1;
    baseStats.defense += random.nextInt( baseStats.level() ) + 1;
    baseStats.maxHp += random.nextInt( baseStats.level() ) + 1;
  }
  
  @ Override
  protected void onDamage( final int damage, final Agent source ) {
  }
  
  public void setRunning( final boolean running ) {
    this.running = running;
    move.speed = running ? 6f : 2f;
  }
}