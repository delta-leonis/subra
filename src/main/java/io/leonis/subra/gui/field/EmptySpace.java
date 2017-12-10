package io.leonis.subra.gui.field;

import com.googlecode.lanterna.*;
import com.googlecode.lanterna.gui2.*;
import java.util.function.BiFunction;
import java.util.stream.IntStream;
import lombok.AllArgsConstructor;

/**
 * @author Jeroen de Jong
 */
@AllArgsConstructor
public class EmptySpace extends AbstractComponent<EmptySpace> {
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
