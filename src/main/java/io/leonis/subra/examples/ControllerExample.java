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
import java.util.Comparator;
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
                    new Player.PlayerIdentity(3, TeamColor.BLUE), ImmutableSet.of(new Controller.ControllerIdentity(0)))))
            .transform(new ChangeAssignmentDeducer<>(Comparator.comparingInt(Player.PlayerIdentity::getId)))
            .transform(new ControllerDeducer<>(new ControllerHandler<>(), a -> new AveragePlayerCommand(a)))
            .subscribe(m -> m.forEach((key, value) -> {
                System.out.print(key);
                System.out.print(" getChipKick ");
                System.out.print("chipkick: " + value.getChipKick());
                System.out.print("getDribblerSpin: " + value.getDribblerSpin());
                System.out.print("getFlatKick: " + value.getFlatKick());
                System.out.print("getVelocityX: " + value.getVelocityX());
                System.out.print("getVelocityY: " + value.getVelocityY());
                System.out.println("getVelocityR: " + value.getVelocityR());
            }));

        while(true) {}
    }
}
