package io.leonis.subra.gui.field;

import com.googlecode.lanterna.TerminalPosition;
import io.leonis.subra.game.data.FieldLine;
import java.util.*;
import java.util.function.Function;
import java.util.stream.*;
import lombok.*;

/**
 * @author jeroen.dejong.
 */
@Value
@AllArgsConstructor
public class Corners implements Function<TerminalPosition, Character> {
  Set<TerminalPosition> corners;

  public Corners(final Collection<FieldLine> fieldLines) {
    this(fieldLines.stream()
          .flatMap(line ->
            Stream.of(
                new TerminalPosition((int)line.getXStart(), (int)line.getXEnd()),
                new TerminalPosition((int)line.getYStart(), (int)line.getYEnd())
            )
          ).collect(Collectors.toSet()));
  }

  @Override
  public Character apply(final TerminalPosition position) {
    return corners.stream().anyMatch(position::equals) ? '+' : null;
  }
}
