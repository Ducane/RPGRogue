package de.ducane.roguelike.menu;

import static de.androbin.gfx.util.GraphicsUtil.*;
import de.androbin.gfx.util.*;
import de.androbin.thread.*;
import de.ducane.roguelike.entity.*;
import de.ducane.roguelike.item.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;

public final class StatsMenu extends Menu {
  private final String[] labels = { "Weapon", "Armor", "Accessoire" };
  
  private Rectangle2D.Float statBounds;
  
  public StatsMenu( final MainMenu parent ) {
    super( parent );
  }
  
  @ Override
  protected String getLabel( final int index, final LockedList<Item> inventory ) {
    return null;
  }
  
  @ Override
  public void onResized( final int width, final int height ) {
    stroke = new BasicStroke( 0.005f * width );
    
    bounds = new Rectangle2D.Float( 0.25f * width, 0f, 0.5f * width, 0.7f * height );
    
    statBounds = new Rectangle2D.Float(
        0.3f * width, 0.0525f * height, 0.15f * width, 0.56f * height );
    
    buttonBounds = new Rectangle2D.Float[ 3 ];
    
    final Rectangle2D.Float singleButton = new Rectangle2D.Float(
        0.65f * width, 0.0525f * height, 0.05f * width, 0.05f * width );
    
    resizeButtons( singleButton, 0.21f * height );
  }
  
  @ Override
  public void render( final Graphics2D g, final Player player ) {
    super.render( g, player );
    
    final Equipment equipment = player.equipment;
    final Item[] items = {
        equipment.getWeapon(),
        equipment.getArmor(),
        equipment.getAccessoire()
    };
    
    final Stats stats = player.getStats();
    
    final Stats stats0 = new Stats();
    equipment.applyTo( stats0 );
    
    final String[] statStrings = {
        "Stats:",
        "Level: " + stats.level(),
        "HP: " + stats.hp + "/" + stats.maxHp + "(+" + stats0.maxHp + ")",
        "ATK: " + stats.attack + "(+" + stats0.attack + ")",
        "DEF: " + stats.defense + "(+" + stats0.defense + ")", "EXP: " + stats.exp,
        "REXP: " + stats.remExp()
    };
    
    final FontMetrics fm = g.getFontMetrics();
    g.setColor( Color.WHITE );
    
    for ( int i = 0; i < statStrings.length; i++ ) {
      final String statString = statStrings[ i ];
      
      g.drawString( statString,
          statBounds.x + ( statBounds.width - fm.stringWidth( statString ) ) * 0.5f,
          statBounds.y + ( i * 2f + 1f ) * fm.getAscent() );
    }
    
    g.drawString( "Name: " + player.name,
        bounds.x + ( bounds.width - fm.stringWidth( "Name: " + player.name ) ) * 0.5f,
        statBounds.y + statBounds.height + fm.getAscent() );
    
    for ( int i = 0; i < labels.length; i++ ) {
      if ( items[ i ] == null ) {
        final BufferedImage icon = ImageUtil.loadImage(
            "menu/character/" + labels[ i ] + "-Icon.png" );
        drawImage( g, icon, buttonBounds[ i ] );
      } else {
        final BufferedImage frame = ImageUtil.loadImage(
            "menu/character/icon.png" );
        final BufferedImage icon = items[ i ].image;
        drawImage( g, frame, buttonBounds[ i ] );
        drawImage( g, icon, buttonBounds[ i ] );
      }
    }
    
    for ( int i = 0; i < items.length; i++ ) {
      final Item item = items[ i ];
      
      if ( item == null ) {
        continue;
      }
      
      final Rectangle2D.Float rect = buttonBounds[ i ];
      
      drawImage( g, item.image, rect );
      
      final String name = item.name;
      final String trimName = name.length() > 10
          ? name.substring( 0, 10 )
          : name;
      
      g.drawString( trimName,
          rect.x - fm.stringWidth( trimName ) - rect.width * 0.5f,
          rect.y + ( rect.height - fm.getHeight() ) * 0.5f + fm.getAscent() );
    }
  }
  
  @ Override
  protected Menu runCommand( final Player player ) {
    final Equipment equipment = player.equipment;
    final Item[] items = {
        equipment.getWeapon(),
        equipment.getArmor(),
        equipment.getAccessoire()
    };
    
    if ( items[ selection ] == null ) {
      return this;
    }
    
    final LockedList<Item> inventory = player.inventory;
    
    switch ( selection ) {
      case 0:
        inventory.add( equipment.setWeapon( null ) );
        break;
      case 1:
        inventory.add( equipment.setArmor( null ) );
        break;
      case 2:
        inventory.add( equipment.setAccessoire( null ) );
        break;
    }
    
    return this;
  }
}