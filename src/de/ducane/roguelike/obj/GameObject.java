package de.ducane.roguelike.obj;

import de.androbin.gfx.util.*;
import de.ducane.roguelike.entity.*;
import de.ducane.roguelike.level.*;
import java.awt.image.*;

public abstract class GameObject {
  public final BufferedImage image;
  protected boolean passable;
  
  protected GameObject( final String name ) {
    image = ImageUtil.loadImage( "obj/" + name + ".png" );
  }
  
  public abstract void onPlayerEntered( Level level, Player player );
}