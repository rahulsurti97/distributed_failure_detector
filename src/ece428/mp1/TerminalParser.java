package ece428.mp1;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class TerminalParser {
    private String command;


    public TerminalParser() {
    }


    public TerminalParser(final String command) {
        this.command = command;
    }

    /**
     * @return The command that is held.
     */
    public String getCommand() {
        return this.command;
    }

    /**
     * Sets the command that we want to run on the shell.
     *
     * @param command The terminal command to hold.
     */
    public void setCommand(final String command) {
        this.command = command;
    }

    /**
     * Adds the "cat" file to the command so that user does not need to type it in.
     *
     * @param com The command that will be executed after being a pipe from "cat".
     * @return Entire command as one string.
     * @throws IOException
     */
    private String addCatToCommand(final String com) throws IOException {
        String returnValue = "";
        try {
//            final BufferedReader bufferedReader = new BufferedReader(new FileReader("../number.txt"));
            returnValue = "cat ../output.txt | " + com;
//            returnValue = "cat ../logs/vm" + bufferedReader.readLine() + ".log | " + com;
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return returnValue;
    }


    /**
     * Runs the command that's stored in the command variable.
     *
     * @throws InterruptedException
     * @throws IOException
     */
    public void runCommand(final Connection connection) throws InterruptedException, IOException {
        String line;
        final String newCommand = addCatToCommand(this.command);
        final String[] cmd = {"/bin/sh", "-c", newCommand};

        final Process process = Runtime.getRuntime().exec(cmd);
        final BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

        final DataOutputStream dataOutputStream = new DataOutputStream(connection.getSocket().getOutputStream());

        while ((line = reader.readLine()) != null) {
            dataOutputStream.writeUTF(line);
        }
        dataOutputStream.writeUTF("DONE");
        process.waitFor();
    }
}
