package de.ducane.roguelike.item;

import de.androbin.gfx.util.*;
import java.awt.image.*;
import org.json.simple.*;

public class Item {
  public final String name;
  public final BufferedImage image;
  public final String description;
  
  public Item( final String name, final JSONObject data ) {
    this.name = name;
    this.image = ImageUtil.loadImage( "item/" + name + ".png" );
    this.description = (String) data.get( "description" );
  }
  
  public interface Builder {
    Item build( String name, JSONObject data );
  }
}