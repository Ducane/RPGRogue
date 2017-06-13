package de.ducane.roguelike.screen;

import static de.androbin.gfx.util.GraphicsUtil.*;
import de.androbin.game.*;
import de.androbin.game.listener.*;
import de.androbin.rpg.*;
import de.androbin.rpg.gfx.*;
import de.androbin.rpg.tile.*;
import de.androbin.util.*;
import de.ducane.roguelike.*;
import de.ducane.roguelike.entity.*;
import de.ducane.roguelike.item.*;
import de.ducane.roguelike.level.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.util.List;
import org.json.simple.*;

public final class PlayScreen extends RPGScreen {
  private Level level;
  private int floor;
  private int requestedFloor;
  
  private Blackout blackout;
  
  private List<Rectangle> rooms;
  private Rectangle room;
  
  private Rectangle2D.Float barBounds;
  
  private State state;
  
  private Rectangle2D.Float statBounds;
  private Rectangle2D.Float statImageBounds;
  
  private Stroke menuStroke;
  private Rectangle2D.Float menuBounds;
  private Stroke menuButtonStroke;
  
  private static final String[] MENU_BUTTON_LABELS = { "Inventar", "Beenden" };
  
  private Stroke inventoryStroke;
  private Rectangle2D.Float inventoryBounds;
  private Stroke inventoryButtonStroke;
  private float inventoryButtonDY;
  
  private Stroke itemSelectStroke;
  private Rectangle2D.Float itemSelectBounds;
  
  private String[] itemSelectButtonLabels = new String[ 2 ];
  
  private Rectangle2D.Float pageCursorRight;
  private Rectangle2D.Float pageCursorLeft;
  private int pageIndex;
  
  public PlayScreen( final Game game, final float scale, final String name ) {
    super( game, scale );
    
    Tiles.builder = data -> new RogueTile( this, data );
    
    player = new Player( this, name );
    camera.setFocus( Camera.focus( player ) );
    
    updateFloor();
  }
  
  public boolean canAttack( final RogueEntity entity, final Direction viewDir ) {
    final Point pos = entity.getPos();
    
    final int x = pos.x + viewDir.dx;
    final int y = pos.y + viewDir.dy;
    
    return world.getEntity( new Point( x, y ) ) != null;
  }
  
  @ Override
  public Level createWorld( final String name ) {
    final int floor = Integer.parseInt( name.split( "-" )[ 1 ] );
    final int index = Math.min( ( floor / 4 + 1 ), 5 );
    
    final String path = "level/level" + index + ".json";
    final JSONObject data = (JSONObject) JSONUtil.parseJSON( path ).get();
    
    return LevelGenerator.generate( this, name, data );
  }
  
  private Rectangle currentRoom() {
    Rectangle tileRoom = null;
    
    for ( final Rectangle room : rooms ) {
      final int x = room.x * 2 - 1;
      final int y = room.y * 2 - 1;
      final int width = room.width * 2 + 1;
      final int height = room.height * 2 + 1;
      
      tileRoom = new Rectangle( x, y, width, height );
      
      if ( tileRoom.contains( player.getPos() ) ) {
        return tileRoom;
      }
    }
    
    return null;
  }
  
  private void debug( final Graphics2D g ) {
    final FontMetrics fm = g.getFontMetrics();
    final Player player = getPlayer();
    
    final Point2D.Float pos = player.getFloatPos();
    
    final String x = String.valueOf( pos.x );
    final String y = String.valueOf( pos.y );
    
    final String viewDir = "viewDir: " + player.getViewDir();
    final String moveDir = "moveDir: " + player.getMoveDir();
    
    final String name = "name: " + player.name;
    
    g.drawString( x, getWidth() - fm.stringWidth( x ), getHeight() - fm.getHeight() * 5 );
    g.drawString( y, getWidth() - fm.stringWidth( y ), getHeight() - fm.getHeight() * 4 );
    
    g.drawString( viewDir, getWidth() - fm.stringWidth( viewDir ),
        getHeight() - fm.getHeight() * 3 );
    g.drawString( moveDir, getWidth() - fm.stringWidth( moveDir ),
        getHeight() - fm.getHeight() * 2 );
    
    g.drawString( name, getWidth() - fm.stringWidth( name ), getHeight() - fm.getHeight() * 1 );
  }
  
