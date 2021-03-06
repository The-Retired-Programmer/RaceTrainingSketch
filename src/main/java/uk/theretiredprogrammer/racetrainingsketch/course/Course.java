/*
 * Copyright 2014-2020 Richard Linsdale.
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
package uk.theretiredprogrammer.racetrainingsketch.course;

import uk.theretiredprogrammer.racetrainingsketch.strategy.Leg;
import java.awt.Graphics2D;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;
import uk.theretiredprogrammer.racetrainingsketch.core.ListOf;
import uk.theretiredprogrammer.racetrainingsketch.core.Location;
import uk.theretiredprogrammer.racetrainingsketch.core.LegValue;
import uk.theretiredprogrammer.racetrainingsketch.ui.Controller;
import uk.theretiredprogrammer.racetrainingsketch.ui.Displayable;

/**
 * The Mark Class - represent course marks.
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class Course implements Displayable {

    private final Leg firstcourseleg;
    private final Map<String, Mark> marks = new HashMap<>();

    public Course(Supplier<Controller> controllersupplier, JsonObject parsedjson) throws IOException {
        JsonArray markarray = parsedjson.getJsonArray("MARKS");
        if (markarray != null) {
            for (JsonValue markv : markarray) {
                if (markv.getValueType() == JsonValue.ValueType.OBJECT) {
                    JsonObject markparms = (JsonObject) markv;
                    Mark mark = new Mark(controllersupplier, markparms);
                    marks.put(mark.name, mark);
                } else {
                    throw new IOException("Malformed Definition File - MARKS array contains items other that mark objects");
                }
            }
        }
        //
        JsonObject courseobj = parsedjson.getJsonObject("COURSE");
        if (courseobj == null) {
            throw new IOException("Malformed Definition File - missing COURSE object");
        }
        Location start = Location.parse(courseobj, "start").orElse(new Location(0, 0));
        List<LegValue> legvalues = ListOf.<LegValue>parse(courseobj, "legs", (jval) -> LegValue.parseElement(jval))
                .orElseThrow(() -> new IOException("Malformed Definition file - <legs> is a mandatory parameter"));
        Leg following = null;
        int i = legvalues.size() - 1;
        while (i >= 0) {
            Location endmarklocation = marks.get(legvalues.get(i).getMarkname()).location;
            following = new Leg(controllersupplier,
                    i == 0 ? start : marks.get(legvalues.get(i - 1).getMarkname()).location,
                    endmarklocation, legvalues.get(i).isPortRounding(),
                    following);
            i--;
        }
        firstcourseleg = following;
    }

    public Leg getFirstCourseLeg() {
        return firstcourseleg;
    }

    @Override
    public void draw(Graphics2D g2D, double zoom) throws IOException {
        for (Mark mark : marks.values()) {
            mark.draw(g2D, zoom);
        }
    }
}
