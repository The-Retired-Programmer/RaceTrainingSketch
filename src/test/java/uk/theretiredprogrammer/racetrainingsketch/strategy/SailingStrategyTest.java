/*
 * Copyright 2020 richard linsdale.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.theretiredprogrammer.racetrainingsketch.strategy;

import java.io.IOException;
import java.util.function.Supplier;
import javax.json.Json;
import javax.json.JsonObjectBuilder;
import uk.theretiredprogrammer.racetrainingsketch.boats.Boat;
import static javax.json.JsonValue.FALSE;
import static javax.json.JsonValue.TRUE;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import uk.theretiredprogrammer.racetrainingsketch.core.Angle;
import static uk.theretiredprogrammer.racetrainingsketch.strategy.Decision.DecisionAction.MARKROUNDING;
import static uk.theretiredprogrammer.racetrainingsketch.strategy.Decision.DecisionAction.SAILON;
import static uk.theretiredprogrammer.racetrainingsketch.strategy.Decision.DecisionAction.STOP;
import static uk.theretiredprogrammer.racetrainingsketch.strategy.Decision.DecisionAction.TURN;
import uk.theretiredprogrammer.racetrainingsketch.ui.Controller;

/**
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class SailingStrategyTest {

    private Boat boat;
    private Controller controller;
    private Angle winddirection;

    public Decision makeDecision(String filename, Supplier<String>... configs) throws IOException {
        controller = new Controller(filename, (s) -> requestpaint(s));
        boat = controller.boats.getBoat("Red");
        //
        for (var config : configs) {
            String error = config.get();
            if (error != null) {
                throw new IOException(error);
            }
        }
        Leg leg = controller.course.getFirstCourseLeg();
        winddirection = controller.windflow.getFlow(boat.location).getAngle();
        BoatStrategyForLeg strategy = BoatStrategyForLeg.getLegStrategy(
                controller, boat, leg);
        strategy.nextBoatStrategyTimeInterval(controller);
        return strategy.decision;
    }

    private void requestpaint(String s) {
        fail("BAD - request for paint made -should not occur");
    }

    String setwindfrom(String name, int degrees) {
        try {
            controller.windflow.getFlowComponentSet().change(Json.createObjectBuilder().add("from", degrees).build(), name);
            controller.windflow.setFlows();
            return null;
        } catch (IOException ex) {
            return ex.getLocalizedMessage();
        }
    }

    String setwindfrom(int zlevel, int degrees) {
        try {
            controller.windflow.getFlowComponentSet().change(Json.createObjectBuilder().add("from", degrees).build(), zlevel);
            controller.windflow.setFlows();
            return null;
        } catch (IOException ex) {
            return ex.getLocalizedMessage();
        }
    }

    String setwindfrom(int degrees) {
        return setwindfrom(0, degrees);
    }

    String setboatparam(String name, boolean value) {
        try {
            boat.change(Json.createObjectBuilder().add(name, value ? TRUE : FALSE).build());
            return null;
        } catch (IOException ex) {
            return ex.getLocalizedMessage();
        }
    }

    String setboatparamstrue(String... names) {
        try {
            JsonObjectBuilder job = Json.createObjectBuilder();
            for (String name : names) {
                job.add(name, TRUE);
            }
            boat.change(job.build());
            return null;
        } catch (IOException ex) {
            return ex.getLocalizedMessage();
        }
    }

    public String setboatintvalue(String name, int value) {
        try {
            boat.change(Json.createObjectBuilder().add(name, value).build());
            return null;
        } catch (IOException ex) {
            return ex.getLocalizedMessage();
        }
    }

    String setboatlocationvalue(String name, double x, double y) {
        try {
            boat.change(Json.createObjectBuilder()
                    .add(name, Json.createArrayBuilder().add(x).add(y).build())
                    .build());
            return null;
        } catch (IOException ex) {
            return ex.getLocalizedMessage();
        }
    }

    String setboatdirectionvalue(String name, double angle) {
        try {
            boat.change(Json.createObjectBuilder()
                    .add(name, angle)
                    .build());
            return null;
        } catch (IOException ex) {
            return ex.getLocalizedMessage();
        }
    }

    Angle getStarboardCloseHauled() {
        return boat.getStarboardCloseHauledCourse(winddirection);
    }

    Angle getPortCloseHauled() {
        return boat.getPortCloseHauledCourse(winddirection);
    }

    Angle getStarboardReaching() {
        return boat.getStarboardReachingCourse(winddirection);
    }

    Angle getPortReaching() {
        return boat.getPortReachingCourse(winddirection);
    }

    void assertTURN(Decision decision, int angle, boolean isSTARBOARD) {
        assertAll("Decision is TURN?",
                () -> assertEquals(TURN, decision.getAction()),
                () -> assertEquals(new Angle(angle), decision.getAngle()),
                () -> assertEquals(isSTARBOARD, !decision.isPort())
        );
    }

    void assertTURN(Decision decision, Angle angle, boolean isSTARBOARD) {
        assertAll("Decision is TURN?",
                () -> assertEquals(TURN, decision.getAction()),
                () -> assertEquals(angle, decision.getAngle()),
                () -> assertEquals(isSTARBOARD, !decision.isPort())
        );
    }

    void assertSailing(Decision decision, Angle minangle, Angle maxangle) {
        switch (decision.getAction()) {
            case TURN:
                assertAll("Check angle Range of a Turn",
                        () -> assertTrue(minangle.lteq(decision.getAngle()), "angle " + decision.getAngle().toString() + " is less than minimum " + minangle.toString()),
                        () -> assertTrue(maxangle.gteq(decision.getAngle()), "angle " + decision.getAngle().toString() + " is greater than maximum " + maxangle.toString())
                );
                return;
            case SAILON:
                return;
            default:
                fail("action is not TURN or SAILON");
        }

    }

    void assertSAILON(Decision decision) {
        assertAll("Decision is SAILON?",
                () -> assertEquals(SAILON, decision.getAction())
        );
    }

    void assertMARKROUNDING(Decision decision, int angle, boolean isSTARBOARD) {
        assertAll("Decision is MARKROUNDING?",
                () -> assertEquals(MARKROUNDING, decision.getAction()),
                () -> assertEquals(new Angle(angle), decision.getAngle()),
                () -> assertEquals(isSTARBOARD, !decision.isPort())
        );
    }

    void assertSTOP(Decision decision) {
        assertAll("Decision is STOP?",
                () -> assertEquals(STOP, decision.getAction())
        );
    }

}
