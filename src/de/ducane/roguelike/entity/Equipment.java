package de.ducane.roguelike.entity;

import de.ducane.roguelike.item.*;

public final class Equipment {
  private Accessoire accessoire;
  private Armor armor;
  private Weapon weapon;
  
  public void applyTo( final Stats stats ) {
    stats.attack += weapon == null ? 0 : weapon.attack;
    stats.defense += armor == null ? 0 : armor.defense;
    stats.maxHp += accessoire == null ? 0 : accessoire.hp;
  }
  
  public Accessoire getAccessoire() {
    return accessoire;
  }
  
  public Armor getArmor() {
    return armor;
  }
  
  public Weapon getWeapon() {
    return weapon;
  }
  
  public Accessoire setAccessoire( final Accessoire accessoire ) {
    final Accessoire current = this.accessoire;
    this.accessoire = accessoire;
    return current;
  }
  
  public Armor setArmor( final Armor armor ) {
    final Armor current = this.armor;
    this.armor = armor;
    return current;
  }
  
  public Weapon setWeapon( final Weapon weapon ) {
    final Weapon current = this.weapon;
    this.weapon = weapon;
    return current;
  }
}