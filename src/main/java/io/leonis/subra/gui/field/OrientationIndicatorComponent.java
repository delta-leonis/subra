package io.leonis.subra.gui.field;

import static io.leonis.algieba.geometry.CardinalDirection.*;

import com.google.common.collect.ImmutableMap;
import com.googlecode.lanterna.*;
import com.googlecode.lanterna.TextColor.RGB;
import com.googlecode.lanterna.gui2.*;
import io.leonis.algieba.geometry.CardinalDirection;
import io.leonis.subra.game.data.Player;
import java.util.AbstractMap.SimpleEntry;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.BiFunction;
import lombok.AllArgsConstructor;

/**
 * The class OrientationIndicatorComponent.
 *
 * Draws an arrow character around the original player location in the orientation the player is
 * facing.
 *
 * @author Jeroen de Jong
 */
@AllArgsConstructor
public final class OrientationIndicatorComponent extends
    AbstractComponent<OrientationIndicatorComponent> {

  /**
   * The set of players to draw the indicator for
   */
  private final Collection<Player> players;
  /**
   * Function to determine background color at a specific position
   */
  private final BiFunction<Integer, Integer, TextColor> backgroundSupplier;
  /**
   * Mapping of {@link CardinalDirection} to a {@link Character} and its offset
   */
  private final Map<CardinalDirection, Entry<TerminalPosition, Character>> mapping =
      ImmutableMap.<CardinalDirection, Entry<TerminalPosition, Character>>builder()
          .put(NORTH, new SimpleEntry<>(new TerminalPosition(0, -1), '↑'))
          .put(NORTH_EAST, new SimpleEntry<>(new TerminalPosition(1, -1), '↗'))
          .put(EAST, new SimpleEntry<>(new TerminalPosition(1, 0), '→'))
          .put(SOUTH_EAST, new SimpleEntry<>(new TerminalPosition(1, 1), '↘'))
          .put(SOUTH, new SimpleEntry<>(new TerminalPosition(0, 1), '↓'))
          .put(SOUTH_WEST, new SimpleEntry<>(new TerminalPosition(-1, 1), '↙'))
          .put(WEST, new SimpleEntry<>(new TerminalPosition(-1, 0), '←'))
          .put(NORTH_WEST, new SimpleEntry<>(new TerminalPosition(-1, -1), '↖'))
          .build();

  @Override
  protected ComponentRenderer<OrientationIndicatorComponent> createDefaultRenderer() {
    return new ComponentRenderer<OrientationIndicatorComponent>() {
      @Override
      public TerminalSize getPreferredSize(final OrientationIndicatorComponent component) {
        return component.getParent().getPreferredSize();
      }

      @Override
      public void drawComponent(
          final TextGUIGraphics graphics,
          final OrientationIndicatorComponent component
      ) {
        graphics.setForegroundColor(new RGB(255, 255, 255));
        component.players.forEach(player -> {
          final Entry<TerminalPosition, Character> entry = mapping
              .get(player.getCardinalDirection());
          final TerminalPosition position = entry.getKey()
              .withRelative((int) player.getX(), (int) player.getY());
          graphics.setBackgroundColor(
              component.backgroundSupplier.apply(position.getRow(), position.getColumn()));
          graphics.setCharacter(position, entry.getValue());
        });
      }
    };
  }
}
