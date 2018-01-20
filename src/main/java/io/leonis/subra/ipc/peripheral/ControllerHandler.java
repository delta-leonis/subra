package io.leonis.subra.ipc.peripheral;

import io.leonis.subra.game.data.PlayerCommand;
import io.leonis.zosma.ipc.peripheral.component.Stick;
import io.leonis.zosma.ipc.peripheral.component.trigger.RightTriggerSupplier;
import io.leonis.zosma.ipc.peripheral.component.xbox.RightClusterSupplier;
import java.util.function.Function;

/**
 * The Class ControllerHandler.
 *
 * Transforms a controller into a {@link PlayerCommand}.
 *
 * @author Jeroen de Jong
 */
public final class ControllerHandler<C extends Stick.LeftSupplier & Stick.RightSupplier & RightClusterSupplier & RightTriggerSupplier>
    implements Function<C, PlayerCommand> {

  @Override
  public PlayerCommand apply(final C controller) {
    return new PlayerCommand.State(
        -1f * (float) Math.tanh(Math.pow(controller.getLeftStick().getX(), 3))
            / 0.8f,
        -1f * (float) Math.tanh(Math.pow(controller.getLeftStick().getY(), 3))
            / 0.8f,
        (float) Math.tanh(Math.pow(controller.getRightStick().getX(), 3)) / 3.5f,
        controller.isAPressed() ? 1f : 0f,
        controller.isBPressed() ? 1f : 0f,
        controller.getRightTrigger());
  }
}
