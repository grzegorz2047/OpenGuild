/*
 * Copyright 2014 Aleksander.
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
package pl.grzegorz2047.openguild2047.hooks.skript.expression;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Converter;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import pl.grzegorz2047.openguild2047.events.guild.GuildCreateEvent;
import pl.grzegorz2047.openguild2047.events.guild.GuildEvent;
import org.bukkit.event.Event;

/**
 *
 * @author Aleksander
 */
public class DescriptionExpression extends PropertyExpression<String, String> {
    public DescriptionExpression() {
        Skript.registerExpression(DescriptionExpression.class, String.class, ExpressionType.PROPERTY,
                "[the] [open]guild[[']s] description of %guild%");
    }
    
    @Override
    protected String[] get(final Event event, String[] fs) {
        return get(fs, new Converter<String, String>() {
            
            @Override
            public String convert(String f) {
                if(event instanceof GuildEvent) {
                    return ((GuildEvent) event).getGuild().getDescription();
                } else if(event instanceof GuildCreateEvent) {
                    return ((GuildCreateEvent) event).getDescription();
                }
                return null;
            }
        });
    }
    
    @Override
    public Class<? extends String> getReturnType() {
        return String.class;
    }
    
    @Override
    public boolean init(Expression<?>[] exprsns, int i, Kleenean kln, SkriptParser.ParseResult pr) {
        setExpr((Expression<String>) exprsns[0]);
        return true;
    }
    
    @Override
    public String toString(Event event, boolean bln) {
        return "the description " + getExpr().toString(event, bln);
    }
}
