/* Copyright 2004-2005 the original author or authors.
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
package org.codehaus.groovy.grails.cli;

import org.codehaus.groovy.runtime.DefaultGroovyMethods;

import java.io.*;

/**
 * Utility methods for use on the command line, including method to accept user input etc.
 *
 * @author Graeme Rocher
 * @since 1.2
 */
public class CommandLineHelper {

    // Use static variables so that feature uses of CommandLineHelper use those provided by the constructor
    // bit of a hack, but don't see many other options
    private static PrintStream out = System.out;
    private static InputStream input = System.in;

    public CommandLineHelper() {
        // default
    }

    public CommandLineHelper(PrintStream out) {
        CommandLineHelper.out = out;
    }

    public CommandLineHelper(InputStream input, PrintStream out) {
        CommandLineHelper.out = out;
        CommandLineHelper.input = input;
    }

    /**
     * Replacement for AntBuilder.input() to eliminate dependency of
     * GrailsScriptRunner on the Ant libraries. Prints a message and
     * returns whatever the user enters (once they press &lt;return&gt;).
     * @param message The message/question to display.
     * @return The line of text entered by the user. May be a blank
     * string.
     */
    public String userInput(String message) {
        return userInput(message, null);
    }

    /**
     * Replacement for AntBuilder.input() to eliminate dependency of
     * GrailsScriptRunner on the Ant libraries. Prints a message and
     * list of valid responses, then returns whatever the user enters
     * (once they press &lt;return&gt;). If the user enters something
     * that is not in the array of valid responses, the message is
     * displayed again and the method waits for more input. It will
     * display the message a maximum of three times before it gives up
     * and returns <code>null</code>.
     * @param message The message/question to display.
     * @param validResponses An array of responses that the user is
     * allowed to enter. Displayed after the message.
     * @return The line of text entered by the user, or <code>null</code>
     * if the user never entered a valid string.
     */
    public String userInput(String message, String[] validResponses) {
        String responsesString = null;
        if (validResponses != null) {
            responsesString = DefaultGroovyMethods.join(validResponses, ",");
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(input));

        for (int it = 0; it < 3; it++) {
            out.print(message);
            if (responsesString != null) {
                out.print(" [");
                out.print(responsesString);
                out.print("] ");
            }

            try {
                String line = reader.readLine();

                if (validResponses == null) return line;

                for (String validResponse : validResponses) {
                    if (line != null && line.equalsIgnoreCase(validResponse)) {
                        return line;
                    }
                }

                out.println();
                out.println("Invalid option '" + line + "' - must be one of: [" + responsesString + "]");
                out.println();
            }
            catch (IOException ex) {
                System.out.println("ERROR: Could not read System.in due to:" + ex.getMessage() );
                return null;
            }
        }

        // No valid response given.
        out.println("No valid response entered - giving up asking.");
        return null;
    }
}
