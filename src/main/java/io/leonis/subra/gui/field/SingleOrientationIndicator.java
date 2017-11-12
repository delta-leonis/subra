package io.leonis.subra.gui.field;

import com.googlecode.lanterna.TerminalPosition;
import io.leonis.algieba.geometry.CardinalDirection;
import io.leonis.subra.game.data.Player;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;

/**
 * @author Jeroen de Jong
 */
@AllArgsConstructor
public class SingleOrientationIndicator implements Function<TerminalPosition, Character> {
  private final Character character;
  private final List<TerminalPosition> positions;

  public SingleOrientationIndicator(
      final Set<Player> players,
      final CardinalDirection cardinalDirection,
      final TerminalPosition offset,
      final Character character
  ){
    this(character, players.stream()
        .filter(r -> CardinalDirection.from(r.getOrientation()).equals(cardinalDirection))
        .map(robot -> offset.withRelative((int)robot.getX(), (int)robot.getY()))
        .collect(Collectors.toList()));
  }

  @Override
  public Character apply(final TerminalPosition position) {
    return this.positions.contains(position) ? this.character : null;
  }
}
