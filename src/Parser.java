import java.io.IOException;
import java.io.RandomAccessFile;


public class Parser
{
    private RandomAccessFile instructions; //the file with all the instructions
    private RandomAccessFile log; //the log file to echo comments/instructions

    // ----------------------------------------------------------
    /**
     * Create a new Parser object.
     * @param i the list of instructions
     * @param l the log file to echo instructions to
     */
    public Parser(RandomAccessFile i, RandomAccessFile l) {
        instructions = i;
        log = l;
    }

    public String nextInstruction() {

        String command; //the current instruction
        try
        {
            command = instructions.readLine(); //gets next line in command file
            if (command != null && command.charAt(0) == ';') { //checks if entire line is a comment
                log.writeBytes(command + "\n"); //echoes comment
                return nextInstruction(); //goes to the next instruction
            }
            return command; //returns the current command
        }
        catch ( IOException e )
        {
            System.err.println("Invalid File");
        }
        return null; //unable to process instruction
    }
}
