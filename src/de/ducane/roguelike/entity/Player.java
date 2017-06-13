package de.ducane.roguelike.entity;

import de.androbin.gfx.util.*;
import de.androbin.rpg.*;
import de.androbin.util.txt.*;
import de.ducane.roguelike.item.*;
import de.ducane.roguelike.screen.*;
import java.awt.*;
import java.awt.image.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;

public final class Player extends RogueEntity {
  private final List<Item> inventory = new LinkedList<>();
  
  private Weapon weapon;
  private Armor armor;
  private Accessoire accessoire;
  
  public BufferedImage[][] moveAnimation;
  
  public final String name;
  
  public boolean running;
  
  public Player( final PlayScreen screen, final String name ) {
    super( screen, null, new Point() );
    
    this.name = name;
    
    viewDir = Direction.DOWN;
    
    renderer = new PlayerRenderer( this );
    
    initPlayer();
    moveAnimation = prepareImages();
  }
  
  private void addExp( final int exp ) {
    baseStats.exp += exp;
  }
  
  @ Override
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
    
    if ( entity.isDead() ) {
      addExp( stats2.exp );
    }
  }
  
  @ Override
  public void collectItem( final Item item ) {
    inventory.add( item );
  }
  
  public void eat( final Food food ) {
    final Stats stats = getStats();
    baseStats.hp += Math.min( food.hp, stats.maxHp - stats.hp );
  }
  
  public Accessoire getAccessoire() {
    return accessoire;
  }
  
  public Armor getArmor() {
    return armor;
  }
  
  public List<Item> getInventory() {
    return inventory;
  }
  
  @ Override
  public Stats getStats() {
    final Stats stats = new Stats( super.getStats() );
    stats.attack += weapon == null ? 0 : weapon.attack;
    stats.defense += armor == null ? 0 : armor.defense;
    stats.maxHp += accessoire == null ? 0 : accessoire.hp;
    return stats;
  }
  
  public Weapon getWeapon() {
    return weapon;
  }
  
  private void initPlayer() {
    baseStats.maxHp = 25;
    baseStats.hp = 25;
    baseStats.stage = 1;
    baseStats.attack = 1;
    baseStats.defense = 0;
  }
  
  public void levelUp() {
    baseStats.stage++;
    
    final Random random = ThreadLocalRandom.current();
    
    baseStats.hp += random.nextInt( baseStats.stage ) + 1;
    baseStats.attack += random.nextInt( baseStats.stage ) + 1;
    baseStats.defense += random.nextInt( baseStats.stage ) + 1;
  }
  
  @ Override
  public float moveSpeed() {
    return running ? 7f : 2f;
  }
  
  private static BufferedImage[][] prepareImages() {
    final BufferedImage[][] animation = new BufferedImage[ Direction.values().length ][ 3 ];
    
    for ( int i = 0; i < animation.length; i++ ) {
      final String dir = Direction.values()[ i ].name();
      final BufferedImage image = ImageUtil.loadImage(
          "player/" + CaseUtil.toProperCase( dir ) + ".png" );
      
      for ( int j = 0; j < animation[ i ].length; j++ ) {
        animation[ i ][ j ] = image.getSubimage( j * 16, 0, 16, 18 );
      }
    }
    
    return animation;
  }
  
  @ Override
  public void requestAttack() {
    if ( screen.canAttack( this, viewDir ) ) {
      attacking = true;
    }
  }
  
  public Accessoire setAccessoire( final Accessoire accessoire ) {
    final Accessoire current = this.accessoire;
    this.accessoire = accessoire;
    return current;
  }
  
  public Armor setArmour( final Armor armour ) {
    final Armor current = this.armor;
    this.armor = armour;
    return current;
  }
  
  public Weapon setWeapon( final Weapon weapon ) {
    final Weapon current = this.weapon;
    this.weapon = weapon;
    return current;
  }
  
  @ Override
  public void update( final float delta, final RPGScreen screen ) {
    super.update( delta, screen );
    
    if ( isAttacking() ) {
      attackProgress += delta;
      
      if ( attackProgress >= 1f ) {
        damaging = true;
        attack( viewDir );
        
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