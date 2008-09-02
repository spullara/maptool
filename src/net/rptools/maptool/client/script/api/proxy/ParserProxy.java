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
package net.rptools.maptool.client.script.api.proxy;

import java.math.BigDecimal;

import net.rptools.parser.Expression;
import net.rptools.parser.Parser;
import net.rptools.parser.ParserException;

public class ParserProxy {
    private final Parser parser;
    
    public ParserProxy(Parser parser) {
        this.parser = parser;
    }
    
    public void setVariable(String name, Object value) throws ParserException {
        if (value instanceof Number)
            parser.setVariable(name, new BigDecimal(((Number) value).doubleValue()));
        else
            parser.setVariable(name, value);
    }

    public Object getVariable(String variableName) throws ParserException {
        return parser.getVariable(variableName);
    }
    
    public Object evaluate(String expression) throws ParserException {
        Expression xp = parser.parseExpression(expression);
        return xp.evaluate();
    }
}
