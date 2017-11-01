package io.leonis.subra.game.rule;

import java.util.*;
import io.leonis.zosma.game.Rule;
import io.leonis.subra.game.data.*;
import io.leonis.subra.game.data.Referee.Command;

/**
 * The Class InDirectFreeKickRule.
 *
 * @author Rimon Oz
 */
public class InDirectFreeKickRule implements Rule<Referee.Supplier, TeamColor> {

  @Override
  public boolean test(final Referee.Supplier sslReferee) {
    return !this.getViolators(sslReferee).isEmpty();
  }

  @Override
  public Set<TeamColor> getViolators(final Referee.Supplier sslReferee) {
    if (sslReferee.getReferee().getCommand()
        .equals(Command.INDIRECT_FREE_BLUE)) {
      return Collections.singleton(TeamColor.BLUE);
    } else if (sslReferee.getReferee().getCommand()
        .equals(Command.INDIRECT_FREE_YELLOW)) {
      return Collections.singleton(TeamColor.YELLOW);
    }
    return Collections.emptySet();
  }
}
