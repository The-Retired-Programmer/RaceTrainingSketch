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

import uk.theretiredprogrammer.racetrainingsketch.core.Angle;

/**
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class PerformanceVectors {

    private final Angle[] windAngleData;
    private final double[] windSpeedData;
    private final double[][] boatSpeedData;

    public PerformanceVectors(double[][] boatSpeedData, Angle[] windAngleData, double[] windSpeedData) {
        this.boatSpeedData = boatSpeedData;
        this.windAngleData = windAngleData;
        this.windSpeedData = windSpeedData;
    }

    double getPotentialBoatSpeed(Angle angle, double windSpeed) {
        // find the lookup point for wind direction
        int angleindex = -1;
        for (Angle windangldata : windAngleData) {
            if (windangldata.gteq(angle)) {
                // found higher
                break;
            }
            angleindex++;
        }
        if (angleindex == -1){
            angleindex = 0;
        }
        // find the lookup point for wind speed
        int speedindex = -1;
        for (int i = 0; i < windSpeedData.length; i++) {
            if (windSpeedData[i] > windSpeed) { // found higher
                break;
            }
            speedindex++;
        }
        // now extrapolate the boat speed, firstly on two windspeed curves and then between them.
        double boatspeedlower = extrapolateFromTable(boatSpeedData, windAngleData, angle, speedindex, angleindex);
        double boatspeedupper = extrapolateFromTable(boatSpeedData, windAngleData, angle, speedindex + 1, angleindex);
        return extrapolateSpeed(boatspeedlower, boatspeedupper, windSpeed, windSpeedData, speedindex);
    }

    private double extrapolateFromTable(double[][] boatSpeedData, Angle[] windAngleData, Angle relative,
            int speedindex, int angleindex) {
        double boatspeedlower = boatSpeedData[speedindex][angleindex];
        double boatspeedupper = boatSpeedData[speedindex][angleindex + 1];
        double ratio = relative.sub(windAngleData[angleindex]).div(windAngleData[angleindex + 1].sub(windAngleData[angleindex]));
        return boatspeedlower + ratio * (boatspeedupper - boatspeedlower);
    }

    private double extrapolateSpeed(double boatspeedlower, double boatspeedupper, double windSpeed,
            double[] windSpeedData, int speedindex) {
        double ratio = (windSpeed - windSpeedData[speedindex]) / (windSpeedData[speedindex + 1] - windSpeedData[speedindex]);
        return boatspeedlower + ratio * (boatspeedupper - boatspeedlower);
    }

}
