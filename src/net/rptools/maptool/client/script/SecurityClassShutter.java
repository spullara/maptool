/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package net.rptools.maptool.client.script;

import org.mozilla.javascript.ClassShutter;

public class SecurityClassShutter implements ClassShutter {

    public boolean visibleToScripts(String cname) {
        // Everything in java.lang excluding the system class.
        if (cname.startsWith("java.lang")) {
            if (cname.equals("java.lang.System")) {
                return false;
            }
            return true;
        }

        // Everything in java.util
        if (cname.startsWith("java.util")) {
            return true;
        }

        // Everything in java.math
        if (cname.startsWith("java.math")) {
            return true;
        }

        // Maptool JavaScript macro api classes.
        if (cname.startsWith("net.rptools.maptool.client.script.api")) {
            return true;
        }

        // Allow the mozilla javascript classes
        if (cname.startsWith("org.mozilla.javascript")) {
            return true;
        }

        return false;
    }
}
