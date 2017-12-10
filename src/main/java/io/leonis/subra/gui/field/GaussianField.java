package io.leonis.subra.gui.field;

import com.googlecode.lanterna.*;
import com.googlecode.lanterna.TextColor.ANSI;
import com.googlecode.lanterna.gui2.*;
import io.leonis.algieba.spatial.PotentialField;
import io.leonis.subra.game.data.*;
import io.leonis.subra.game.data.Player.Identity;
import io.leonis.subra.ipc.network.StrategyMulticastSubscriber;
import io.leonis.subra.ipc.peripheral.*;
import io.leonis.subra.math.spatial.GaussianPotentialField;
import io.leonis.torch.component.graph.Gradient;
import io.leonis.torch.window.RxPanel;
import io.leonis.zosma.ipc.ip.MulticastSubscriber;
import io.leonis.zosma.ipc.peripheral.Controller.MappingSupplier;
import java.awt.Color;
import java.io.IOException;
import java.net.InetAddress;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.*;
import java.util.stream.*;
import lombok.*;
import reactor.core.publisher.Flux;

/**
 * @author Jeroen de Jong
 */
@AllArgsConstructor
public class GaussianField<S extends Player.SetSupplier & Ball.SetSupplier & io.leonis.subra.game.data.Field.Supplier & PotentialField.Supplier> extends Panel {
  public GaussianField(
    final S data
  ) {
    final BiFunction<Integer, Integer, TextColor> backgroundSupplier = new GaussianPotentialFieldBackground(
        (GaussianPotentialField)data.getPotentialField(), new Gradient(Color.GREEN, Color.RED)
    );
    this.addComponent(new EmptySpace(backgroundSupplier));
    this.addComponent(new FieldLines(data.getField().getLines(), backgroundSupplier));
    this.addComponent(new Balls(data.getBalls()));
    this.addComponent(new Robots(data.getPlayers()));
  }
}
