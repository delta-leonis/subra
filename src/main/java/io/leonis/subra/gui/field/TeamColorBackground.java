package io.leonis.subra.gui.field;

import com.googlecode.lanterna.*;
import io.leonis.subra.game.data.Player;
import java.util.Set;
import java.util.function.Function;
import lombok.AllArgsConstructor;

/**
 * @author Jeroen de Jong
 */
@AllArgsConstructor
public class TeamColorBackground implements Function<TerminalPosition, TextColor> {
  private final Set<Player> Players;
  private final TextColor color;

  @Override
  public TextColor apply(final TerminalPosition position) {
    return this.Players.stream()
        .anyMatch(robot -> position.getRow() == robot.getY() &&
                          position.getColumn() == robot.getX()) ? this.color : null;
  }
}
