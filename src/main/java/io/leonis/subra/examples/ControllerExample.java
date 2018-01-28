package io.leonis.subra.examples;

import com.google.common.collect.*;
import io.leonis.subra.game.data.*;
import io.leonis.subra.game.data.Player.PlayerIdentity;
import io.leonis.subra.ipc.peripheral.ChangeAssignmentDeducer;
import io.leonis.zosma.ipc.peripheral.Controller.ControllerIdentity;
import io.leonis.zosma.ipc.peripheral.*;
import java.time.Duration;
import java.util.*;
import reactor.core.publisher.Flux;

/**
 * @author jeroen.dejong.
 */
public class ControllerExample {

  public static void main(final String[] args) {
    final Map<PlayerIdentity, Set<ControllerIdentity>> controllerMapping = ImmutableMap.of(
        new PlayerIdentity(1, TeamColor.BLUE), ImmutableSet.of(new ControllerIdentity(0)),
        new PlayerIdentity(2, TeamColor.BLUE), Collections.emptySet(),
        new PlayerIdentity(3, TeamColor.BLUE), Collections.emptySet(),
        new PlayerIdentity(4, TeamColor.BLUE), Collections.emptySet());

    Flux.from(new ControllerSetPublisher<>(2, Duration.ofMillis(100), new XboxJamepadAdapter()))
        .transform(new ChangeAssignmentDeducer<>(Flux.just(() -> controllerMapping),
            Comparator.comparingInt(Player.PlayerIdentity::getId)))
        .subscribe(m -> m.getControllerMapping().forEach((key, value) -> {
              System.out.print(key.getId());
              System.out.print(" => ");
              value.forEach(bal -> {
                System.out.print("controller: " + bal.getIndex() + ", ");
              });
              System.out.println();
            })
        );

    while(true) {}
  }
}
