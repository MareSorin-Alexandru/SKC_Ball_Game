package com.company;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;

public class ServerProgram
{
    private final static int port = 8888;

    //Thread Collection to add threads to so we can cyclic barrier wait for them to be upated
    static ArrayList<ClientHandler> client_handlers = new ArrayList<ClientHandler>();
    //flag for letting the server know the state of the game has changed and as such should push updates
    static AtomicBoolean update_everyone = new AtomicBoolean(false);

    //subclass / thread to push / sync updates
    private static class Updater_Thread implements Runnable
    {
        public Updater_Thread(){}

        @Override
        public void run()
        {
            while(true)
            {
                if (update_everyone.get())
                {
                    update_everyone.set(false);
                    Iterator<ClientHandler> it = client_handlers.iterator();
                    while(it.hasNext())
                        it.next().setIndividual_update(new AtomicBoolean(true));
                    System.out.println(Ball_Game.get_game_state());
                }
            }
        }

    }

    private static void RunServer()
    {
        ServerSocket serverSocket = null;
        try
        {
            //this is for spinning new threads
            serverSocket = new ServerSocket(port);
            System.out.println("Waiting for incoming connections...");

            //updater thread
            new Thread(new Updater_Thread()).start();

            while (true)
            {
                Socket socket = serverSocket.accept();
                ClientHandler aux_handler = new ClientHandler(socket, update_everyone);
                Thread aux_thread = new Thread(aux_handler);
                aux_thread.start();
                client_handlers.add(aux_handler);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }


    public static void main(String[] args)
    {
        RunServer();
    }

}
