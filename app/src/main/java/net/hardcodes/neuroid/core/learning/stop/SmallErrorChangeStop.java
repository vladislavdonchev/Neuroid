/**
 * Copyright 2010 Neuroph Project http://neuroph.sourceforge.net
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package net.hardcodes.neuroid.core.learning.stop;

import net.hardcodes.neuroid.core.learning.SupervisedLearning;

import java.io.Serializable;

/**
 * Stops learning rule if error change has been too small for specified number
 * of iterations
 *
 * @author Zoran Sevarac <sevarac@gmail.com>
 */
public class SmallErrorChangeStop implements StopCondition, Serializable {

    private SupervisedLearning learningRule;

    public SmallErrorChangeStop(SupervisedLearning learningRule) {
        this.learningRule = learningRule;
    }

    @Override
    public boolean isReached() {
        if (learningRule.getMinErrorChangeIterationsCount() >= learningRule.getMinErrorChangeIterationsLimit()) {
            return true;
        }

        return false;
    }
}
