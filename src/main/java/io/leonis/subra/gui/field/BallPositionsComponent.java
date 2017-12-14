package io.leonis.subra.gui.field;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor.RGB;
import com.googlecode.lanterna.gui2.*;
import io.leonis.subra.game.data.Ball;
import java.util.Collection;
import lombok.AllArgsConstructor;

/**
 * The class BallPositionsComponent.
 *
 * Draws an {@code 0} with an orange background on the positions of a provided {@link Collection} of
 * {@link Ball balls}.
 *
 * @author Jeroen de Jong
 */
@AllArgsConstructor
public final class BallPositionsComponent extends AbstractComponent<BallPositionsComponent> {

  /**
   * The collection of balls to draw
   */
  private final Collection<Ball> balls;

  @Override
  protected ComponentRenderer<BallPositionsComponent> createDefaultRenderer() {
    return new ComponentRenderer<BallPositionsComponent>() {
      @Override
      public TerminalSize getPreferredSize(final BallPositionsComponent component) {
        return TerminalSize.ZERO;
      }

      @Override
      public void drawComponent(
          final TextGUIGraphics graphics,
          final BallPositionsComponent component
      ) {
        graphics.setForegroundColor(new RGB(255, 255, 255));
        component.balls.forEach(ball -> {
          graphics.setBackgroundColor(new RGB(255, 165, 0));
          graphics.setCharacter((int) ball.getX(), (int) ball.getY(), '0');
        });
      }
    };
  }
}
