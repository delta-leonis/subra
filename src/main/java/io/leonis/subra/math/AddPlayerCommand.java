package io.leonis.subra.math;

import io.leonis.subra.game.data.PlayerCommand;
import lombok.experimental.Delegate;

/**
 * The Class AddPlayerCommand.
 *
 * This class represents the computation of addition of two {@link PlayerCommand}.
 *
 * @author Rimon Oz
 */
public class AddPlayerCommand implements PlayerCommand {
  @Delegate
  private final PlayerCommand playerCommand;

  /**
   * Computes the addition of the supplied two {@link PlayerCommand}.
   *
   * @param leftCommand  The first command to add.
   * @param rightCommand The second command to add.
   */
  public AddPlayerCommand(final PlayerCommand leftCommand, final PlayerCommand rightCommand) {
    this.playerCommand = new PlayerCommand.State(
        leftCommand.getVelocityX() + rightCommand.getVelocityX(),
        leftCommand.getVelocityY() + rightCommand.getVelocityY(),
        leftCommand.getVelocityR() + rightCommand.getVelocityR(),
        leftCommand.getFlatKick() + rightCommand.getFlatKick(),
        leftCommand.getChipKick() + rightCommand.getChipKick(),
        leftCommand.getDribblerSpin() + rightCommand.getDribblerSpin());
  }
}
