package de.ducane.roguelike.screen;

import static de.ducane.roguelike.Configuration.gui_.menu_.*;
import de.androbin.game.*;
import de.androbin.gfx.transition.*;
import de.androbin.math.util.floats.*;
import de.androbin.math.util.ints.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

public class MenuScreen extends Screen {
  private final ResourceManager<String> rm;
  
  private int selection;
  
  private int backgroundDecoYOffset;
  
  private Stroke cursorStroke;
  
  private Rectangle2D[] buttonBounds;
  private Font buttonFont;
  private Stroke buttonStroke;
  private Stroke buttonStrokeSelected;
  
  private int buttonX;
  private int buttonY;
  private int buttonDy;
  private int buttonWidth;
  private int buttonHeight;
  
  private int titleX;
  private int titleY;
  private int titleWidth;
  private int titleHeight;
  
  private float passedTime;
  private float cursorProgress;
  
  private boolean cursorDirection;
  
  public MenuScreen( final Game game ) {
    super( game );
    
    this.rm = new DefaultResourceManager<>();
  }
  
  @ Override
  public KeyListener getKeyListener() {
    return new MenuKeyListener();
  }
  
  @ Override
  public MouseListener getMouseListener() {
    return new MenuMouseListener();
  }
  
  @ Override
  public MouseMotionListener getMouseMotionListener() {
    return new MenuMouseMotionListener();
  }
  
  @ Override
  public void onPaused() {
    passedTime = 0f;
    
    super.onPaused();
  }
  
  @ Override
  public void onResized( final int width, final int height ) {
    backgroundDecoYOffset = (int) ( BACKGROUND_DECO_Y_OFFSET * getHeight() );
    
    buttonX = (int) ( BUTTON_X * getWidth() );
    buttonY = (int) ( BUTTON_Y * getHeight() );
    buttonDy = (int) ( BUTTON_DY * getHeight() );
    buttonWidth = (int) ( BUTTON_WIDTH * getWidth() );
    buttonHeight = (int) ( BUTTON_HEIGHT * getHeight() );
    buttonFont = new Font( BUTTON_FONT_NAME, BUTTON_FONT_STYLE,
        (int) ( BUTTON_FONT_SIZE * getHeight() ) );
    buttonStroke = new BasicStroke( BUTTON_BORDER_THICKNESS * getHeight() );
    buttonStrokeSelected = new BasicStroke( BUTTON_BORDER_THICKNESS_SELECTED * getHeight() );
    
    buttonBounds = new Rectangle2D[ BUTTON_LABELS.length ];
    
    for ( int i = 0; i < buttonBounds.length; i++ ) {
      buttonBounds[ i ] = new Rectangle2D.Float( buttonX, buttonY + buttonDy * i,
          buttonWidth, buttonHeight );
    }
    
    titleX = (int) ( TITLE_X * getWidth() );
    titleY = (int) ( TITLE_Y * getHeight() );
    titleWidth = (int) ( TITLE_WIDTH * getWidth() );
    titleHeight = (int) ( TITLE_HEIGHT * getHeight() );
    
    cursorStroke = new BasicStroke( CURSOR_BORDER_THICKNESS * getHeight() );
  }
  
  @ Override
  public void onStarted() {
    rm.loadImage( "Rogue", "menu/Rogue.png" );
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
    
    g.drawLine( 0, backgroundDecoYOffset, getWidth(), backgroundDecoYOffset );
    g.drawLine( 0, getHeight() - backgroundDecoYOffset, getWidth(),
        getHeight() - backgroundDecoYOffset );
  }
  
