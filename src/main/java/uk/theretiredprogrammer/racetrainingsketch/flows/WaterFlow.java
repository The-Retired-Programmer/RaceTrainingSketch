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
package uk.theretiredprogrammer.racetrainingsketch.flows;

import java.io.IOException;
import java.util.function.Supplier;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;
import uk.theretiredprogrammer.racetrainingsketch.ui.Controller;

/**
 *
 * @author richard
 */
public class WaterFlow extends Flow {
    
    public static WaterFlow create(Supplier<Controller> controllersupplier, JsonObject parsedjson) throws IOException {
        JsonArray waterarray = parsedjson.getJsonArray("WATER");
        if (waterarray != null) {
            FlowComponentSet flowcomponents = new FlowComponentSet();
            for (JsonValue waterv : waterarray) {
                if (waterv.getValueType() == JsonValue.ValueType.OBJECT) {
                    JsonObject water = (JsonObject) waterv;
                    flowcomponents.add(FlowComponentFactory.createflowelement(controllersupplier, water));
                } else {
                    throw new IOException("Malformed Definition File - WATER array contains items other that water objects");
                }
            }
            return new WaterFlow(controllersupplier, null, flowcomponents);
        }
        return null;
    }
    
    private WaterFlow(Supplier<Controller> controllersupplier, JsonObject params, FlowComponentSet flowcomponents) throws IOException{
        super(controllersupplier, params, flowcomponents);
    }   
    
}
