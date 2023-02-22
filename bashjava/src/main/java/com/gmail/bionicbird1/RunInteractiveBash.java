package com.gmail.bionicbird1;

import java.io.*;

public class RunInteractiveBash {
    public static void main(String[] args) {
        try {
            ProcessBuilder pb = new ProcessBuilder("bash");
            pb.redirectErrorStream(true);
            final Process process = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
            final BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));

            Thread errorThread = new Thread() {
                public void run() {
                    try {
                        String line;
                        while ((line = errorReader.readLine()) != null) {
                            System.err.println(line);
                        }
                    } catch (IOException e) {
                        System.err.println(e);
                    }
                }
            };
            errorThread.start();

            Thread inputThread = new Thread() {
                public void run() {
                    try {
                        BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
                        String line;
                        while ((line = consoleReader.readLine()) != null) {
                            if (line.equals("exit")) {
                                process.destroy();
                                System.exit(0);
                            }
                            writer.write(line);
                            writer.write('\n');
                            writer.flush();
                        }
                    } catch (IOException e) {
                        System.err.println(e);
                    }
                }
            };
            inputThread.start();

            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            errorThread.join();
            inputThread.join();
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            System.err.println(e);
        }
    }
}