package de.ducane.roguelike.screen;

import static de.androbin.gfx.util.GraphicsUtil.*;
import static de.androbin.math.util.floats.FloatMathUtil.*;
import static de.ducane.roguelike.Configuration.gui_.intro_.BACKGROUND_COLOR;
import static de.ducane.roguelike.Configuration.gui_.menu_.*;
import de.androbin.math.util.ints.*;
import de.androbin.screen.*;
import de.androbin.screen.transit.*;
import de.androbin.shell.*;
import de.androbin.shell.gfx.*;
import de.androbin.shell.input.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

public final class MenuScreen extends BasicShell implements AWTGraphics {
  private final SmoothScreenManager<AWTTransition> screens;
  
  private final ResourceManager<String> rm;
  
  private int selection;
  
  private float backgroundDecoDY;
  
  private Stroke cursorStroke;
  
  private Rectangle2D.Float[] buttonBounds;
  private Font buttonFont;
  private Stroke buttonStroke;
  private Stroke buttonStrokeSelected;
  
  private Rectangle2D.Float titleBounds;
  
  private float passedTime;
  private float cursorProgress;
  
  private boolean cursorDirection;
  
  public MenuScreen( final SmoothScreenManager<AWTTransition> screens ) {
    this.screens = screens;
    
    addKeyInput( new MenuKeyInput() );
    addMouseInput( new MenuMouseInput() );
    addMouseMotionInput( new MenuMouseMotionInput() );
    
    this.rm = new SimpleResourceManager<>();
  }
  
  @ Override
  public void onPaused() {
    passedTime = 0f;
    
    super.onPaused();
  }
  
  @ Override
  protected void onResized( final int width, final int height ) {
    backgroundDecoDY = BACKGROUND_DECO_Y_OFFSET * height;
    
    buttonFont = new Font( BUTTON_FONT_NAME, BUTTON_FONT_STYLE,
        (int) ( BUTTON_FONT_SIZE * height ) );
    buttonStroke = new BasicStroke( BUTTON_BORDER_THICKNESS * height );
    buttonStrokeSelected = new BasicStroke( BUTTON_BORDER_THICKNESS_SELECTED * height );
    
    final float buttonX = BUTTON_X * width;
    final float buttonY = BUTTON_Y * height;
    final float buttonDY = BUTTON_DY * height;
    final float buttonWidth = BUTTON_WIDTH * width;
    final float buttonHeight = BUTTON_HEIGHT * height;
    
    buttonBounds = new Rectangle2D.Float[ BUTTON_LABELS.length ];
    
    for ( int i = 0; i < buttonBounds.length; i++ ) {
      buttonBounds[ i ] = new Rectangle2D.Float(
          buttonX, buttonY + buttonDY * i, buttonWidth, buttonHeight );
    }
    
    titleBounds = new Rectangle2D.Float(
        TITLE_X * width, TITLE_Y * height, TITLE_WIDTH * width, TITLE_HEIGHT * height );
    
    cursorStroke = new BasicStroke( CURSOR_BORDER_THICKNESS * getHeight() );
  }
  
  @ Override
  public void onStarted() {
    rm.loadImage( "Rogue", "menu/Rogue.png" );
  }
  
  @ Override
  public void onStopped() {
    rm.release();
  }
  
  @ Override
  public void render( final Graphics2D g ) {
    renderBackground( g );
    renderButtons( g );
    renderTitle( g );
    renderCursor( g );
  }
  
  private void renderBackground( final Graphics2D g ) {
    g.setColor( BACKGROUND_COLOR );
    g.fillRect( 0, 0, getWidth(), getHeight() );
    
    g.setColor( BACKGROUND_DECO_COLOR );
    
    drawLine( g, 0f, backgroundDecoDY, getWidth(), backgroundDecoDY );
    drawLine( g, 0f, getHeight() - backgroundDecoDY, getWidth(),
        getHeight() - backgroundDecoDY );
  }
  
