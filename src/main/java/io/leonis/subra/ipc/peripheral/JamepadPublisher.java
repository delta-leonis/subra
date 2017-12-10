package io.leonis.subra.ipc.peripheral;

import com.studiohartman.jamepad.ControllerManager;
import io.leonis.zosma.game.Agent;
import io.leonis.zosma.ipc.peripheral.*;
import java.util.*;
import java.util.stream.IntStream;
import lombok.Value;
import org.reactivestreams.Subscriber;
import reactor.core.publisher.Flux;

/**
 * @author Jeroen de Jong
 */
@Value
public class JamepadPublisher<A extends Agent> implements
    ControllerPublisher<Integer, JamepadController, A> {
  private final Map<Integer, Set<A>> controllerMapping;
  private final ControllerManager controllers;

  public JamepadPublisher(final Map<Integer, Set<A>> controllerMapping) {
    this.controllerMapping = controllerMapping;
    this.controllers = new ControllerManager(controllerMapping.size());
    this.controllers.initSDLGamepad();
  }

  @Override
  public void subscribe(
      final Subscriber<? super Controller.MappingSupplier<JamepadController, A>> subscriber
  ) {
    Flux.<Controller.MappingSupplier<JamepadController, A>>create(fluxSink -> {
      while (true) {
        IntStream.range(0, this.getControllers().getNumControllers())
            .boxed()
            .filter(controllerIndex -> this.getControllers().getControllerIndex(controllerIndex)
                .isConnected())
            .forEach(index ->
                fluxSink.next(() ->
                    Collections.singletonMap(new JamepadController(
                            index,
                            this.getControllers().getState(index)),
                        this.getControllerMapping().get(index))));
      }
    }).subscribe(subscriber);
  }
}
