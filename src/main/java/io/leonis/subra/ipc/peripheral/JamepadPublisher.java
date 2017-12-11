package io.leonis.subra.ipc.peripheral;

import com.studiohartman.jamepad.ControllerManager;
import io.leonis.subra.game.data.Player;
import io.leonis.zosma.ipc.peripheral.*;
import io.leonis.zosma.ipc.peripheral.Controller.MappingSupplier;
import java.util.*;
import java.util.stream.IntStream;
import lombok.Value;
import org.reactivestreams.Subscriber;
import reactor.core.publisher.Flux;

/**
 * The Class JamepadPublisher.
 *
 * This class represents a {@link org.reactivestreams.Publisher} of {@link JamepadController}.
 *
 * @author Jeroen de Jong
 */
@Value
public class JamepadPublisher implements
    ControllerPublisher<JamepadController.Identity, Player.Identity, JamepadController> {
  private final Map<JamepadController.Identity, Set<Player.Identity>> controllerMapping;
  private final ControllerManager controllers;

  /**
   * Constructs a new {@link JamepadPublisher} based on the supplied mapping of {@link
   * JamepadController.Identity} to a set of {@link Player.Identity} which it controls.
   *
   * @param controllerMapping The mapping of {@link JamepadController.Identity} to a set of {@link
   *                          Player.Identity} which it controls.
   */
  public JamepadPublisher(
      final Map<JamepadController.Identity, Set<Player.Identity>> controllerMapping
  ) {
    this.controllerMapping = controllerMapping;
    this.controllers = new ControllerManager(controllerMapping.size());
    this.controllers.initSDLGamepad();
  }

  @Override
  public void subscribe(
      final Subscriber<? super MappingSupplier<JamepadController, Player.Identity>> subscriber
  ) {
    Flux.<Controller.MappingSupplier<JamepadController, Player.Identity>>create(fluxSink -> {
      while (true) {
        IntStream.range(0, this.getControllers().getNumControllers())
            .boxed()
            .filter(controllerIndex -> this.getControllers().getControllerIndex(controllerIndex)
                .isConnected())
            .forEach(index ->
                fluxSink.next(() ->
                    Collections.singletonMap(new JamepadController(
                            new JamepadController.Identity(index),
                            this.getControllers().getState(index)),
                        this.getControllerMapping().get(new JamepadController.Identity(index)))));
      }
    }).subscribe(subscriber);
  }
}
