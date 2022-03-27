package com.company;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

public class ClientHandler implements Runnable {
    private final Socket socket;
    private AtomicBoolean apply_update;
    
    private AtomicBoolean individual_update;

    PrintWriter writer;

    public void setIndividual_update(AtomicBoolean individual_update) {
        this.individual_update = individual_update;
    }

    private class Individual_Update implements Runnable
    {
        @Override
        public void run()
        {
            while (true)
                if(individual_update.get())
                {
                    setIndividual_update(new AtomicBoolean(false));
                    writer.println(Ball_Game.get_game_state());
                }
        }

    }

    public ClientHandler(Socket socket, AtomicBoolean apply_update) {
        this.socket = socket;
        this.apply_update = apply_update;
        this.individual_update = new AtomicBoolean(false);
    }

    @Override
    public void run()
    {
        String curr_player = Ball_Game.add_new_player();
        apply_update.set(true);


        System.out.println("New Player Joined: "+curr_player);

        try
        {
            Scanner scanner = new Scanner(socket.getInputStream());
            writer = new PrintWriter(socket.getOutputStream(), true);

            writer.println(curr_player);

            try
            {
                new Thread(new Individual_Update()).start();

                while (true)
                {
                    // the command only consists of a pass and updates are needed accordingly
                    String line = scanner.nextLine();
                    if(Ball_Game.pass_ball(curr_player, line))
                    {
                        apply_update.set(true);
                        System.out.println("Ball passed from player "+curr_player+" to player "+line);
                    }
                }

            }

            catch (Exception e)
            {
                /*
                writer.println("ERROR " + e.getMessage());
                socket.close();

                Ball_Game.player_leave(curr_player);
                apply_update.set(true);
                System.out.println("PLayer "+ curr_player +" disconnected.");
                System.out.println(Ball_Game.get_game_state());
                */
            }

        }

        catch (Exception e) {}
        finally
        {
            // updates when the player leaves as well
            Ball_Game.player_leave(curr_player);
            apply_update.set(true);
            System.out.println("PLayer "+ curr_player +" disconnected.");
            //System.out.println(Ball_Game.get_game_state());
        }

    }
}