  private void equip( final int index ) {
    final Player player = getPlayer();
    final List<Item> inventory = player.getInventory();
    final Item item = inventory.get( index );
    
    if ( item instanceof Food ) {
      final Stats stats = player.getStats();
      
      if ( stats.hp < stats.maxHp ) {
        player.eat( (Food) item );
        inventory.remove( index );
      }
    } else if ( item instanceof Weapon ) {
      if ( player.getWeapon() == null ) {
        inventory.remove( index );
      } else {
        inventory.set( index, player.setWeapon( null ) );
      }
      
      player.setWeapon( (Weapon) item );
    } else if ( item instanceof Accessoire ) {
      if ( player.getAccessoire() == null ) {
        inventory.remove( index );
      } else {
        inventory.set( index, player.setWeapon( null ) );
      }
      
      player.setAccessoire( (Accessoire) item );
    } else if ( item instanceof Armor ) {
      if ( player.getArmor() == null ) {
        inventory.remove( index );
      } else {
        inventory.set( index, player.setWeapon( null ) );
      }
      
      player.setArmour( (Armor) item );
    }
  }
  
  public Blackout getBlackout() {
    return blackout;
  }
  
  public Point2D.Float getBlackoutPos() {
    final Point2D.Float pos = getPlayer().getFloatPos();
    return room == null
        ? new Point2D.Float( ( pos.x + 0.5f ) * scale, ( pos.y + 0.5f ) * scale )
        : new Point2D.Float( room.x * scale, room.y * scale );
  }
  
  @ Override
  public KeyListener getKeyListener() {
    return new TeeKeyListener( new PlayKeyListener(), super.getKeyListener() );
  }
  
  @ Override
  public MouseListener getMouseListener() {
    return new PlayMouseListener();
  }
  
  @ Override
  public MouseMotionListener getMouseMotionListener() {
    return new PlayMouseMotionListener();
  }
  
  public Player getPlayer() {
    return (Player) player;
  }
  
  public void onPlayerMoved() {
    this.room = currentRoom();
    updateBlackout();
    
    level.moveMobs();
  }
  
  @ Override
  public void onResized( final int width, final int height ) {
    barBounds = new Rectangle2D.Float(
        0.2f * width, 0.05f * height, 0.4f * width, 0.025f * height );
    
    statBounds = new Rectangle2D.Float(
        0.1f * width, 0.1f * height, 0.2f * width, 0.25f * height );
    statImageBounds = new Rectangle2D.Float(
        0.15f * width, 0.15f * height, 0.1f * width, 0.15f * height );
    
    menuBounds = new Rectangle2D.Float(
        0.1f * width, 0.4f * height, 0.2f * width, 0.4f * height );
    menuStroke = new BasicStroke( 0.005f * getWidth() );
    menuButtonStroke = new BasicStroke( 0f );
    
    final Rectangle2D.Float menuButtonBounds = new Rectangle2D.Float(
        0.125f * width, 0.45f * height, 0.15f * width, 0.05f * height );
    final float menuButtonDY = 0.075f * height;
    
    State.Menu.buttonBounds = new Rectangle2D.Float[ MENU_BUTTON_LABELS.length ];
    
    for ( int i = 0; i < State.Menu.buttonBounds.length; i++ ) {
      State.Menu.buttonBounds[ i ] = new Rectangle2D.Float(
          menuButtonBounds.x, menuButtonBounds.y + menuButtonDY * i,
          menuButtonBounds.width, menuButtonBounds.height );
    }
    
    inventoryStroke = new BasicStroke( 0.005f * getWidth() );
    inventoryBounds = new Rectangle2D.Float(
        0.35f * width, 0.1f * height, 0.25f * width, 0.7f * height );
    inventoryButtonStroke = new BasicStroke( 0f );
    
    final Rectangle2D.Float inventoryButtonBounds = new Rectangle2D.Float(
        0.4f * width, 0.125f * height, 0.15f * width, 0.05f * height );
    
    inventoryButtonDY = 0.075f * height;
    State.Inventory.buttonBounds = new Rectangle2D.Float[ 8 ];
    
    for ( int i = 0; i < State.Inventory.buttonBounds.length; i++ ) {
      State.Inventory.buttonBounds[ i ] = new Rectangle2D.Float(
          inventoryButtonBounds.x, inventoryButtonBounds.y + inventoryButtonDY * i,
          inventoryButtonBounds.width, inventoryButtonBounds.height );
    }
    
    pageCursorLeft = new Rectangle2D.Float(
        inventoryBounds.x + ( 0.1f * inventoryBounds.width ),
        inventoryBounds.y + ( 0.95f * inventoryBounds.height ), 10f, 10f );
    pageCursorRight = new Rectangle2D.Float(
        inventoryBounds.x + ( 0.9f * inventoryBounds.width ),
        inventoryBounds.y + ( 0.95f * inventoryBounds.height ), 10f, 10f );
    
    itemSelectStroke = new BasicStroke( 0.005f * getWidth() );
    itemSelectBounds = new Rectangle2D.Float(
        0.65f * width, 0.15f * height, 0.2f * width, 0.25f * height );
    
    final Rectangle2D.Float itemSelectButtonBounds = new Rectangle2D.Float(
        0.675f * width, 0.175f * height, 0.15f * width, 0.05f * height );
    final float itemSelectButtonDY = 0.075f * height;
    
    State.ItemSelect.buttonBounds = new Rectangle2D.Float[ 2 ];
    
    for ( int i = 0; i < State.ItemSelect.buttonBounds.length; i++ ) {
      State.ItemSelect.buttonBounds[ i ] = new Rectangle2D.Float(
          itemSelectButtonBounds.x, itemSelectButtonBounds.y + itemSelectButtonDY * i,
          itemSelectButtonBounds.width, itemSelectButtonBounds.height );
    }
  }
  
