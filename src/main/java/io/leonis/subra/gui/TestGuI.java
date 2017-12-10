package io.leonis.subra.gui;

import com.googlecode.lanterna.*;
import com.googlecode.lanterna.TextColor.ANSI;
import com.googlecode.lanterna.gui2.BasicWindow;
import io.leonis.algieba.spatial.PotentialField;
import io.leonis.subra.game.data.*;
import io.leonis.subra.gui.field.GaussianField;
import io.leonis.subra.gui.field.*;
import io.leonis.subra.math.spatial.GaussianPotentialField;
import io.leonis.torch.Torch;
import io.leonis.torch.component.ComponentBackground;
import io.leonis.torch.component.graph.Gradient;
import java.awt.*;
import java.util.*;
import java.util.function.*;
import lombok.Value;
import org.nd4j.linalg.api.ndarray.INDArray;

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
    final INDArray data = FieldGenerator.whatever(robots);

    final Set<FieldLine> lines = new HashSet<>(
        Arrays.asList(
            new FieldLine.State(0, 0, 10, 10, 1)
        ));
    Field field = new Field.State(60, 90, lines, Collections.emptySet());
    GaussianPotentialField pf = new GaussianPotentialField(data, 60, 90, false);
    final BiFunction<Integer, Integer, TextColor> backgroundSupplier = new GaussianPotentialFieldBackground(
        pf,
        new Gradient(Color.GREEN.darker(), Color.RED));
    StatesMaarWeer s = new StatesMaarWeer(
        balls,
        robots,
        pf,
        field
    );
    new Thread(
        new Torch(
            gui -> gui.addWindowAndWait(new BasicWindow("wat")),
            new ComponentBackground(new GaussianField<>(s),ANSI.BLUE))
    ).start();
  }

  @Value
  static class StatesMaarWeer implements Ball.SetSupplier, Player.SetSupplier, PotentialField.Supplier, Field.Supplier {
    private Set<Ball> balls;
    private Set<Player> players;
    private PotentialField potentialField;
    private Field field;
  }

}