  private void renderCursor( final Graphics2D g ) {
    // TODO replace with Polygon object
    
    final int[] x = { buttonX - buttonWidth / 5,
        buttonX - buttonWidth / 5,
        buttonX - buttonWidth / 12 };
    final int[] y = { buttonY + buttonHeight / 5 + buttonDy * selection,
        buttonY + buttonHeight - buttonHeight / 5 + buttonDy * selection,
        buttonY + buttonHeight / 2 + buttonDy * selection };
    
    final int[] newX = { buttonX - (int) ( buttonWidth / 5.5f ),
        buttonX - (int) ( buttonWidth / 5.5f ),
        buttonX - (int) ( buttonWidth / 7.5f ) };
    final int[] newY = { buttonY + (int) ( buttonHeight / 2.5f ) + buttonDy * selection,
        buttonY + buttonHeight - (int) ( buttonHeight / 2.5f ) + buttonDy * selection,
        buttonY + buttonHeight / 2 + buttonDy * selection };
    
    final int[] interpolX = { (int) FloatMathUtil.inter( x[ 0 ], cursorProgress, newX[ 0 ] ),
        (int) FloatMathUtil.inter( x[ 1 ], cursorProgress, newX[ 1 ] ),
        (int) FloatMathUtil.inter( x[ 2 ], cursorProgress, newX[ 2 ] ) };
    
    final int[] interpolY = { (int) FloatMathUtil.inter( y[ 0 ], cursorProgress, newY[ 0 ] ),
        (int) FloatMathUtil.inter( y[ 1 ], cursorProgress, newY[ 1 ] ),
        (int) FloatMathUtil.inter( y[ 2 ], cursorProgress, newY[ 2 ] )
    };
    
    g.setColor( CURSOR_COLOR );
    g.fillPolygon( interpolX, interpolY, 3 );
    
    g.setColor( CURSOR_COLOR_BORDER );
    g.setStroke( cursorStroke );
    g.drawPolygon( interpolX, interpolY, 3 );
  }
  
  private void renderButtons( final Graphics2D g ) {
    for ( int i = 0; i < BUTTON_LABELS.length; i++ ) {
      g.setStroke( selection == i ? buttonStrokeSelected : buttonStroke );
      g.setColor( BUTTON_COLOR_BORDER );
      g.drawRect( buttonX, buttonY + buttonDy * i, buttonWidth, buttonHeight );
      
      final String label = BUTTON_LABELS[ i ];
      
      final FontMetrics fm = g.getFontMetrics( buttonFont );
      final Rectangle2D b = fm.getStringBounds( label, g );
      
      final int dx = (int) ( buttonWidth - b.getWidth() ) / 2;
      final int dy = (int) ( buttonHeight - b.getHeight() ) / 2;
      
      g.setColor( BUTTON_COLOR_LABEL );
      g.setFont( buttonFont );
      g.drawString( label, buttonX + dx, buttonY + buttonDy * i + dy + fm.getAscent() );
    }
    
  }
  
  private void renderTitle( final Graphics2D g ) {
    g.drawImage( rm.getImage( "Rogue" ), titleX, titleY, titleWidth, titleHeight, null );
  }
  
  private void runCommand( final int selection ) {
    switch ( selection ) {
      case 0 :
        game.gsm.crossfadeSwitch( new NewGameScreen( game ), ColorCrossfade.BLACK, 1f );
        break;
      case 1 :
        game.gsm.close();
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
      game.gsm.crossfadeClose( ColorCrossfade.BLACK, 1f );
    }
  }
  
  private class MenuKeyListener extends KeyAdapter {
    @ Override
    public void keyPressed( final KeyEvent event ) {
      switch ( event.getKeyCode() ) {
        case KeyEvent.VK_ENTER :
        case KeyEvent.VK_SPACE :
          runCommand( selection );
          break;
        
        case KeyEvent.VK_UP :
          shiftSelection( -1 );
          break;
        case KeyEvent.VK_DOWN :
          shiftSelection( 1 );
          break;
      }
    }
    
    @ Override
    public void keyReleased( final KeyEvent event ) {
    }
  }
  
  private class MenuMouseListener extends MouseAdapter {
    @ Override
    public void mousePressed( final MouseEvent event ) {
      runCommand( selection );
    }
  }
  
  private class MenuMouseMotionListener extends MouseMotionAdapter {
    @ Override
    public void mouseMoved( final MouseEvent event ) {
      for ( int i = 0; i < buttonBounds.length; i++ ) {
        if ( buttonBounds[ i ].contains( event.getPoint() ) ) {
          selection = i;
        }
      }
    }
  }
}