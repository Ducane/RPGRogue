package de.ducane.roguelike.item;

import java.awt.image.*;

public class Item {
  public final BufferedImage image;
  public final String name;
  public final String description;
  
  public Item( final String name, final String description, final BufferedImage image ) {
    this.image = image;
    this.name = name;
    this.description = description;
  }
}