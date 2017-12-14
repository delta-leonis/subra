package io.leonis.subra.gui.field;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.Panel;
import io.leonis.algieba.spatial.PotentialField;
import io.leonis.subra.game.data.*;
import io.leonis.subra.gui.EmptySpace;
import io.leonis.subra.math.spatial.GaussianPotentialField;
import io.leonis.torch.component.graph.Gradient;
import java.awt.Color;
import java.util.function.BiFunction;
import lombok.AllArgsConstructor;

/**
 * The class GaussianFieldPanel.
 *
 * A {@link Panel} containing {@link EmptySpace}, {@link FieldLinesComponent}, {@link OrientationIndicatorComponent},
 * {@link BallPositionsComponent}, and {@link PlayerPositionsComponent}.
 *
 * @author Jeroen de Jong
 */
@AllArgsConstructor
public final class GaussianFieldPanel<S extends Player.SetSupplier & Ball.SetSupplier & Field.Supplier & PotentialField.Supplier> extends
    Panel {

  public GaussianFieldPanel(final S data) {
    final BiFunction<Integer, Integer, TextColor> backgroundSupplier = new GaussianPotentialFieldBackground(
        (GaussianPotentialField) data.getPotentialField(), new Gradient(Color.GREEN, Color.RED)
    );
    this.addComponent(new EmptySpace(backgroundSupplier));
    this.addComponent(new FieldLinesComponent(data.getField().getLines(), backgroundSupplier));
    this.addComponent(new OrientationIndicatorComponent(data.getPlayers(), backgroundSupplier));
    this.addComponent(new BallPositionsComponent(data.getBalls()));
    this.addComponent(new PlayerPositionsComponent(data.getPlayers()));
  }
}
