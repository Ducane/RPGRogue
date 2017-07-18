package de.ducane.roguelike.entity;

import de.ducane.roguelike.dark.*;
import java.awt.*;
import java.awt.image.*;

public final class MobRenderer extends RogueEntityRenderer {
  private final MovingDark dark;
  
  public MobRenderer( final Mob mob, final BufferedImage[][] animation, final MovingDark dark ) {
    super( mob, animation );
    this.dark = dark;
  }
  
  @ Override
  public void render( final Graphics2D g, final float scale ) {
    super.render( dark.clip( g ), scale );
  }
}