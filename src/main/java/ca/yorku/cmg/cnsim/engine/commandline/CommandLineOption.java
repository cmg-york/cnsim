package ca.yorku.cmg.cnsim.engine.commandline;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to define command-line options for the CommandLineParser.
 * <p>
 * This annotation should be applied to fields in the CommandLineParser class
 * that represent command-line options. It provides metadata about each option,
 * including its key, expected argument, description, whether it's required,
 * and any aliases.
 * </p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface CommandLineOption {
    /**
     * @return The primary key for this command-line option.
     */
    String key();

    /**
     * @return A description of the argument expected for this option, e.g., "<file>".
     */
    String argument();

    /**
     * @return A description of what this command-line option does.
     */
    String description();

    /**
     * @return True if this option is required, false otherwise. Defaults to false.
     */
    boolean required() default false;

    /**
     * @return An array of aliases for this option.
     */
    String[] aliases();
}