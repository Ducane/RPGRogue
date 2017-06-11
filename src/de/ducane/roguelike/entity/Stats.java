package de.ducane.roguelike.entity;

public final class Stats {
  public int stage;
  public int exp;
  
  public int hp;
  public int maxHp;
  
  public int defense;
  public int attack;
  
  public Stats() {
  }
  
  public Stats( final Stats stats ) {
    set( stats );
  }
  
  public void set( final Stats stats ) {
    this.stage = stats.stage;
    this.exp = stats.exp;
    
    this.hp = stats.hp;
    this.maxHp = stats.maxHp;
    
    this.defense = stats.defense;
    this.attack = stats.attack;
  }
}