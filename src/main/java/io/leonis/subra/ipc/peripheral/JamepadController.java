package io.leonis.subra.ipc.peripheral;

import com.studiohartman.jamepad.ControllerState;
import io.leonis.subra.game.data.Player;
import io.leonis.zosma.game.Identity;
import io.leonis.zosma.ipc.peripheral.Controller;
import lombok.Value;

/**
 * @author Jeroen de Jong
 */
@Value
public class JamepadController implements Controller<JamepadController.Identity, ControllerState> {
  private final Identity identifier;
  private final ControllerState controls;

  @Value
  public static class Identity implements io.leonis.zosma.game.Identity {
    private final int id;
  }
}
