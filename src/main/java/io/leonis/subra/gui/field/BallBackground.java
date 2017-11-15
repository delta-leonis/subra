package io.leonis.subra.gui.field;

import com.googlecode.lanterna.*;
import io.leonis.subra.game.data.Ball;
import java.util.Set;
import java.util.function.Function;
import lombok.Value;

/**
 * @author jeroen.dejong.
 */
@Value
public class BallBackground implements Function<TerminalPosition, TextColor> {
  final Set<Ball> balls;
  final TextColor color;

  @Override
  public TextColor apply(final TerminalPosition position) {
    return balls.stream().anyMatch(ball -> position.getRow() == ball.getY() && position.getColumn() == ball.getX()) ? this.color : null;
  }
}
