package io.leonis.subra.gui.field;

import com.googlecode.lanterna.TerminalPosition;
import io.leonis.algieba.geometry.CardinalDirection;
import io.leonis.subra.game.data.Player;
import java.util.*;
import java.util.function.Function;

/**
 * @author Jeroen de Jong
 */
public class OrientationIndicator implements Function<TerminalPosition, Character> {
  private final List<Function<TerminalPosition, Character>> orientations;

  public OrientationIndicator(final Set<Player> players) {
    this.orientations = Arrays.asList(
        new SingleOrientationIndicator(players, CardinalDirection.NORTH, new TerminalPosition(0, -1), '^'),
        new SingleOrientationIndicator(players, CardinalDirection.NORTH_EAST, new TerminalPosition(1, -1), '┐'),
        new SingleOrientationIndicator(players, CardinalDirection.EAST, new TerminalPosition(1, 0), '>'),
        new SingleOrientationIndicator(players, CardinalDirection.SOUTH_EAST, new TerminalPosition(1, 1), '┘'),
        new SingleOrientationIndicator(players, CardinalDirection.SOUTH, new TerminalPosition(0, 1), 'v'),
        new SingleOrientationIndicator(players, CardinalDirection.SOUTH_WEST, new TerminalPosition(-1, 1), '└'),
        new SingleOrientationIndicator(players, CardinalDirection.WEST, new TerminalPosition(-1, 0), '<'),
        new SingleOrientationIndicator(players, CardinalDirection.NORTH_WEST, new TerminalPosition(-1, -1), '┌'));
  }

  @Override
  public Character apply(final TerminalPosition position) {
    return this.orientations.stream()
        .map(f -> f.apply(position))
        .filter(Objects::nonNull)
        .findFirst()
        .orElse(null);
  }
}
