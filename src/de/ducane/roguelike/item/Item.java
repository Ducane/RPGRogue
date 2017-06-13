package de.ducane.roguelike.item;

import de.androbin.gfx.util.*;
import java.awt.image.*;
import org.json.simple.*;

public class Item {
  public final String name;
  public final String description;
  public final BufferedImage image;
  
  public Item( final String name, final JSONObject data ) {
    this.name = name;
    this.description = (String) data.get( "description" );
    this.image = ImageUtil.loadImage( "item/" + name + ".png" );
  }
}