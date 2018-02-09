package io.leonis.subra.examples;

import com.google.common.collect.*;
import io.leonis.subra.game.data.*;
import io.leonis.subra.game.data.Player.PlayerIdentity;
import io.leonis.subra.ipc.peripheral.*;
import io.leonis.zosma.game.engine.*;
import io.leonis.zosma.ipc.peripheral.*;
import io.leonis.zosma.ipc.peripheral.Controller.*;
import java.time.Duration;
import java.util.*;
import java.util.function.Function;
import lombok.Value;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

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
        .publishOn(Schedulers.parallel()).subscribeOn(Schedulers.parallel()) //this fixes random jamepad breakage
        .transform(
            new ParallelDeducer<>(
                new IdentityDeducer<>(),
                new ConfigurableMappingDeducer<>(
                    Flux.just(() -> controllerMapping),
                    new MoveControllerFunction<>(
                        Comparator.comparingInt(Player.PlayerIdentity::getId))),
                   Frame::new))
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

  @Value
  public static class Frame
      implements Controller.SetSupplier<XboxController>, Controller.MapSupplier<PlayerIdentity> {
    private final Set<XboxController> controllerSet;
    private final Map<PlayerIdentity, Set<ControllerIdentity>> controllerMapping;

    public Frame(
        final Controller.SetSupplier<XboxController> setSupplier,
        final Controller.MapSupplier<PlayerIdentity> mapSupplier
    ) {
      this.controllerMapping = mapSupplier.getControllerMapping();
      this.controllerSet = setSupplier.getControllerSet();
    }
  }
}
