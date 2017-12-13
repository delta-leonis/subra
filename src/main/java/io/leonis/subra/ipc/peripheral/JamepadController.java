package io.leonis.subra.ipc.peripheral;

import com.studiohartman.jamepad.ControllerState;
import io.leonis.subra.ipc.peripheral.JamepadController.JamepadControllerIdentity;
import io.leonis.zosma.game.Identity;
import io.leonis.zosma.ipc.peripheral.Controller;
import lombok.Value;

/**
 * @author Jeroen de Jong
 */
@Value
public class JamepadController implements Controller<JamepadControllerIdentity, ControllerState> {
  private final JamepadControllerIdentity identifier;
  private final ControllerState controls;

  @Value
  public static class JamepadControllerIdentity implements Identity {
    private final int id;
  }
}
