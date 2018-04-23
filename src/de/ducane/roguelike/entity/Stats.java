package de.ducane.roguelike.entity;

import de.androbin.json.*;

public final class Stats {
  public int exp;
  
  public int attack;
  public int defense;
  
  public int hp;
  public int maxHp;
  
  public Stats() {
  }
  
  public Stats( final Stats stats ) {
    set( stats );
  }
  
  public Stats( final XObject data ) {
    attack = data.get( "attack" ).asInt();
    defense = data.get( "defense" ).asInt();
    exp = data.get( "exp" ).asInt();
    hp = maxHp = data.get( "hp" ).asInt();
  }
  
  public int level() {
    return (int) Math.sqrt( exp / 25f );
  }
  
  public int minExp( final int level ) {
    return level * level * 25;
  }
  
  public int remExp() {
    return minExp( level() + 1 ) - exp;
  }
  
  public void set( final Stats stats ) {
    this.exp = stats.exp;
    
    this.attack = stats.attack;
    this.defense = stats.defense;
    
    this.hp = stats.hp;
    this.maxHp = stats.maxHp;
  }
}