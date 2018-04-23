package de.ducane.roguelike.item;

import de.androbin.gfx.util.*;
import de.androbin.json.*;
import java.awt.image.*;

public class Item {
  public final String name;
  public final BufferedImage image;
  public final String description;
  
  public Item( final String name, final XObject data ) {
    this.name = name;
    this.image = ImageUtil.loadImage( "item/" + name + ".png" );
    this.description = data.get( "description" ).asString();
  }
  
  public interface Builder {
    Item build( String name, XObject data );
  }
}