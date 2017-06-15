package de.ducane.roguelike.entity;

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