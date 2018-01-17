package io.leonis.subra.gui.field;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor.*;
import com.googlecode.lanterna.gui2.*;
import io.leonis.subra.game.data.*;
import java.util.Collection;
import lombok.AllArgsConstructor;

/**
 * The class PlayerPositionsComponent.
 *
 * Draws all provided player ids with the {@link TeamColor} as background.
 *
 * @author Jeroen de Jong
 */
@AllArgsConstructor
public class PlayerPositionsComponent extends AbstractComponent<PlayerPositionsComponent> {

  /**
   * Set of players to draw.
   */
  private final Collection<Player> players;

  @Override
  protected ComponentRenderer<PlayerPositionsComponent> createDefaultRenderer() {
    return new ComponentRenderer<PlayerPositionsComponent>() {
      @Override
      public TerminalSize getPreferredSize(final PlayerPositionsComponent component) {
        return TerminalSize.ZERO;
      }

      @Override
      public void drawComponent(
          final TextGUIGraphics graphics,
          final PlayerPositionsComponent component
      ) {
        graphics.setForegroundColor(new RGB(255, 255, 255));
        component.players.forEach(player -> {
          graphics.setBackgroundColor(
              player.getTeamColor() == TeamColor.BLUE ? ANSI.BLUE : ANSI.YELLOW);
          graphics.putString((int) player.getX(), (int) player.getY(),
              Integer.toHexString(player.getId()));
        });
      }
    };
  }
}
