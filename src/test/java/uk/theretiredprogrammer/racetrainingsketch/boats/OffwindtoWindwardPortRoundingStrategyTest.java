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
package uk.theretiredprogrammer.racetrainingsketch.boats;

import java.io.IOException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static uk.theretiredprogrammer.racetrainingsketch.boats.Decision.DecisionAction.MARKROUNDING;
import static uk.theretiredprogrammer.racetrainingsketch.boats.Decision.DecisionAction.SAILON;
import uk.theretiredprogrammer.racetrainingsketch.core.Angle;
import static uk.theretiredprogrammer.racetrainingsketch.core.Angle.ANGLE90;

/**
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class OffwindtoWindwardPortRoundingStrategyTest extends SailingStrategyTest {

    @Test
    public void layline1() throws IOException {
        System.out.println("layline 1");
        Decision decision = makeDecision("/offwindtowindward-portrounding.json",
                () -> setboatlocationvalue("location", 47, 12));
        assertEquals(SAILON, decision.getAction());
    }

    @Test
    public void layline2() throws IOException {
        System.out.println("layline 2");
        Decision decision = makeDecision("/offwindtowindward-portrounding.json",
                () -> setboatlocationvalue("location", 47, 11));
        assertEquals(SAILON, decision.getAction());
    }

    @Test
    public void layline3() throws IOException {
        System.out.println("layline 3");
        Decision decision = makeDecision("/offwindtowindward-portrounding.json",
                () -> setboatlocationvalue("location", 47, 10));
        assertEquals(MARKROUNDING, decision.getAction());
        assertEquals(new Angle(45), decision.getAngle());
        assert (!decision.isClockwise());
    }

    @Test
    public void layline4() throws IOException {
        System.out.println("layline 4");
        Decision decision = makeDecision("/offwindtowindward-portrounding.json",
                () -> setboatlocationvalue("location", 47, 9.5));
        assertEquals(MARKROUNDING, decision.getAction());
        assertEquals(new Angle(45), decision.getAngle());
        assert (!decision.isClockwise());
    }

    @Test
    public void layline5() throws IOException {
        System.out.println("layline 5");
        Decision decision = makeDecision("/offwindtowindward-portrounding.json",
                () -> setwindfrom(45),
                () -> setboatlocationvalue("location", 47, 11));
        assertEquals(SAILON, decision.getAction());
    }

    @Test
    public void layline6() throws IOException {
        System.out.println("layline 6");
        Decision decision = makeDecision("/offwindtowindward-portrounding.json",
                () -> setwindfrom(45),
                () -> setboatlocationvalue("location", 47, 10));
        assertEquals(MARKROUNDING, decision.getAction());
        assertEquals(ANGLE90, decision.getAngle());
        assert (!decision.isClockwise());
    }
}
