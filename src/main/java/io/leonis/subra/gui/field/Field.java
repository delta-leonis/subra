package io.leonis.subra.gui.field;

import com.googlecode.lanterna.*;
import com.googlecode.lanterna.TextColor.RGB;
import com.googlecode.lanterna.gui2.*;
import java.awt.Color;
import java.util.*;
import java.util.function.Function;
import java.util.stream.IntStream;
import lombok.Value;
import org.nd4j.linalg.api.ndarray.INDArray;

/**
 * @author Jeroen de Jong
 */
@Value
public class Field extends AbstractComponent<Field> {
  private final INDArray data;
  private final List<Function<TerminalPosition, TextColor>> backgrounds;
  private final List<Function<TerminalPosition, Character>> characters;

  @Override
  protected ComponentRenderer<Field> createDefaultRenderer() {
    return new ComponentRenderer<Field>() {
      @Override
      public TerminalSize getPreferredSize(final Field component) {
        return new TerminalSize(component.getData().columns(), component.getData().rows());
      }

      @Override
      public void drawComponent(final TextGUIGraphics graphics, final Field component) {
        IntStream.range(0, component.getData().rows())
          .forEach(x ->
            IntStream.range(0, component.getData().columns())
                .mapToObj(y -> new TerminalPosition(x, y))
                .forEach(position -> {
                  final TextColor background = component.getBackgrounds().stream()
                      .map(b -> b.apply(position))
                      .filter(Objects::nonNull)
                      .findFirst()
                      .orElse(new RGB(0, 0, 0));
                  graphics.setBackgroundColor(background);
                  graphics.setForegroundColor(this.visibleTextColor(background));

                  graphics.setCharacter(position,
                      component.getCharacters().stream()
                          .map(m -> m.apply(position))
                        .filter(Objects::nonNull)
                        .findFirst()
                        .orElse(' '));
                })
          );
      }

      private TextColor visibleTextColor(final TextColor c){
        if(Color.RGBtoHSB(c.toColor().getRed(), c.toColor().getGreen(), c.toColor().getBlue(), null)[2] > 0.5)
          return new RGB(0, 0, 0);
        else
          return new TextColor.RGB(255, 255, 255);
      }
    };
  }
}