  private void renderCursor( final Graphics2D g ) {
    final Rectangle2D.Float button = buttonBounds[ selection ];
    
    // TODO replace with Polygon object
    
    final float[] x = {
        button.x - button.width / 5,
        button.x - button.width / 5,
        button.x - button.width / 12 };
    final float[] y = {
        button.y + button.height / 5,
        button.y + button.height - button.height / 5,
        button.y + button.height / 2 };
    
    final float[] newX = {
        button.x - button.width / 5.5f,
        button.x - button.width / 5.5f,
        button.x - button.width / 7.5f };
    final float[] newY = {
        button.y + button.height / 2.5f,
        button.y + button.height - button.height / 2.5f,
        button.y + button.height / 2 };
    
    final int[] interX = {
        (int) inter( x[ 0 ], cursorProgress, newX[ 0 ] ),
        (int) inter( x[ 1 ], cursorProgress, newX[ 1 ] ),
        (int) inter( x[ 2 ], cursorProgress, newX[ 2 ] ) };
    
    final int[] interY = {
        (int) inter( y[ 0 ], cursorProgress, newY[ 0 ] ),
        (int) inter( y[ 1 ], cursorProgress, newY[ 1 ] ),
        (int) inter( y[ 2 ], cursorProgress, newY[ 2 ] ) };
    
    g.setColor( CURSOR_COLOR );
    g.fillPolygon( interX, interY, 3 );
    
    g.setColor( CURSOR_COLOR_BORDER );
    g.setStroke( cursorStroke );
    g.drawPolygon( interX, interY, 3 );
  }
  
  private void renderButtons( final Graphics2D g ) {
    for ( int i = 0; i < BUTTON_LABELS.length; i++ ) {
      final Rectangle2D.Float button = buttonBounds[ i ];
      
      g.setStroke( selection == i ? buttonStrokeSelected : buttonStroke );
      g.setColor( BUTTON_COLOR_BORDER );
      drawRect( g, button );
      
      final String label = BUTTON_LABELS[ i ];
      
      final FontMetrics fm = g.getFontMetrics( buttonFont );
      final Rectangle2D.Float bounds = (Rectangle2D.Float) fm.getStringBounds( label, g );
      
      final float dx = ( button.width - bounds.width ) * 0.5f;
      final float dy = ( button.height - bounds.height ) * 0.5f;
      
      g.setColor( BUTTON_COLOR_LABEL );
      g.setFont( buttonFont );
      g.drawString( label, button.x + dx, button.y + dy + fm.getAscent() );
    }
  }
  
  private void renderTitle( final Graphics2D g ) {
    drawImage( g, rm.getImage( "Rogue" ), titleBounds );
  }
  
  private void runCommand( final int selection ) {
    switch ( selection ) {
      case 0:
        screens.fadeSwitchTo( new NewGameScreen( screens ),
            new AWTColorCrossfade( Color.BLACK, 0.5f, 1f ) );
        break;
      case 1:
        System.exit( 0 );
        break;
    }
  }
  
  private void shiftSelection( final int shift ) {
    selection = IntMathUtil.mod( selection + shift, BUTTON_LABELS.length );
  }
  
  @ Override
  public void update( final float delta ) {
    if ( cursorProgress >= 1f ) {
      cursorDirection = true;
    } else if ( cursorProgress <= 0f ) {
      cursorDirection = false;
    }
    
    cursorProgress += cursorDirection ? delta * -CURSOR_SPEED : delta * CURSOR_SPEED;
    
    passedTime += delta;
    
    if ( passedTime > RETURN_TIME ) {
      screens.fadeClose( new AWTColorCrossfade( Color.BLACK, 0.5f, 1f ) );
    }
  }
  
  private final class MenuKeyInput implements KeyInput {
    @ Override
    public void keyPressed( final int keycode ) {
      switch ( keycode ) {
        case KeyEvent.VK_ENTER:
        case KeyEvent.VK_SPACE:
          runCommand( selection );
          break;
        
        case KeyEvent.VK_UP:
          shiftSelection( -1 );
          break;
        case KeyEvent.VK_DOWN:
          shiftSelection( 1 );
          break;
      }
    }
  }
  
  private final class MenuMouseInput implements MouseInput {
    @ Override
    public void mousePressed( final int x, final int y, final int button ) {
      runCommand( selection );
    }
  }
  
  private final class MenuMouseMotionInput implements MouseMotionInput {
    @ Override
    public void mouseMoved( final int x, final int y ) {
      for ( int i = 0; i < buttonBounds.length; i++ ) {
        if ( buttonBounds[ i ].contains( x, y ) ) {
          selection = i;
        }
      }
    }
  }
}