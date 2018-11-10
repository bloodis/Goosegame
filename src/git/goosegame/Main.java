package git.goosegame;

import java.util.*;

class Main {

    public static void main(String[] args) {

        int[] map = new int[64];
        int playerCount = 0;
        LinkedHashMap<Integer,String> players = new LinkedHashMap<>();
        String winner = null;

        Arrays.fill(map,-1);

        boolean stop = false;
        while (!stop){

            Scanner sc = new Scanner(System.in);

            if (playerCount < 2){

                System.out.println("There isn't enough player, add a player. ");
                System.out.print("Player name: ");

                players.put(playerCount,sc.next());

                playerCount++;
            }

            if (playerCount >= 2){


                System.out.print("There is enough player, add another player? y/n :");

                char c = sc.next().charAt(0);
                if (c != 'y' && c != 'Y' && c != 'n' && c != 'N'){

                    System.out.println("invalid choice, try again.");

                }else if (c == 'y'){

                    System.out.print("Player name: ");

                    players.put(playerCount,sc.next());

                    playerCount++;
                }else if (c == 'n'){

                    stop = true;
                }
            }

        }

        while(winner == null){

            Scanner sc = new Scanner(System.in);

            System.out.println("Chose the game mode:");
            System.out.println("1. Auto play");
            System.out.println("2. Auto roll");
            System.out.println("3. Manual roll");

            int selector = sc.nextInt();

            sc.close();

            switch (selector){

                case 1:

                    while(winner == null) {

                        for (Map.Entry<Integer, String> player : players.entrySet()) {

                            Integer playerID = player.getKey();

                            move(playerID,roll(),map,players);

                            if (map[63] != -1){

                                winner = players.get(map[63]);

                                break;
                            }
                        }

                    }

                break;

                case 2:

                    while(winner == null) {

                        Scanner sc1 = new Scanner(System.in);

                        System.out.println("Enter the player name:");

                        String playerName = sc1.next();
                        boolean valid = false;

                        for (Map.Entry<Integer, String> player : players.entrySet()){

                            Integer id = player.getKey();
                            String name = player.getValue();

                            System.out.println(name);

                            if(name.equals(playerName)){

                                valid = true;
                                move(id,roll(),map,players);

                                if (map[63] != -1){

                                    winner = players.get(map[63]);

                                    break;
                                }
                            }

                        }

                        if (!valid){

                            System.out.println(playerName + " is not a valid name");
                            continue;
                        }

                        sc1.close();
                    }

                break;

                case 3:

                    while(winner == null) {

                        Scanner sc2 = new Scanner(System.in);
                        System.out.println("Use the move command to move the players");

                        String input = sc2.nextLine();

                        String[] command = input.trim().replaceAll("[^a-zA-Z1-6]"," ").split("\\s");

                        if(!commandCheck(command,players)){

                            continue;
                        }

                        for (Map.Entry<Integer, String> player : players.entrySet()){

                            Integer id = player.getKey();
                            String name = player.getValue();

                            if(name.equals(command[1])){

                                move(id,roll(),map,players);

                                if (map[63] != -1){

                                    winner = players.get(map[63]);

                                    break;
                                }
                            }

                        }

                        sc2.close();
                    }

                break;
            }
        }

        System.out.println("The winner is:" + winner);

    }

    private static int[] roll(){

        int[] dices = {1,1};

        for (int i = 0; i < dices.length; i++) {

            dices[i] = (int) Math.ceil(Math.random() * 6);

        }

        return dices;
    }

    private static void move(int playerID,int[] diceResult, int[] mapArray, LinkedHashMap players){

        int playerPosition = 0;
        int newPosition;
        int diceSum = diceResult[0] + diceResult[1];

        System.out.println(players.get(playerID) + " rolled "+ diceResult[0] +","+ diceResult [1]);

        for (int i = 0; i < mapArray.length; i++){

            if (mapArray[i] == playerID) {
                playerPosition = i;
                mapArray[i] = -1;

            }
        }

        newPosition = playerPosition + diceSum;

        if (newPosition > 63){

            newPosition = 63 - (newPosition - 63);

            System.out.println( players.get(playerID) + " moved further then space 63! Bounce back to " + newPosition);
        }

        if (mapArray[newPosition] != -1){

            mapArray[playerPosition] = mapArray[newPosition];
            mapArray[newPosition] = playerID;

            System.out.println( players.get(playerID) + " moved in the same space of " + players.get(mapArray[playerPosition]) + "(" + newPosition + ")" + ". They will switch place!\n");

        }else {

            mapArray[newPosition] = playerID;
            playerPosition = newPosition;

            spaceCheck(mapArray,players, playerID,playerPosition,diceSum);

        }

    }

    private static void spaceCheck(int[] mapArray, LinkedHashMap players, int playerID, int playerPosition, int dices){

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

        while (playerPosition == 5 || playerPosition == 7 || playerPosition == 14 || playerPosition == 18 || playerPosition == 23 || playerPosition == 27){

            System.out.println( players.get(playerID) + " landed on the goose (" + playerPosition + ")." + players.get(playerID) + " move again.");

            mapArray[playerPosition] = -1;

            playerPosition += dices;

            mapArray[playerPosition] = playerID;
        }

        System.out.println( players.get(playerID) + " moved to space " + playerPosition + "\n");

    }

    private static boolean commandCheck(String[] command, LinkedHashMap<Integer,String> players){

        if (command.length > 4){

            System.out.println(" too much arguments");

            return false;
        }

        if (command.length < 4){

            System.out.println(" too few arguments");

            return false;
        }

        if (!command[0].equals("move")){

            System.out.println(command[0] + "is not a valid command");

            return false;
        }

        boolean name = false;

        for (Map.Entry<Integer, String> player : players.entrySet()){

            if (command[1].equals(player.getValue())){

                name=true;
            }
        }

        if (!name){

            System.out.println(command[1] + "is not a valid name");


            return false;
        }

        if (!command[2].matches("[1-6]")){

            System.out.println(command[2] + "is not a valid number");
            return false;
        }


        if (!command[3].matches("[1-6]")){

            System.out.println(command[3] + "is not a valid number");
            return false;
        }

        return true;
    }
}
