package com.company;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClientProgram implements AutoCloseable{

    final static int port = 8888;

    private static Scanner reader;
    private static PrintWriter writer;

    public static String player_ID;

    private static class update_gamestate implements Runnable
    {
        public update_gamestate(){}

        @Override
        public void run()
        {
            while (true)
            {
                String ball_carrier_line = reader.nextLine();
                System.out.println(ball_carrier_line);
                System.out.println(reader.nextLine());
                System.out.println("Player ID: " + player_ID);
                if(("Ball Carrier: "+player_ID).equals(ball_carrier_line))
                    System.out.println("You have the ball: You can pass it to a player from the list! by typing in their ID");
                System.out.println();
            }
        }

    }

    public static void main(String[] args)
    {
        try
        {
            // from keyboard
            Scanner scanner = new Scanner(System.in);

            //to and from socket

            // Connecting to the server and creating objects for communications
            Socket socket = new Socket("localhost", port);
            reader = new Scanner(socket.getInputStream());

            // Automatically flushes the stream with every command
            writer = new PrintWriter(socket.getOutputStream(), true);

            // call and get ID from the server
            player_ID = reader.nextLine();

            //start thread that updates Client gamestate
            //new update_gamestate().run();
            new Thread(new update_gamestate()).start();

            {
                System.out.println("Logged in successfully.");

                while (true)
                {
                    //handle input (passing) to whom
                    writer.println(scanner.nextLine());
                }
            }
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void close() throws Exception {
        reader.close();
        writer.close();
    }

}
