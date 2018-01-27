package io.leonis.subra.examples;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import io.leonis.subra.game.data.Player;
import io.leonis.subra.game.data.TeamColor;
import io.leonis.subra.ipc.peripheral.ChangeAssignmentDeducer;
import io.leonis.subra.ipc.peripheral.ControllerHandler;
import io.leonis.subra.math.AveragePlayerCommand;
import io.leonis.zosma.ipc.peripheral.Controller;
import io.leonis.zosma.ipc.peripheral.ControllerDeducer;
import io.leonis.zosma.ipc.peripheral.ControllerPublisher;
import io.leonis.zosma.ipc.peripheral.XboxController;
import io.leonis.zosma.ipc.peripheral.XboxJamepadAdapter;
import java.time.Duration;
import java.util.*;
import reactor.core.publisher.Flux;

/**
 * @author Jeroen de Jong
 * @date 26-1-2018
 */
public class ControllerExample {

    public static void main(String[] args) {
        Flux.from(
            new ControllerPublisher<>(6, Duration.ofMillis(100), new XboxJamepadAdapter(),
                ImmutableMap.of(
                    new Player.PlayerIdentity(1, TeamColor.BLUE), ImmutableSet.of(new Controller.ControllerIdentity(0)),
                    new Player.PlayerIdentity(2, TeamColor.BLUE), Collections.emptySet(),
                    new Player.PlayerIdentity(3, TeamColor.BLUE), Collections.emptySet(),
                    new Player.PlayerIdentity(4, TeamColor.BLUE), Collections.emptySet())))
            .transform(new ChangeAssignmentDeducer<>(Comparator.comparingInt(Player.PlayerIdentity::getId)))
            .map(Controller.MapSupplier::getControllerMapping)
            .subscribe(m -> m.forEach((key, value) -> {
                System.out.print(key.getId());
                System.out.print(" => ");
                value.stream().forEach(bal -> {
                  System.out.print("controller: " + bal.getIndex() + ", ");
                });
              System.out.println();
            }));

        while(true) {}
    }
}
