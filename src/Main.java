import java.util.*;

class Main {

    public static void main(String[] args) {

        //variable declaration
        int[] map = new int[64];
        int playerCount = 0;
        LinkedHashMap<Integer,String> players = new LinkedHashMap<>();
        String winner = null;

        //preloading the hashmap in order to start the duplicate check for
        players.put(null,null);

        //filling map array with -1, each -1 means no player in that position
        Arrays.fill(map,-1);

        //players name insertion loop
        boolean stop = false;
        while (!stop){

            Scanner sc = new Scanner(System.in);
            String playerName;

            //the game can't start without at least 2 player
            if (playerCount < 2){

                boolean duplicatedEntry = false;

                System.out.println("There isn't enough player, add a player. ");
                System.out.print("Player name: ");

                playerName = sc.next();

                //for each entry check if there is already a player with that name
                for (Map.Entry<Integer, String> player : players.entrySet()) {

                    String name = player.getValue();

                    if (playerName.equals(name)){

                        System.out.println("Player " + playerName + " already exist");

                        //set the duplicate check variable to 'true' and break the loop to avoid overwriting
                        duplicatedEntry = true;
                        break;
                    }
                }

                //if there is no duplicated player insert the name and increase the player count
                if (!duplicatedEntry){

                    players.put(playerCount,playerName);
                    playerCount++;
                }

            }

            //if there is at least 2 player ask if more player want to join
            if (playerCount >= 2){

                boolean duplicatedEntry = false;

                System.out.print("There is enough player, add another player? y/n :");

                char c = sc.next().charAt(0);
                if (c != 'y' && c != 'n'){

                    System.out.println("invalid choice, try again.");

                }else if (c == 'y'){

                    System.out.print("Player name: ");

                    playerName = sc.next();

                    for (Map.Entry<Integer, String> player : players.entrySet()) {

                        String name = player.getValue();

                        if (playerName.equals(name)){

                            System.out.println("Player " + playerName + " already exist");

                            duplicatedEntry = true;
                            break;
                        }
                    }

                    if (!duplicatedEntry){

                        players.put(playerCount,playerName);

                        playerCount++;
                    }


                }else{

                    stop = true;

                    //remove the preloading entry to avoid nullPointerException
                    players.remove(null);
                }
            }
        }

        while(winner == null){

            Scanner sc = new Scanner(System.in);

            //ask for the game mode
            System.out.println("Chose the game mode:");
            System.out.println("1. Auto play");
            System.out.println("2. Auto roll");
            System.out.println("3. Manual roll");

            int selector = sc.nextInt();

            switch (selector){

                // if the players select 1 the game will autoplay
                case 1:

                    while(winner == null) {

                        //this for make a move for each player one at time
                        for (Map.Entry<Integer, String> player : players.entrySet()) {

                            Integer playerID = player.getKey();

                            //call the move method passing the playerID, the roll method result, the map in order
                            //to update the player position and the players list to switch players position if needed
                            move(playerID,roll(),map,players);

                            //if the position 63 of the map array is different from '-1' that
                            // means that someone has win and the loop will stop
                            if (map[63] != -1){

                                winner = players.get(map[63]);
                                break;
                            }
                        }
                    }
                 break;

                // if the players select 2 the game will auto roll the dices for the selected player
                case 2:

                    while(winner == null) {

                        // ask for the name of the player that have to roll
                        System.out.println("Who move?");

                        String playerName = sc.next();
                        boolean valid = false;

                        //check if the inserted name is a valid name
                        //in case it is true it will roll the dice and move the player
                        for (Map.Entry<Integer, String> player : players.entrySet()){

                            Integer id = player.getKey();
                            String name = player.getValue();

                            if(name.equals(playerName)){

                                valid = true;
                                move(id,roll(),map,players);

                                if (map[63] != -1){

                                    winner = players.get(map[63]);

                                    break;
                                }
                            }
                        }

                        // if the name is not valid the 'valid' variable will not change
                        // and the game will print an error skipping this iteration
                        if (!valid){

                            System.out.println(playerName + " is not a valid name");
                        }
                    }

                 break;

                // if the players select 3 they will play the game on their own
                // calling the move command with the name of the player and the dices rolls
                case 3:

                    while(winner == null) {

                        Scanner sc2 = new Scanner(System.in);

                        // the game ask for the command to move the selected player
                        System.out.println("Use the move command to move the players 'move playername dice1 dice2' :");

                        String input = sc2.nextLine();

                        // once the command is inserted it is trimmed
                        // have any wrong character replaced with a blank space and splitted in separated words
                        String[] command = input.trim().replaceAll("[^a-zA-Z0-9]"," ").split("\\s+");

                        // once the command is prepared for the check the game will call the 'commandCheck' method
                        // if the command is not valid it will throw a specific error
                        // asking to try again and skipping the iteration
                        if(!commandCheck(command,players)){

                            continue;
                        }

                        // if the command is valid the dices array will select the 2 number from the command
                        int[] dices = {Integer.parseInt(command[2]), Integer.parseInt(command[3])};

                        // the game will now search for the ID of the player
                        // once found it will call the 'move' method
                        for (Map.Entry<Integer, String> player : players.entrySet()){

                            Integer id = player.getKey();
                            String name = player.getValue();

                            if(name.equals(command[1])){

                                move(id,dices,map,players);

                                if (map[63] != -1){

                                    winner = players.get(map[63]);

                                    break;
                                }
                            }
                        }
                    }

                 break;
            }
        }

        // once the variable 'winner is changed it will printed before the game end
        System.out.println("The winner is:" + winner);

    }

