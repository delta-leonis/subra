package io.leonis.subra.gui.field;

import com.googlecode.lanterna.TerminalPosition;
import io.leonis.subra.game.data.FieldLine;
import java.util.AbstractMap.SimpleEntry;
import java.util.*;
import java.util.function.Function;
import java.util.stream.*;
import lombok.AllArgsConstructor;

/**
 * @author jeroen.dejong.
 */
@AllArgsConstructor
public class FieldLines implements Function<TerminalPosition, Character> {
  private Map<TerminalPosition, Character> mapping;

  public FieldLines(final Collection<FieldLine> fieldLines) {
    this(fieldLines.stream()
        .flatMap(fieldLine ->
          IntStream.range((int)fieldLine.getXStart(), (int)fieldLine.getXEnd())
            .boxed()
            .flatMap(x -> IntStream.range((int)fieldLine.getYStart(), (int)fieldLine.getYEnd())
                                   .mapToObj(y ->
                                       new SimpleEntry<>(
                                           new TerminalPosition(x, y),
                                           fieldLine.getYStart() == fieldLine.getYEnd() ? '-' : '|'))))
        .collect(Collectors.toMap(
            SimpleEntry::getKey,
            SimpleEntry::getValue
          )));
  }

  @Override
  public Character apply(final TerminalPosition position) {
    return mapping.get(position);
  }
}
