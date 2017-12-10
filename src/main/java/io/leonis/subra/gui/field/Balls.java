package io.leonis.subra.gui.field;

import com.googlecode.lanterna.*;
import com.googlecode.lanterna.TextColor.RGB;
import com.googlecode.lanterna.gui2.*;
import io.leonis.subra.game.data.Ball;
import java.util.Set;
import lombok.*;

/**
 * @author Jeroen de Jong
 */
@AllArgsConstructor
public final class Balls extends AbstractComponent<Balls> {
  final Set<Ball> balls;

  @Override
  protected ComponentRenderer<Balls> createDefaultRenderer() {
    return new ComponentRenderer<Balls>() {
      @Override
      public TerminalSize getPreferredSize(final Balls component) {
        // TODO This should be determined based on the set of balls
        return new TerminalSize(10, 20);
      }

      @Override
      public void drawComponent(final TextGUIGraphics graphics, final Balls component) {
        graphics.setForegroundColor(new RGB(255, 255, 255));
        component.balls.forEach(ball -> {
          graphics.setBackgroundColor(new RGB(255,165,0));
          graphics.setCharacter((int)ball.getX(), (int)ball.getY(), '0');
        });
      }
    };
  }
}
