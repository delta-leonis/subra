package io.leonis.subra.math;

import io.leonis.subra.game.data.PlayerCommand;
import java.util.stream.Stream;
import lombok.experimental.Delegate;

/**
 * The Class AveragePlayerCommand.
 *
 * This class represents the computation of the average of a collection of {@link PlayerCommand}.
 *
 * @author Rimon Oz
 */
public class AveragePlayerCommand implements PlayerCommand {
  @Delegate
  private final PlayerCommand playerCommand;

  /**
   * Computes the average of the supplied {@link PlayerCommand commands}.
   *
   * @param commands The {@link PlayerCommand commands} to compute the average of.
   */
  public AveragePlayerCommand(final PlayerCommand... commands) {
    this.playerCommand = new MultiplyPlayerCommand(
        Stream.of(commands)
            .reduce(PlayerCommand.State.STOP, AddPlayerCommand::new),
        1f / commands.length);
  }
}