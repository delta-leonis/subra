package io.leonis.subra.game.data;

import java.awt.Color;

/**
 * The Enum TeamColor.
 *
 * This enumeration represents the possible colors a team can sport in a Small Size League
 * sequence.
 *
 * @author Jeroen de Jong
 * @author Rimon Oz
 */
public enum TeamColor {
  /**
   * Blue team color.
   */
  BLUE(Color.BLUE),
  /**
   * Yellow team color.
   */
  YELLOW(Color.YELLOW),
  /**
   * None team color.
   */
  NONE(Color.BLACK);

  /**
   * The color as a {@link Color}.
   */
  private Color color;

  /**
   * Constructs a new color.
   *
   * @param color The {@link Color} to use.
   */
  TeamColor(final Color color) {
    this.color = color;
  }

  /**
   * Converts the {@link TeamColor} to an actual {@link Color}.
   *
   * @return {@link Color} describing {@link TeamColor}
   */
  public Color toColor() {
    return this.color;
  }
}