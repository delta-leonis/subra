package io.leonis.subra.gui.field;

import com.googlecode.lanterna.*;
import com.googlecode.lanterna.gui2.*;
import io.leonis.subra.game.data.FieldLine;
import java.util.Collection;
import java.util.function.BiFunction;
import java.util.stream.IntStream;
import lombok.AllArgsConstructor;

/**
 * @author Jeroen de Jong
 */
@AllArgsConstructor
public class FieldLines extends AbstractComponent<FieldLines> {

  private final Collection<FieldLine> fieldLines;
  private final BiFunction<Integer, Integer, TextColor> backgroundSupplier;

  @Override
  protected ComponentRenderer<FieldLines> createDefaultRenderer() {
    return new ComponentRenderer<FieldLines>() {
      @Override
      public TerminalSize getPreferredSize(final FieldLines component) {
        return null;
      }

      @Override
      public void drawComponent(final TextGUIGraphics graphics, final FieldLines component) {
        component.fieldLines.forEach(fieldLine ->
            IntStream.range((int) fieldLine.getXStart(), (int) fieldLine.getXEnd())
                .forEach(
                    x -> IntStream.range((int) fieldLine.getYStart(), (int) fieldLine.getYEnd())
                        .forEach(y -> {
                          graphics.setBackgroundColor(backgroundSupplier.apply(x, y));
                          // TODO The determination of the character should be more obvious.
                          graphics.setCharacter(x, y, this.getCharacter(x, y, fieldLine));
                        })));
      }

      private Character getCharacter(
          final Integer x,
          final Integer y,
          final FieldLine fieldLine
      ) {
        if ((fieldLine.getYStart() == y && fieldLine.getXStart() == x) ||
            (fieldLine.getYEnd() == y && fieldLine.getXEnd() == x)) {
          return '+';
        }
        return fieldLine.getYStart() == fieldLine.getYEnd() ? '|' : '–';
      }
    };
  }
}
