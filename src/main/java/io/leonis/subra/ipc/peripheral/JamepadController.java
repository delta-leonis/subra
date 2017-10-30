package io.leonis.subra.ipc.peripheral;

import com.studiohartman.jamepad.ControllerState;
import io.leonis.zosma.ipc.peripheral.Controller;
import lombok.Value;

/**
 * @author Jeroen de Jong
 */
@Value
public class JamepadController implements Controller<Integer, ControllerState> {
  private final Integer identifier;
  private final ControllerState controls;
}
