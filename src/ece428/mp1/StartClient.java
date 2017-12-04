package ece428.mp1;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;

public class StartClient {
    public static void main(final String[] args) throws Exception {
        final BufferedReader temp = new BufferedReader(new FileReader("../number.txt"));
        final int exclude = Integer.parseInt(temp.readLine());
        final ArrayList<GrepClient> grepClientArrayList = new ArrayList<GrepClient>();
        for (int i = 1; i <= 10; i++) {
            if (i != exclude) {
                String host = "fa17-cs425-g39-0" + Integer.toString(i) + ".cs.illinois.edu";
                if (i == 10) {
                    host = "fa17-cs425-g39-" + Integer.toString(i) + ".cs.illinois.edu";
                }
                final GrepClient grepClient = new GrepClient(host, 9090);
                grepClientArrayList.add(grepClient);
            }
        }

        final Scanner scanner = new Scanner(System.in);

        while (true) {
            final String cmd = scanner.nextLine();
            for (int i = 0; i < grepClientArrayList.size(); ++i) {
                final GrepClient current = grepClientArrayList.get(i);
                if (current.isAvailable() || current.openConnection()) {
                    current.writeData(cmd);
                    System.out.println("Server: " + Integer.valueOf(i + 1));
                    current.readData();
                    System.out.println();
                }
            }
        }
    }
}
