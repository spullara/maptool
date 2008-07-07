package net.rptools.maptool.client.macro;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@java.lang.annotation.Target(ElementType.TYPE)
public @interface MacroDefinition {
	String name();
	String[] aliases() default { };
	String description();
	boolean hidden() default false;
	boolean expandRolls() default true;
}
