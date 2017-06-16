package de.ducane.roguelike.entity;

import org.json.simple.*;

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
  
  public Stats( final JSONObject data ) {
    attack = ( (Number) data.get( "attack" ) ).intValue();
    defense = ( (Number) data.get( "defense" ) ).intValue();
    exp = ( (Number) data.get( "exp" ) ).intValue();
    hp = maxHp = ( (Number) data.get( "hp" ) ).intValue();
  }
  
  public int level() {
    return (int) Math.sqrt( exp / 25f );
  }
  
  public void set( final Stats stats ) {
    this.exp = stats.exp;
    
    this.attack = stats.attack;
    this.defense = stats.defense;
    
    this.hp = stats.hp;
    this.maxHp = stats.maxHp;
  }
}