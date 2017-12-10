package io.leonis.subra.gui.field;

import static io.leonis.algieba.geometry.CardinalDirection.*;

import com.google.common.collect.ImmutableMap;
import com.googlecode.lanterna.*;
import com.googlecode.lanterna.TextColor.*;
import com.googlecode.lanterna.gui2.*;
import io.leonis.algieba.geometry.CardinalDirection;
import io.leonis.subra.game.data.Player;
import java.util.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import java.util.function.*;
import lombok.AllArgsConstructor;

/**
 * @author Jeroen de Jong
 */
@AllArgsConstructor
public class OrientationIndicators extends AbstractComponent<OrientationIndicators> {
  private final Set<Player> players;
  private final BiFunction<Integer, Integer, TextColor> backgroundSupplier;
  private final Map<CardinalDirection, Entry<TerminalPosition, Character>> mapping =
    ImmutableMap.<CardinalDirection, Entry<TerminalPosition, Character>>builder()
      .put(NORTH, new SimpleEntry<>(new TerminalPosition( 0, -1), '↑'))
      .put(NORTH_EAST,  new SimpleEntry<>(new TerminalPosition( 1, -1), '↗'))
      .put(EAST, new SimpleEntry<>(new TerminalPosition( 1,  0), '→'))
      .put(SOUTH_EAST, new SimpleEntry<>(new TerminalPosition( 1,  1), '↘'))
      .put(SOUTH, new SimpleEntry<>(new TerminalPosition( 0,  1), '↓'))
      .put(SOUTH_WEST, new SimpleEntry<>(new TerminalPosition(-1,  1), '↙'))
      .put(WEST, new SimpleEntry<>(new TerminalPosition(-1,  0), '←'))
      .put(NORTH_WEST, new SimpleEntry<>(new TerminalPosition(-1, -1), '↖'))
      .build();

  @Override
  protected ComponentRenderer<OrientationIndicators> createDefaultRenderer() {
    return new ComponentRenderer<OrientationIndicators>() {
      @Override
      public TerminalSize getPreferredSize(final OrientationIndicators component) {
        // TODO This should be determined based on the set of players
        return new TerminalSize(10, 20);
      }

      @Override
      public void drawComponent(final TextGUIGraphics graphics,
          final OrientationIndicators component) {
        graphics.setForegroundColor(new RGB(255, 255, 255));
        players.forEach(player -> {
            final Entry<TerminalPosition, Character> entry = mapping.get(player.getCardinalDirection());
          final TerminalPosition position = entry.getKey().withRelative((int)player.getX(), (int)player.getY());
            graphics.setBackgroundColor(backgroundSupplier.apply(position.getRow(), position.getColumn()));
            graphics.setCharacter(position, entry.getValue());
        });
      }
    };
  }
}
