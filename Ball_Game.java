/*
At  any  point  in  time,  exactly  one  player  has  the  virtual  ball.    This
player needs to decide who to pass the ball to.  (They are allowed to
pass the ball to themselves.)  Once the decision is made, they pass
the ball to the corresponding player.
New players can join the game at any time.  The number of players in
the game is unlimited.  Every player joining the game has to be as-
signed a unique ID that will not change until they leave the game and
will not be reused after they leave the game.  All the players including
the one with the ball will immediately learn about new players and,
hence, the current ball owner can decide to pass the ball to the player
who has just joint the game.
Any player can leave the game at any time (the client application can
be closed/killed).  If the player with the ball leaves the game, the
server passes the ball to one of the remaining players.
If there are no players in the game, the server waits until someone
joins the game in which case the first player to connect receives the
ball.
*/
package com.company;

import java.util.HashSet;

public class Ball_Game {
    private static HashSet<String> current_players = new HashSet<>();
    private static String Ball_Carrier = "";
    private static Integer last_given_ID = 0;

    //returns current list of players
    public static String get_players()
    {
        return current_players.toString();
    }

    //returns current ball carrier
    public static String getBall_Carrier()
    {
        return Ball_Carrier;
    }

    public static String get_game_state()
    {
        return "Ball Carrier: " + getBall_Carrier() + System.lineSeparator() + "Current Players: " + get_players();
    }

    // performs check and passes ball
    public static boolean pass_ball(String From, String To)
    {
        if ( From.equals(Ball_Carrier) && current_players.contains(To))
        {
            Ball_Carrier = To;
            return true;
        }
        return false;
    }

    //assign ID to new players
    public static String add_new_player()
    {
        last_given_ID++;

        // ID is an integer, first one not to be present in the list
        if( current_players.isEmpty() )
            Ball_Carrier = last_given_ID.toString();

        // add it to the list and return it
        current_players.add(last_given_ID.toString());
        return  last_given_ID.toString();
    }

    // if player leaves
    public static void player_leave(String leaver)
    {
        current_players.remove(leaver);

        // redistribute ball
        if (Ball_Carrier.equals(leaver))
        {
            if(current_players.isEmpty() == false)
                // assign to first available player
                Ball_Carrier = current_players.iterator().next();
            else
                Ball_Carrier = "";
        }

    }

}