  @ Override
  public void render( final Graphics2D g ) {
    super.render( g );
    
    final Point2D.Float pos = getBlackoutPos();
    blackout.darken( g, pos.x + trans.x, pos.y + trans.y, getWidth(), getHeight() );
    
    level.renderMiniMap( g, getPlayer().getFloatPos(), scale, getWidth() );
    
    g.setColor( Color.WHITE );
    
    if ( Main.DEBUG ) {
      debug( g );
    }
    
    renderHPBar( g );
    
    g.setColor( Color.WHITE );
    
    final Player player = getPlayer();
    final Stats stats = player.getStats();
    
    g.setFont( new Font( "Determination Mono", 0, (int) ( 0.04 * getHeight() ) ) );
    g.drawString( "Lv " + stats.stage,
        barBounds.x - 0.1f * getWidth(), barBounds.y );
    g.drawString( "E" + floor, barBounds.x - 0.175f * getWidth(), barBounds.y );
    g.drawString( "HP " + stats.hp + "/" + stats.maxHp,
        barBounds.x, barBounds.y - 0.01f * getHeight() );
    
    final FontMetrics fm = g.getFontMetrics();
    
    if ( state != null ) {
      g.setColor( Color.BLACK );
      fillRect( g, menuBounds );
      g.setStroke( menuStroke );
      g.setColor( Color.WHITE );
      drawRect( g, menuBounds );
      
      g.setStroke( menuButtonStroke );
      
      for ( int i = 0; i < State.Menu.buttonBounds.length; i++ ) {
        final Rectangle2D.Float rect = State.Menu.buttonBounds[ i ];
        g.setColor( state == State.Menu && i == state.selection ? Color.YELLOW : Color.WHITE );
        g.drawString( MENU_BUTTON_LABELS[ i ],
            rect.x + ( rect.width - fm.stringWidth( MENU_BUTTON_LABELS[ i ] ) ) * 0.5f,
            rect.y + ( rect.height - fm.getHeight() ) * 0.5f + fm.getAscent() );
      }
    }
    
    if ( state == State.Inventory || state == State.ItemSelect ) {
      g.setColor( Color.BLACK );
      fillRect( g, inventoryBounds );
      g.setStroke( inventoryStroke );
      g.setColor( Color.WHITE );
      drawRect( g, inventoryBounds );
      
      final List<Item> inventory = player.getInventory();
      g.setStroke( inventoryButtonStroke );
      
      for ( int i = pageIndex * 8; i < ( pageIndex + 1 ) * 8; i++ ) {
        final Rectangle2D.Float rect = State.Inventory.buttonBounds[ i % 8 ];
        
        if ( inventory.size() > i ) {
          final String itemName = inventory.get( i ).name;
          final String subItemName = itemName.length() > 10
              ? itemName.substring( 0, 10 ) : itemName;
          g.setColor( state == State.Inventory && i % 8 == state.selection
              ? Color.YELLOW : Color.WHITE );
          g.drawString( subItemName, rect.x + ( rect.width - fm.stringWidth( subItemName ) ) * 0.5f,
              rect.y + ( rect.height - fm.getHeight() ) * 0.5f + fm.getAscent() );
        } else {
          final String nothing = "----------";
          g.setColor( Color.WHITE );
          g.drawString( nothing,
              rect.x + ( rect.width - fm.stringWidth( nothing ) ) * 0.5f,
              rect.y + ( rect.height - fm.getHeight() ) * 0.5f + fm.getAscent() );
        }
      }
      
      final String size = inventory.size() + "/" + ( pageIndex + 1 );
      g.setColor( Color.WHITE );
      g.drawString( size,
          inventoryBounds.x + ( inventoryBounds.width - fm.stringWidth( size ) ) * 0.5f,
          inventoryBounds.y + 0.95f * inventoryBounds.height );
      fillRect( g, pageCursorLeft );
      fillRect( g, pageCursorRight );
      
      final int index = State.Inventory.selection + pageIndex * 8;
      
      if ( inventory.size() > index ) {
        g.setColor( Color.BLACK );
        fillRect( g, statBounds );
        g.setStroke( inventoryStroke );
        g.setColor( Color.WHITE );
        drawRect( g, statBounds.x, inventoryBounds.y, statBounds.width, statBounds.height );
      }
      
      if ( !inventory.isEmpty() && inventory.size() > index ) {
        final int slot = pageIndex > 0 ? index : State.Inventory.selection;
        final BufferedImage image = inventory.get( slot ).image;
        drawImage( g, image, statImageBounds );
      }
    }
    
    if ( state == State.ItemSelect ) {
      g.setColor( Color.BLACK );
      fillRect( g, itemSelectBounds.x,
          itemSelectBounds.y + State.Inventory.selection * inventoryButtonDY,
          itemSelectBounds.width, itemSelectBounds.height );
      g.setStroke( itemSelectStroke );
      g.setColor( Color.WHITE );
      drawRect( g, itemSelectBounds.x,
          itemSelectBounds.y + State.Inventory.selection * inventoryButtonDY,
          itemSelectBounds.width, itemSelectBounds.height );
      
      for ( int i = 0; i < State.ItemSelect.buttonBounds.length; i++ ) {
        final Rectangle2D.Float bounds = State.ItemSelect.buttonBounds[ i ];
        g.setColor( i == state.selection ? Color.YELLOW : Color.WHITE );
        g.drawString( itemSelectButtonLabels[ i ],
            bounds.x + ( bounds.width - fm.stringWidth( itemSelectButtonLabels[ i ] ) ) * 0.5f,
            bounds.y + ( bounds.height - fm.getHeight() ) * 0.5f + fm.getAscent() );
      }
    }
  }
  
