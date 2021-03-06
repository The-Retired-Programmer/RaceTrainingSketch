/*
 * Copyright 2020 richard.
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
package uk.theretiredprogrammer.racetrainingsketch.timerlog;

import uk.theretiredprogrammer.racetrainingsketch.core.Angle;

/**
 *
 * @author richard
 */
public class WindSwingLogEntry extends TimerLogEntry {

    private final double windswing;

    public WindSwingLogEntry(Angle swing) {
        this.windswing = swing.getDegrees();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append("  WINDSWING: ");
        sb.append(format1dp(windswing));
        sb.append("°");
        return sb.toString();
    }
}
