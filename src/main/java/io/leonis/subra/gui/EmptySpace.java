package io.leonis.subra.gui;

import com.googlecode.lanterna.*;
import com.googlecode.lanterna.gui2.*;
import java.util.function.BiFunction;
import java.util.stream.IntStream;
import lombok.AllArgsConstructor;

/**
 * The class EmptySpace.
 *
 * Draws an rectangle the same size as it's parent a dynamic color.
 *
 * @author Jeroen de Jong
 */
@AllArgsConstructor
public class EmptySpace extends AbstractComponent<EmptySpace> {

  /**
   * Function to determine background color at a specific position
   */
  private final BiFunction<Integer, Integer, TextColor> backgroundSupplier;

  @Override
  protected ComponentRenderer<EmptySpace> createDefaultRenderer() {
    return new ComponentRenderer<EmptySpace>() {
      @Override
      public TerminalSize getPreferredSize(final EmptySpace component) {
        return component.getParent().getPreferredSize();
      }

      @Override
      public void drawComponent(final TextGUIGraphics graphics, final EmptySpace component) {
        IntStream.range(0, this.getPreferredSize(component).getColumns()).forEach(x ->
            IntStream.range(0, this.getPreferredSize(component).getRows()).forEach(y -> {
              graphics.setBackgroundColor(backgroundSupplier.apply(x, y));
              graphics.setCharacter(x, y, ' ');
            }));
      }
    };
  }
}
