package io.leonis.subra.gui.field;

import com.googlecode.lanterna.*;
import com.googlecode.lanterna.gui2.*;
import io.leonis.subra.game.data.FieldLine;
import java.util.Collection;
import java.util.function.BiFunction;
import java.util.stream.IntStream;
import lombok.AllArgsConstructor;

/**
 * The class FieldLinesComponent.
 *
 * Draws vertical and horizontal {@link FieldLine field lines}. Draws a + at the begin and end of
 * every line.
 *
 * @author Jeroen de Jong
 */
@AllArgsConstructor
public final class FieldLinesComponent extends AbstractComponent<FieldLinesComponent> {

  /**
   * Collection of lines to draw
   */
  private final Collection<FieldLine> fieldLines;
  /**
   * Function to determine background color at a specific position
   */
  private final BiFunction<Integer, Integer, TextColor> backgroundSupplier;

  @Override
  protected ComponentRenderer<FieldLinesComponent> createDefaultRenderer() {
    return new ComponentRenderer<FieldLinesComponent>() {
      @Override
      public TerminalSize getPreferredSize(final FieldLinesComponent component) {
        return component.getParent().getPreferredSize();
      }

      @Override
      public void drawComponent(final TextGUIGraphics graphics,
          final FieldLinesComponent component) {
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
