package io.leonis.subra.gui.field;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor.*;
import com.googlecode.lanterna.gui2.*;
import io.leonis.subra.game.data.*;
import java.util.Set;
import lombok.AllArgsConstructor;

/**
 * @author Jeroen de Jong
 */
@AllArgsConstructor
public class Robots extends AbstractComponent<Robots> {

  private final Set<Player> players;

  @Override
  protected ComponentRenderer<Robots> createDefaultRenderer() {
    return new ComponentRenderer<Robots>() {
      @Override
      public TerminalSize getPreferredSize(final Robots component) {
        // TODO This should be determined based on the set of players
        return new TerminalSize(10, 10);
      }

      @Override
      public void drawComponent(final TextGUIGraphics graphics, final Robots component) {
        graphics.setForegroundColor(new RGB(255, 255, 255));
        players.forEach(player -> {
          graphics.setBackgroundColor(
              player.getTeamColor() == TeamColor.BLUE ? ANSI.BLUE : ANSI.YELLOW);
          graphics.setCharacter((int) player.getX(), (int) player.getY(),
              String.valueOf(player.getId()).charAt(0));
        });
      }
    };
  }
}
