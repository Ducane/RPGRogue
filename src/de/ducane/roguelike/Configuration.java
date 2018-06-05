package de.ducane.roguelike;

import de.androbin.json.*;
import java.awt.*;

public final class Configuration {
  private static final XObject CONFIG = XUtil.readJSON( "config.json" ).get().asObject();
  
  public static final class window_ {
    private static final XObject CONFIG_WINDOW = CONFIG.get( "window" ).asObject();
    
    public static final float SCALE = CONFIG_WINDOW.get( "scale" ).asFloat();
    public static final boolean RESIZABLE = CONFIG_WINDOW.get( "resizable" ).asBoolean();
    public static final int FPS = CONFIG_WINDOW.get( "fps" ).asInt();
    public static final String TITLE = CONFIG_WINDOW.get( "title" ).asString();
  }
  
  public static final class gui_ {
    private static final XObject CONFIG_GUI = CONFIG.get( "gui" ).asObject();
    
    public static final float CROSSFADE_SWITCH = CONFIG_GUI.get( "crossfade_switch" ).asFloat();
    
    public static final class menu_ {
      private static final XObject CONFIG_MENU = CONFIG_GUI.get( "menu" ).asObject();
      
      public static final Color BACKGROUND_COLOR = CONFIG_MENU.get( "background_color" ).asColor();
      
      public static final Color BACKGROUND_DECO_COLOR = CONFIG_MENU.get( "background_deco_color" )
          .asColor();
      public static final float BACKGROUND_DECO_Y_OFFSET = CONFIG_MENU
          .get( "background_deco_y_offset" ).asFloat();
      
      public static final String[] BUTTON_LABELS = CONFIG_MENU.get( "button_labels" )
          .asStringArray();
      public static final Color BUTTON_COLOR_LABEL = CONFIG_MENU.get( "button_color_label" )
          .asColor();
      
      public static final Color BUTTON_COLOR_BORDER = CONFIG_MENU.get( "button_color_border" )
          .asColor();
      
      public static final float BUTTON_X = CONFIG_MENU.get( "button_x" ).asFloat();
      public static final float BUTTON_Y = CONFIG_MENU.get( "button_y" ).asFloat();
      public static final float BUTTON_DY = CONFIG_MENU.get( "button_dy" ).asFloat();
      public static final float BUTTON_WIDTH = CONFIG_MENU.get( "button_width" ).asFloat();
      public static final float BUTTON_HEIGHT = CONFIG_MENU.get( "button_height" ).asFloat();
      
      public static final String BUTTON_FONT_NAME = CONFIG_MENU.get( "button_font_name" )
          .asString();
      public static final int BUTTON_FONT_STYLE = CONFIG_MENU.get( "button_font_style" ).asInt();
      public static final float BUTTON_FONT_SIZE = CONFIG_MENU.get( "button_font_size" ).asFloat();
      
      public static final float BUTTON_BORDER_THICKNESS = CONFIG_MENU
          .get( "button_border_thickness" ).asFloat();
      public static final float BUTTON_BORDER_THICKNESS_SELECTED = CONFIG_MENU
          .get( "button_border_thickness_selected" ).asFloat();
      
      public static final float CURSOR_SPEED = CONFIG_MENU.get( "cursor_speed" ).asFloat();
      
      public static final Color CURSOR_COLOR = CONFIG_MENU.get( "cursor_color" ).asColor();
      public static final Color CURSOR_COLOR_BORDER = CONFIG_MENU.get( "cursor_color_border" )
          .asColor();
      
      public static final float CURSOR_BORDER_THICKNESS = CONFIG_MENU
          .get( "cursor_border_thickness" ).asFloat();
      
      public static final int RETURN_TIME = CONFIG_MENU.get( "return_time" ).asInt();
      
      public static final float TITLE_X = CONFIG_MENU.get( "title_x" ).asFloat();
      public static final float TITLE_Y = CONFIG_MENU.get( "title_y" ).asFloat();
      public static final float TITLE_WIDTH = CONFIG_MENU.get( "title_width" ).asFloat();
      public static final float TITLE_HEIGHT = CONFIG_MENU.get( "title_height" ).asFloat();
    }
    
    public static final class intro_ {
      private static final XObject CONFIG_INTRO = CONFIG_GUI.get( "intro" ).asObject();
      
      public static final Color BACKGROUND_COLOR = CONFIG_INTRO.get( "background_color" ).asColor();
      public static final Color FONT_COLOR = CONFIG_INTRO.get( "font_color" ).asColor();
      
      public static final String[] TEXT = CONFIG_INTRO.get( "text" ).asStringArray();
      
      public static final float TEXT_SPEED = CONFIG_INTRO.get( "char_speed" ).asFloat();
      
      public static final float TEXT_DX = CONFIG_INTRO.get( "text_dx" ).asFloat();
      
      public static final String TEXT_FONT_NAME = CONFIG_INTRO.get( "text_font_name" ).asString();
      public static final int TEXT_FONT_STYLE = CONFIG_INTRO.get( "text_font_style" ).asInt();
      public static final float TEXT_FONT_SIZE = CONFIG_INTRO.get( "text_font_size" ).asFloat();
    }
  }
  
  public static final class level_ {
    private static final XObject CONFIG_LEVEL = CONFIG.get( "level" ).asObject();
    
    public static final class tile_ {
      private static final XObject CONFIG_TILE = CONFIG_LEVEL.get( "tile" ).asObject();
      
      public static final int TILE_SIZE = CONFIG_TILE.get( "tile_size" ).asInt();
    }
  }
}