  private void renderHPBar( final Graphics2D g ) {
    final Player player = getPlayer();
    final Stats stats = player.getStats();
    
    final float progress = (float) stats.hp / stats.maxHp;
    final float progressWidth = barBounds.width * progress;
    
    final BufferedImage barImage = new BufferedImage( (int) progressWidth, (int) barBounds.height,
        BufferedImage.TYPE_INT_ARGB );
    
    final int color = new Color(
        progress <= 0.5f ? 0.5f : 1f - progress,
        progress >= 0.5f ? 0.5f : progress,
        0f ).getRGB();
    
    for ( int y = 0; y < barImage.getHeight(); y++ ) {
      for ( int x = 0; x < barImage.getWidth(); x++ ) {
        barImage.setRGB( x, y, color );
      }
    }
    
    g.setColor( Color.BLACK );
    fillRect( g, barBounds );
    
    drawImage( g, barImage, barBounds.x, barBounds.y );
    
    g.setStroke( new BasicStroke( 0.0003f * getHeight() ) );
    
    final Color backgroundColor = new Color(
        progress < 0.5f ? 0.3f : ( 1f - progress ) * 0.6f,
        progress > 0.5f ? 0.3f : progress * 0.6f,
        0f );
    
    g.setColor( backgroundColor );
    drawRect( g, barBounds );
  }
  
  public void requestNextFloor() {
    requestedFloor = floor + 1;
  }
  
  public void requestPreviousFloor() {
    requestedFloor = Math.max( floor - 1, 0 );
  }
  
  private void runItemSelectCommand( final int selection ) {
    final Player player = getPlayer();
    final List<Item> inventory = player.getInventory();
    
    final int index = State.Inventory.selection + pageIndex * 8;
    final Item item = inventory.get( index );
    
    switch ( selection ) {
      case 0:
        equip( index );
        state = State.Inventory;
        break;
      case 1:
        // TODO display item info
        break;
      case 2:
        inventory.remove( item );
        break;
    }
  }
  
