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
import java.util.Optional;
import uk.theretiredprogrammer.racetrainingsketch.boats.Boat;
import uk.theretiredprogrammer.racetrainingsketch.boats.BoatMetrics;
import uk.theretiredprogrammer.racetrainingsketch.core.Angle;
import static uk.theretiredprogrammer.racetrainingsketch.core.Angle.ANGLE90;
import uk.theretiredprogrammer.racetrainingsketch.core.DistancePolar;
import uk.theretiredprogrammer.racetrainingsketch.core.Location;
import static uk.theretiredprogrammer.racetrainingsketch.strategy.Decision.DecisionAction.SAILON;
import uk.theretiredprogrammer.racetrainingsketch.timerlog.BoatLogEntry;
import uk.theretiredprogrammer.racetrainingsketch.timerlog.DecisionLogEntry;
import uk.theretiredprogrammer.racetrainingsketch.timerlog.ReasonLogEntry;
import uk.theretiredprogrammer.racetrainingsketch.timerlog.TimerLog;
import uk.theretiredprogrammer.racetrainingsketch.ui.Controller;

/**
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public abstract class BoatStrategyForLeg {

    static enum LegType {
        WINDWARD, OFFWIND, GYBINGDOWNWIND, NONE
    }

    static BoatStrategyForLeg getLegStrategy(Controller controller, Boat boat, Leg leg) throws IOException {
        LegType legtype = getLegType(controller, boat, leg);
        switch (legtype) {
            case WINDWARD:
                return new BoatStrategyForWindwardLeg(controller, boat, leg);
            case OFFWIND:
                return new BoatStrategyForOffwindLeg(controller, boat, leg);
            case GYBINGDOWNWIND:
                return new BoatStrategyForGybingDownwindLeg(controller, boat, leg);
            default:
                throw new IOException("Illegal/unknown LEGTYPE: " + legtype.toString());
        }
    }

    static LegType getLegType(Controller controller, Boat boat, Leg leg) throws IOException {
        if (leg == null) {
            return LegType.NONE;
        }
        BoatMetrics metrics = boat.metrics;
        Angle legtowind = leg.getAngleofLeg().absAngleDiff(controller.windflow.getMeanFlowAngle());
        if (legtowind.lteq(metrics.getUpwindrelative())) {
            return LegType.WINDWARD;
        }
        if (boat.reachdownwind && legtowind.gteq(metrics.getDownwindrelative())) {
            return LegType.GYBINGDOWNWIND;
        }
        return LegType.OFFWIND;
    }
    
    static Optional<Double> getRefDistance(Location location, Location marklocation, Angle refangle) {
        DistancePolar tomark = new DistancePolar(location, marklocation);
        Angle refangle2mark = refangletomark(tomark.getAngle(), refangle);
        if (refangle2mark.gt(ANGLE90)) {
            return Optional.empty();
        }
        return Optional.of(refdistancetomark(tomark.getDistance(), refangle2mark));
    }

    private static double refdistancetomark(double distancetomark, Angle refangle2mark) {
        return distancetomark * Math.cos(refangle2mark.getRadians());
    }

    private static Angle refangletomark(Angle tomarkangle, Angle refangle) {
        return tomarkangle.absAngleDiff(refangle);
    }

    public final Boat boat;
    public final Leg leg;
    public final Decision decision;

    private final double length;
    private final Angle portoffsetangle;
    private final Angle starboardoffsetangle;

    public BoatStrategyForLeg(Boat boat, Leg leg,
            Angle portroundingportoffsetangle, Angle portroundingstarboardoffsetangle,
            Angle starboardroundingportoffsetangle, Angle starboardroundingstarboardoffsetangle) {
        this.boat = boat;
        this.leg = leg;
        this.decision = new Decision(boat);
        this.length = boat.metrics.getWidth() * 2;
        if (leg.isPortRounding()) {
            this.portoffsetangle = portroundingportoffsetangle;
            this.starboardoffsetangle = portroundingstarboardoffsetangle;
        } else {
            this.portoffsetangle = starboardroundingportoffsetangle;
            this.starboardoffsetangle = starboardroundingstarboardoffsetangle;
        }
    }

    public BoatStrategyForLeg(Boat boat, Leg leg,
            Angle portroundingoffsetangle, Angle starboardroundingoffsetangle) {
        this.boat = boat;
        this.decision = new Decision(boat);
        this.leg = leg;
        this.length = boat.metrics.getWidth() * 2;
        if (leg.isPortRounding()) {
            this.portoffsetangle = portroundingoffsetangle;
            this.starboardoffsetangle = portroundingoffsetangle;
        } else {
            this.portoffsetangle = starboardroundingoffsetangle;
            this.starboardoffsetangle = starboardroundingoffsetangle;
        }
    }

    public BoatStrategyForLeg(Boat boat, Leg previousleg) {
        this.boat = boat;
        this.decision = new Decision(boat);
        this.leg = previousleg;
        this.length = 0.0;
        this.portoffsetangle = null;
        this.starboardoffsetangle = null;
    }

    double getDistanceToMark(Location here) {
        return leg.getDistanceToEnd(here);
    }

    Location getMarkLocation() {
        return leg.getEndLocation();
    }

    Location getSailToLocation(boolean onPort) {
        return new DistancePolar(length, onPort ? portoffsetangle : starboardoffsetangle)
                .polar2Location(leg.getEndLocation());
    }

    Angle getAngletoSail(Location here, boolean onPort) {
        return here.angleto(getSailToLocation(onPort));
    }

    abstract String nextBoatStrategyTimeInterval(Controller controller) throws IOException;

    BoatStrategyForLeg nextTimeInterval(Controller controller, int simulationtime, TimerLog timerlog) throws IOException {
        String boatname = boat.name;
        if (decision.getAction() == SAILON) {
            String reason = nextBoatStrategyTimeInterval(controller);
            timerlog.add(new BoatLogEntry(boat));
            timerlog.add(new DecisionLogEntry(boatname, decision));
            timerlog.add(new ReasonLogEntry(boatname, reason));
        }
        if (boat.moveUsingDecision()) {
            Leg nextleg = leg.getFollowingLeg();
            return nextleg == null
                    ? new BoatStrategyForAfterFinishLeg(boat, leg)
                    : BoatStrategyForLeg.getLegStrategy(controller, boat, nextleg);
        }
        return null;
    }
}