    private static int[] roll(){

        int[] dices = {1,1};

        //randomize a number between 1 and 6 for each dices position
        for (int i = 0; i < dices.length; i++) {

            dices[i] = (int) Math.ceil(Math.random() * 6);

        }

        // return the dices array
        return dices;
    }

    private static void move(int playerID,int[] diceResult, int[] mapArray, LinkedHashMap players){

        int playerPosition = 0;
        int newPosition;
        int diceSum = diceResult[0] + diceResult[1];

        //print wich player have rolled the dice and what is the results
        System.out.println(players.get(playerID) + " rolled "+ diceResult[0] +","+ diceResult [1]);

        //search for the player position on the map and set it to '-1' to avoid multiple position for the same player
        for (int i = 0; i < mapArray.length; i++){

            if (mapArray[i] == playerID) {
                playerPosition = i;
                mapArray[i] = -1;

            }
        }

        // the new position is equals to the previous position plus the sum of the dice rolls
        newPosition = playerPosition + diceSum;

        // if the new position is greater than 63 the player will be send back of the remaining jump
        if (newPosition > 63){

            newPosition = 63 - (newPosition - 63);

            System.out.println( players.get(playerID) + " moved further then space 63! Bounce back to " + newPosition);
        }

        // if the new position on the map is not set to '-1' that means that there is already a player in that position
        // and the 2 player will switch position
        if (mapArray[newPosition] != -1){

            mapArray[playerPosition] = mapArray[newPosition];
            mapArray[newPosition] = playerID;

            System.out.println( players.get(playerID) + " moved in the same space of " + players.get(mapArray[playerPosition]) + "(" + newPosition + ")" + ". They will switch place!\n");

        }else {

            // else the position is free an the player can move in, now the game will check if the new position
            // is an 'event' space calling the method 'spaceCheck'
            mapArray[newPosition] = playerID;
            playerPosition = newPosition;

            spaceCheck(mapArray,players, playerID,playerPosition,diceSum);

        }

    }

    private static void spaceCheck(int[] mapArray, LinkedHashMap players, int playerID, int playerPosition, int dices){

        // if the position of the player is the 6th space it will trigger the bridge
        // sending the player to the 12th space and eventually switching his place with the player in that position
        if (playerPosition == 6){

            if (mapArray[12] != -1){

                mapArray[6] = mapArray[12];
                mapArray[12] = playerID;

                System.out.println( players.get(playerID) + " moved to the bridge, on the other side there is " + players.get(mapArray[playerPosition]) + "( 12 )" + ". They will switch place!\n");
            }else {

                mapArray[12] = playerID;
                mapArray[6] = -1;

                System.out.println( players.get(playerID) + " moved to the other side of the bridge. The new position is the space 12");
            }
        }

        // while the player is on the 5th,7th,14th,18th,23th or 27th position it will keep moving forward
        // of the same rolled space eventually switching position with other player
        while (playerPosition == 5 || playerPosition == 7 || playerPosition == 14 || playerPosition == 18 || playerPosition == 23 || playerPosition == 27){

            System.out.println( players.get(playerID) + " landed on the goose (" + playerPosition + ")." + players.get(playerID) + " move again.");

            if (mapArray[playerPosition + dices] != -1){

                mapArray[playerPosition] = mapArray[playerPosition + dices];
                mapArray[playerPosition + dices] = playerID;

                System.out.println( players.get(playerID) + " moved in the same space of " + players.get(mapArray[playerPosition + dices]) + "(" + (playerPosition + dices) + ")" + ". They will switch place!\n");

            }else {

                mapArray[playerPosition] = -1;

                playerPosition += dices;

                mapArray[playerPosition] = playerID;

            }
        }

        //the game will now print the new position
        System.out.println( players.get(playerID) + " moved to space " + playerPosition + "\n");

    }

    private static boolean commandCheck(String[] command, LinkedHashMap<Integer,String> players){

        // if the length of the command array is more than 4 space, it is not valid
        if (command.length > 4){

            System.out.println(" too much arguments");

            return false;
        }

        // if the length of the command array is less than 4 space, it is not valid
        if (command.length < 4){

            System.out.println(" too few arguments");

            return false;
        }

        // if the first word is not 'move' the command is not valid
        if (!command[0].equals("move")){

            System.out.println(command[0] + " is not a valid command");

            return false;
        }

        // if the second word is not a player name, the command is not valid
        boolean name = false;

        for (Map.Entry<Integer, String> player : players.entrySet()){

            if (command[1].equals(player.getValue())){

                name=true;
            }
        }

        if (!name){

            System.out.println(command[1] + " is not a valid name");


            return false;
        }

        // if the last 2 string is not 2 number between 1 and 6 the command is not valid
        if (!command[2].matches("[1-6]")){

            System.out.println(command[2] + " is not a valid number");
            return false;
        }


        if (!command[3].matches("[1-6]")){

            System.out.println(command[3] + " is not a valid number");
            return false;
        }

        // if note of the if is triggered the command is valid
        return true;
    }
}
