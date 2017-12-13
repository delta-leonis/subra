package io.leonis.subra.ipc.network;

import io.leonis.subra.game.data.Player;
import io.leonis.subra.game.data.Player.PlayerIdentity;
import io.leonis.subra.ipc.network.GamepadPublisher.Frame;
import io.leonis.subra.ipc.peripheral.JamepadController;
import io.leonis.zosma.ipc.peripheral.Controller;
import java.util.*;
import lombok.*;
import org.reactivestreams.*;
import reactor.core.publisher.Flux;

/**
 * The Class GamepadPublisher.
 *
 * This class represents a {@link Publisher} of gamepad data.
 *
 * @author Jeroen de Jong
 */
@AllArgsConstructor
public class GamepadPublisher implements Publisher<Frame> {
  private final Publisher<JamepadController> controllerPublisher;
  private final Publisher<Set<Player>> playerPublisher;

  @Override
  public void subscribe(final Subscriber<? super Frame> subscriber) {
    Flux.combineLatest(this.controllerPublisher, this.playerPublisher, (controllers, players) ->
        new Frame(players, Collections.emptyMap()))
        .subscribe(subscriber);
  }

  @Value
  public static class Frame implements Controller.MappingSupplier<JamepadController, PlayerIdentity>, Player.SetSupplier {
    private final Set<Player> players;
    private final Map<JamepadController, Set<PlayerIdentity>> agentMapping;
  }
}
