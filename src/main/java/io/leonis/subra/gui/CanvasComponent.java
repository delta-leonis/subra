package io.leonis.subra.gui;

import com.googlecode.lanterna.*;
import com.googlecode.lanterna.gui2.*;
import java.util.function.BiFunction;
import java.util.stream.IntStream;
import lombok.AllArgsConstructor;

/**
 * The class CanvasComponent.
 *
 * Draws an rectangle the same size as it's parent a dynamic color.
 *
 * @author Jeroen de Jong
 */
@AllArgsConstructor
public class CanvasComponent extends AbstractComponent<CanvasComponent> {

  /**
   * Function to determine background color at a specific position
   */
  private final BiFunction<Integer, Integer, TextColor> backgroundSupplier;

  @Override
  protected ComponentRenderer<CanvasComponent> createDefaultRenderer() {
    return new ComponentRenderer<CanvasComponent>() {
      @Override
      public TerminalSize getPreferredSize(final CanvasComponent component) {
        return new TerminalSize(0, 0);
      }

      @Override
      public void drawComponent(final TextGUIGraphics graphics, final CanvasComponent component) {
        IntStream.range(0, this.getPreferredSize(component).getColumns()).forEach(x ->
            IntStream.range(0, this.getPreferredSize(component).getRows()).forEach(y -> {
              graphics.setBackgroundColor(backgroundSupplier.apply(x, y));
              graphics.setCharacter(x, y, ' ');
            }));
      }
    };
  }
}