  private void runInventoryCommand( final int selection ) {
    final Player player = getPlayer();
    final List<Item> inventory = player.getInventory();
    final int index = selection + pageIndex * 8;
    
    if ( inventory.size() > index ) {
      state = State.ItemSelect;
      
      final Item item = inventory.get( index );
      
      if ( item instanceof Food ) {
        itemSelectButtonLabels[ 0 ] = "Eat";
      } else if ( item instanceof Weapon || item instanceof Armor || item instanceof Accessoire ) {
        itemSelectButtonLabels[ 0 ] = "Equip";
      }
      
      itemSelectButtonLabels[ 1 ] = "Throw";
    }
  }
  
  private void runMenuCommand( final int selection ) {
    switch ( selection ) {
      case 0:
        state = State.Inventory;
        break;
      case 1:
        game.gsm.close();
        break;
    }
  }
  
  @ Override
  public void switchWorld( final String name, final Point pos ) {
    super.switchWorld( name, pos );
    level = (Level) world;
  }
  
  @ Override
  public void update( final float delta ) {
    super.update( delta );
    
    for ( final Entity entity : world.listEntities() ) {
      if ( entity instanceof Mob && getPlayer().isDamaging() ) {
        ( (Mob) entity ).requestAttack();
      }
    }
    
    level.update();
    
    if ( floor != requestedFloor ) {
      floor = requestedFloor;
      updateFloor();
    }
  }
  
  private void updateBlackout() {
    final Color color = new Color( 0f, 0f, 0f, 0.8f );
    
    if ( room == null ) {
      blackout = new CircularBlackout( color, scale * 1.5f );
    } else {
      blackout = new RectangularBlackout( color, scale * room.width, scale * room.height );
    }
    
    level.updateMiniMap( blackout, scale, getBlackoutPos() );
  }
  
  public void updateFloor() {
    final String name = "floor-" + floor;
    final Level level = (Level) getWorld( name );
    switchWorld( name, level.getUpStairsPos() );
    rooms = level.getRooms();
    room = currentRoom();
    updateBlackout();
  }
  
  private class PlayKeyListener extends KeyAdapter {
    @ Override
    public void keyPressed( final KeyEvent event ) {
      final Player player = getPlayer();
      
      if ( event.isShiftDown() ) {
        player.running = true;
      }
    }
    
    @ Override
    public void keyReleased( final KeyEvent event ) {
      final Player player = getPlayer();
      
      if ( !event.isShiftDown() ) {
        player.running = false;
      }
      
      switch ( event.getKeyCode() ) {
        case KeyEvent.VK_SPACE:
          if ( !player.isDamaging() ) {
            player.requestAttack();
          }
          break;
        case KeyEvent.VK_M:
          state = state == null ? State.Menu : null;
          break;
      }
    }
  }
  
  private class PlayMouseListener extends MouseAdapter {
    @ Override
    public void mousePressed( final MouseEvent event ) {
      if ( state == null ) {
        return;
      }
      
      if ( event.getButton() == MouseEvent.BUTTON1 ) {
        switch ( state ) {
          case Menu:
            runMenuCommand( state.selection );
            break;
          
          case Inventory:
            for ( final Rectangle2D.Float bounds : State.Inventory.buttonBounds ) {
              if ( bounds.contains( event.getPoint() ) ) {
                runInventoryCommand( state.selection );
                break;
              }
            }
            
            if ( pageCursorLeft.contains( event.getPoint() ) ) {
              pageIndex = Math.max( 0, pageIndex - 1 );
            }
            
            if ( pageCursorRight.contains( event.getPoint() )
                && getPlayer().getInventory().size() > ( pageIndex + 1 ) * 8 ) {
              pageIndex++;
            }
            
            break;
          
          case ItemSelect:
            runItemSelectCommand( state.selection );
            break;
        }
      } else if ( event.getButton() == MouseEvent.BUTTON3 ) {
        state = state.previous();
      }
    }
  }
  
  private class PlayMouseMotionListener extends MouseAdapter {
    @ Override
    public void mouseMoved( final MouseEvent event ) {
      if ( state == null ) {
        return;
      }
      
      for ( int i = 0; i < state.buttonBounds.length; i++ ) {
        if ( state.buttonBounds[ i ].contains( event.getPoint() ) ) {
          state.selection = i;
        }
      }
    }
  }
}