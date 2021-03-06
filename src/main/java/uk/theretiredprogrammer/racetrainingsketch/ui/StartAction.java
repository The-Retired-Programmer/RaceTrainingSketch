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
package uk.theretiredprogrammer.racetrainingsketch.ui;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.openide.util.ImageUtilities;

/**
 * The Start Action.
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public final class StartAction extends AbstractAction {

    private final SimulationDisplay element;

    /**
     * Constructor
     *
     * @param element the simulation element into which the action icon is to be
     * placed
     */
    public StartAction(SimulationDisplay element) {
        super("Start Simulation", ImageUtilities.loadImageIcon("com/famfamfam/www/silkicons/control_play_blue.png", false));
        this.element = element;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        element.start();
    }
}
