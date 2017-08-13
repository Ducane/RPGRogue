package de.ducane.roguelike.screen;

import de.androbin.screen.*;
import de.androbin.screen.transit.*;
import de.androbin.shell.*;
import de.androbin.shell.gfx.*;
import de.androbin.shell.input.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

public final class NewGameScreen extends BasicShell implements AWTGraphics {
  private final SmoothScreenManager<AWTTransition> screens;
  
  private final StringBuilder name;
  private FontMetrics fm;
  
  private Rectangle2D.Float buttonBounds;
  private boolean buttonSelection;
  
  public NewGameScreen( final SmoothScreenManager<AWTTransition> screens ) {
    this.screens = screens;
    
    addKeyInput( new NewGameKeyInput() );
    addMouseInput( new NewGameMouseInput() );
    addMouseMotionInput( new NewGameMouseMotionInput() );
    
    name = new StringBuilder();
  }
  
  @ Override
  protected void onResized( final int width, final int height ) {
    final Canvas c = new Canvas();
    fm = c.getFontMetrics( new Font( "Determination Mono", 0, (int) ( 0.1f * getHeight() ) ) );
    
    final float buttonHeight = fm.getAscent() - fm.getLeading();
    buttonBounds = new Rectangle2D.Float(
        0.85f * getWidth(), 0.9f * getHeight() - buttonHeight,
        fm.stringWidth( "OK" ), buttonHeight );
  }
  
  @ Override
  public void render( final Graphics2D g ) {
    g.setColor( Color.BLACK );
    g.fillRect( 0, 0, getWidth(), getHeight() );
    
    g.setFont( new Font( "Determination Mono", 0, (int) ( 0.05625f * getWidth() ) ) );
    final String output = name.toString();
    
    g.setColor( Color.WHITE );
    g.drawString( "Benenne deinen Helden!",
        0.05f * getWidth(), fm.getAscent() + 0.1f * getHeight() );
    
    g.setColor( buttonSelection ? Color.YELLOW : Color.WHITE );
    g.drawString( "OK", buttonBounds.x, buttonBounds.y
        + ( buttonBounds.height - fm.getAscent() - fm.getLeading() ) * 0.5f + fm.getAscent() );
    
    g.setColor( Color.WHITE );
    g.drawString( output, ( getWidth() - fm.stringWidth( output ) ) * 0.5f,
        getHeight() * 0.5f );
  }
  
  private void startGame() {
    screens.fadeSwitchTo( new PlayScreen( screens, 48f, name.toString() ),
        new AWTColorCrossfade( Color.BLACK, 0.5f, 1f ) );
  }
  
  @ Override
  public void update( final float delta ) {
  }
  
  private final class NewGameKeyInput implements KeyInput {
    @ Override
    public void keyPressed( final int keycode ) {
      if ( name.length() > 0 ) {
        switch ( keycode ) {
          case KeyEvent.VK_BACK_SPACE:
            name.deleteCharAt( name.length() - 1 );
            break;
          case KeyEvent.VK_ENTER:
            startGame();
            break;
        }
      }
    }
    
    @ Override
    public void keyTyped( final char keychar ) {
      if ( Character.isLetterOrDigit( keychar ) && name.length() <= 12 ) {
        name.append( keychar );
      }
    }
  }
  
  private final class NewGameMouseInput implements MouseInput {
    @ Override
    public void mousePressed( final int x, final int y, final int button ) {
      if ( buttonSelection && name.length() > 0 ) {
        startGame();
      }
    }
  }
  
  private final class NewGameMouseMotionInput implements MouseMotionInput {
    @ Override
    public void mouseMoved( final int x, final int y ) {
      if ( buttonBounds.contains( x, y ) ) {
        buttonSelection = true;
      } else {
        buttonSelection = false;
      }
    }
  }
}