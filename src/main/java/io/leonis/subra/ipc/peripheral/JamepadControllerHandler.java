package io.leonis.subra.ipc.peripheral;

import io.leonis.subra.game.data.PlayerCommand;
import io.leonis.zosma.ipc.peripheral.ControllerHandler;
import lombok.Value;

/**
 * @author Jeroen de Jong
 */
@Value
public class JamepadControllerHandler
    implements ControllerHandler<JamepadController, PlayerCommand> {

  @Override
  public PlayerCommand apply(final JamepadController controller) {
    return new PlayerCommand.State(
        -1f * (float) StrictMath.tanh(StrictMath.pow(controller.getControls().leftStickX, 3))
            / 0.8f,
        -1f * (float) StrictMath.tanh(StrictMath.pow(controller.getControls().leftStickY, 3))
            / 0.8f,
        (float) StrictMath.tanh(StrictMath.pow(controller.getControls().rightStickX, 3)) / 3.5f,
        controller.getControls().aJustPressed ? 1f : 0f,
        controller.getControls().bJustPressed ? 1f : 0f,
        controller.getControls().rightTrigger);
  }
}
