package io.leonis.subra.gui;

import com.googlecode.lanterna.TextColor.ANSI;
import com.googlecode.lanterna.gui2.BasicWindow;
import io.leonis.algieba.spatial.*;
import io.leonis.subra.game.data.*;
import io.leonis.subra.gui.field.PotentialFieldPanel;
import io.leonis.subra.math.spatial.GaussianPotentialField;
import io.leonis.torch.Torch;
import io.leonis.torch.component.ComponentBackground;
import java.util.*;
import java.util.stream.Collectors;
import lombok.Value;
import org.nd4j.linalg.factory.Nd4j;

/**
 * todo remove
 */
public class TestGuI {

  public static void main(final String[] args) {
    final Set<Player> robots =
        new HashSet<>(
            Arrays.asList(
                new Player.State(
                    1,
                    System.currentTimeMillis(),
                    10,
                    5,
                    180,
                    TeamColor.BLUE),
                new Player.State(
                    2,
                    System.currentTimeMillis(),
                    4,
                    13,
                    320,
                    TeamColor.BLUE),
                new Player.State(
                    3,
                    System.currentTimeMillis(),
                    14,
                    20,
                    140,
                    TeamColor.BLUE)));
    final Set<Ball> balls = new HashSet<>(
        Arrays.asList(
            new Ball.State(
                System.currentTimeMillis(),
                10, 10, 0),
            new Ball.State(
                System.currentTimeMillis(),
                5, 5, 0)));
//    final INDArray data = FieldGenerator.whatever(robots);

    final Set<FieldLine> lines = new HashSet<>(
        Arrays.asList(
            new FieldLine.State(0, 0, 10, 10, 1)
        ));
    Field field = new Field.State(60, 90, lines, Collections.emptySet());

    PotentialField aggregatedPotentialField =
        new AggregatedPotentialField(
            Nd4j.zeros(2, 1),
            robots.stream()
                .map(player ->
                    new GaussianPotentialField(player.getXY(), 60, 1, 1, 0, false))
                .collect(Collectors.toSet()));

    StatesMaarWeer s = new StatesMaarWeer(
        balls,
        robots,
        aggregatedPotentialField,
        field
    );
    new Thread(
        new Torch(
            gui -> gui.addWindowAndWait(new BasicWindow("wat")),
            new ComponentBackground(new PotentialFieldPanel<>(s), ANSI.BLUE))
    ).start();
  }

  @Value
  static class StatesMaarWeer implements Ball.SetSupplier, Player.SetSupplier,
      PotentialField.Supplier, Field.Supplier {

    private Set<Ball> balls;
    private Set<Player> players;
    private PotentialField potentialField;
    private Field field;
  }

}
