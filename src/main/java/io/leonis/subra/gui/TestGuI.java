package io.leonis.subra.gui;

import com.googlecode.lanterna.*;
import com.googlecode.lanterna.TextColor.ANSI;
import com.googlecode.lanterna.gui2.BasicWindow;
import io.leonis.subra.game.data.*;
import io.leonis.subra.gui.field.Field;
import io.leonis.subra.gui.field.*;
import io.leonis.torch.Torch;
import io.leonis.torch.component.ComponentBackground;
import io.leonis.torch.component.graph.Gradient;
import java.awt.Color;
import java.util.*;
import java.util.stream.*;
import org.nd4j.linalg.api.ndarray.INDArray;

/**
 * todo remove
 */
public class TestGuI {

  public static void main(String[] args) throws NoSuchMethodException {
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
    final Set<FieldLine> fieldLines = Collections.emptySet();
    //FIXME might want to wrap the character in a label and set the textstyle to BOLD or something.
    new Thread(
        new Torch(
            gui -> gui.addWindowAndWait(new BasicWindow("wat")),
            new ComponentBackground(
                new Field(
                    data,
                    Arrays.asList(
                        new BallBackground(balls, TextColor.RGB.Indexed.fromRGB(255,165,0)),
                        new TeamColorBackground(robots, TextColor.Indexed.fromRGB(255, 255, 50)),
                        new PotentialBackground(data, new Gradient(Color.GREEN.darker(), Color.RED))
                    ),
                    Arrays.asList(
                        new FieldLines(fieldLines),
                        new Corners(fieldLines),
                        new OrientationIndicator(robots),
                        balls.stream().collect(Collectors.toMap(b -> new TerminalPosition((int) b.getX(), (int) b.getY()), b -> 'O'))::get,
                        robots.stream().collect(Collectors.toMap(r -> new TerminalPosition((int)r.getX(), (int)r.getY()), r -> String.valueOf(r.getId()).charAt(0)))::get
                    )), ANSI.BLUE))
    ).start();
  }

}
