import com.google.common.base.Stopwatch;

import java.util.*;
import java.util.stream.IntStream;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class Main {
    static Stack<Card> deck = new Stack<Card>();
    static int round = 0;
    static String[] validValues = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};
    static String[] validSuits = {"Spade", "Club", "Diamond", "Heart"};
    static int startingPoints = 0;
    static int handSize = 5;
    static int dealerHand = 0; //HAS TO BE ODD OR 0
    static int maxRounds = 10000;
    static Player blue = new Player("Blue", startingPoints);
    static Player yellow = new Player("Yellow", startingPoints);
    static Player grey = new Player("Grey", startingPoints);
    static Player green = new Player("Green", startingPoints);
    static Player dealer = new Player("Dealer", 0);
    static ArrayList<Player> players = new ArrayList<Player>();

    //stats for analysis
    static double[] handIndices = {0, 2.0, 4.0, 5.0, 6.0, 6.5, 7.0, 8.0, 8.5};
    static int[] highCard = {0,0,0,0,0,0,0,0,0,0,0,0,0};
    static int[] pair = {0,0,0,0,0,0,0,0,0,0,0,0,0};
    static int[] twoPair = {0,0,0,0,0,0,0,0,0,0,0,0,0};
    static int[] set = {0,0,0,0,0,0,0,0,0,0,0,0,0};
    static int[] straight = {0,0,0,0,0,0,0,0,0,0,0,0,0};
    static int[] flush = {0,0,0,0,0,0,0,0,0,0,0,0,0};
    static int[] fullHouse = {0,0,0,0,0,0,0,0,0,0,0,0,0};
    static int[] quad = {0,0,0,0,0,0,0,0,0,0,0,0,0};
    static int[] straightFlush = {0,0,0,0,0,0,0,0,0,0,0,0,0};
    static String[] handNames = {"High Card", "Pair", "Two Pair", "Set", "Straight", "Flush", "Full House", "Quad", "Straight Flush"};
    static ArrayList<int[]> handWins = new ArrayList<>();

    public static void main(String[] args){
        Stopwatch stopwatch = Stopwatch.createStarted();
        populatePlayers();
        initializeStatLists();
        while(round < maxRounds){
            newRound();
            dealerSetup();

            for(int i = 0; i < players.size(); i++){
                //players.get(i).printHand();
                players.get(i).determineHands();
            }

            replaceCard();
            for(int i = 0; i < players.size(); i++){
                //players.get(i).printHand();
                players.get(i).determineHands();
            }
            recordStats();
        }
        //System.out.println("");
        printStats();
        stopwatch.stop();
        long millis = stopwatch.elapsed(MILLISECONDS);
        System.out.println("Time elapsed: " + stopwatch);
    }

    public static void printStats() {
        float maxRs = maxRounds;
        float trueWinPercentage = 0;
        for(int i = 0; i < handWins.size(); i++){
            System.out.println(handNames[i] + " wins:");
            System.out.println("Card Value - Amount of Wins - Percent it Won - Percent will Win Overall");
            float specificHandWins = 0;
            for(int j = 0; j < handWins.get(i).length; j++){
                float wins = handWins.get(i)[j];
                float percentOfWins = round2(((wins / maxRs) * 100),3);
                trueWinPercentage+=percentOfWins;
                if(j == 8){
                    System.out.println(validValues[j] + "-\t\t" + handWins.get(i)[j] + " times - " + percentOfWins + "% - "+ round2(trueWinPercentage, 2) + "%");
                }else {
                    System.out.println(validValues[j] + " -\t\t" + handWins.get(i)[j] + " times - " + percentOfWins + "% - "+ round2(trueWinPercentage, 2) + "%");
                }
                specificHandWins += handWins.get(i)[j];
            }
            float totalPercentOfWins = round2(((specificHandWins / maxRs) * 100),3);
            //trueWinPercentage+=totalPercentOfWins;
            System.out.println("Total wins in this hand: " + specificHandWins + " - " + totalPercentOfWins + "%");
            System.out.println("True win percentage: " + round2(trueWinPercentage, 2) + "%");
            System.out.println("");
        }
    }
    public static void initializeStatLists(){
        handWins.add(highCard);
        handWins.add(pair);
        handWins.add(twoPair);
        handWins.add(set);
        handWins.add(straight);
        handWins.add(flush);
        handWins.add(fullHouse);
        handWins.add(quad);
        handWins.add(straightFlush);
    }
    public static void recordStats(){
        //System.out.println("winner points: "+ findWinner().getPoints());
        int handWinsIndex = indexOf(handIndices, findWinner().getPoints()); //gets the type of hand that the winning player had and returns its index for handWins to use to find the correct int[]
        //System.out.println("hand wins index: " + handWinsIndex);
        //System.out.println("winner secondary value: " + findWinner().getSecondaryValue());
        handWins.get(handWinsIndex)[findWinner().getSecondaryValue()-2]++;

    }
    public static Player findWinner(){
        double[] playerPoints = {0,0,0,0};
        int[] playerSecondaryPoints = {0,0,0,0};
        for(int i = 0; i < players.size(); i++){
            playerPoints[i] = players.get(i).getPoints();
        }
        for(int i = 0; i < players.size(); i++){
            playerSecondaryPoints[i] = players.get(i).getSecondaryValue();
        }

        double winnerPoints = maxValue(playerPoints);
        int winnerSecondaryPoints = maxValue(playerSecondaryPoints);
        List<Integer> winnerIndex = new ArrayList<>();

        for(int i = 0; i < winnerIndex.size(); i++){
            if(playerPoints[i] == winnerPoints){
                winnerIndex.add(indexOf(playerPoints, winnerPoints));
            }
        }
        if(winnerIndex.size()>1){
            int currentHighest = 0;
            for(int i = 0; i < winnerIndex.size(); i++){
                if(playerSecondaryPoints[i] > currentHighest){
                    currentHighest = playerSecondaryPoints[i];
                }
            }
            //System.out.println(players.get(indexOf(playerPoints, winnerSecondaryPoints)).getName() + " wins!");
            return players.get(indexOf(playerPoints, winnerSecondaryPoints));
        }else {
            //System.out.println(players.get(indexOf(playerPoints, winnerPoints)).getName() + " wins!");
            return players.get(indexOf(playerPoints, winnerPoints));
        }
    }
    public static double maxValue(double array[]){
        double max = Arrays.stream(array).max().getAsDouble();
        return max;
    }
    public static int maxValue(int array[]){
        int max = Arrays.stream(array).max().getAsInt();
        return max;
    }
    public static int indexOf(double[] arr, double val) {
        return IntStream.range(0, arr.length).filter(i -> arr[i] == val).findFirst().orElse(-1);
    }


    //These functions deal with setting up the deck, players, hands, etc
    public static Stack<Card> initializeDeck(){
        Stack<Card> newDeck = deck;

        for(int i = 0; i < validSuits.length; i++){
            for(int j = 0; j < validValues.length; j++){
                Card card = new Card(validSuits[i], validValues[j]);
                deck.add(card);
            }
        }
        return newDeck;
    }
    public static void newRound(){
        for(int i = 0; i < players.size(); i++){
            players.get(i).setPoints(0);
            players.get(i).resetHand();
        }
        deck.clear();

        deck = initializeDeck();
        Collections.shuffle(deck);
        deal(deck);
        round++;
        //System.out.println("");
        System.out.println(round +"/" + maxRounds);
    }
    public static void deal(Stack<Card> deck){
        for(int i = 0; i < handSize; i++){
            for(int j = 0; j < players.size(); j++){
                players.get(j).addCard(deck.pop());
            }
        }
    }
    public static void dealerSetup(){
        deck.pop();
        if(dealerHand==0){
            return;
        }
        for(int j = 0; j < dealerHand/2 + 1; j++){
            dealer.addCard(deck.pop());
        }
        for(int j = 0; j < dealerHand/2; j++){
            deck.pop();
            dealer.addCard(deck.pop());
        }
    }
    public static void populatePlayers(){
        players.add(blue);
        players.add(yellow);
        players.add(green);
        players.add(grey);
    }


    //These functions "play" the game

    //if have 4 of a suit draw another card
    //if have a pair get rid of other card etc
    //keep highest card unless lower than 10
    //if have possible straight lose other card ----------going to be very hard for late
    public static void replaceCard(){ //------------------------------------------------------------DOES NOT ITERATE THROUGH HAND CORRECTLY
        for(int i = 0; i < players.size(); i++){
            List<Card> found = new ArrayList<>();
            //System.out.println(players.get(i).getName());
            double playerPoints = players.get(i).getPoints();

            //flush thingy
            int[] suitCount = {0,0,0,0}; //spade, club, diamond, heart
            for(Card cards: players.get(i).getHand()){
                for(String suit: validSuits){
                    if(cards.getSuit() == suit){
                        suitCount[indexOf(validSuits, suit)]++;
                    }
                }
            }
            boolean potentialFlush = false;
            String potentialFlushSuit = "";
            for(int suitCounts: suitCount){
                if(suitCounts == 4){
                    potentialFlushSuit = validSuits[getArrayIndex(suitCount, suitCounts)];
                    potentialFlush=true;
                }
            }
            String incorrectVal = "";
            if(players.get(i).isPotentialStraight()){
                if(players.get(i).getStraightAsString().indexOf("01") != -1){
                    incorrectVal = validValues[players.get(i).getStraightAsString().indexOf("01")];
                }else if(players.get(i).getStraightAsString().indexOf("10") != -1) {
                    incorrectVal = validValues[players.get(i).getStraightAsString().indexOf("01")];
                }else{
                    incorrectVal = players.get(i).getHand().get(0).getValue();
                }
            }

            if(playerPoints == 0){
                //check if potential straight else get rid of all but the highest card
                if(players.get(i).isPotentialStraight()){
                    for(Card cardToBeRemoved: players.get(i).getHand()){
                        if(cardToBeRemoved.getValue() != incorrectVal){
                            found.add(cardToBeRemoved);
                        }
                    }
                }else if(potentialFlush){
                    for(Card cardToBeRemoved: players.get(i).getHand()){
                        if(cardToBeRemoved.getSuit() != potentialFlushSuit){
                            found.add(cardToBeRemoved);
                        }
                    }
                }/**else{
                    for(Card cardToBeRemoved: players.get(i).getHand()){
                        if(cardToBeRemoved.getNumericalValue() != (players.get(i).getSecondaryValue()-2)){
                            found.add(cardToBeRemoved);
                        }
                    }
                }**/
                players.get(i).getHand().removeAll(found);
            }else if(playerPoints == 2){
                //get rid of all but pair
                if(players.get(i).isPotentialStraight()){
                    for(Card cardToBeRemoved: players.get(i).getHand()){
                        if(cardToBeRemoved.getValue() != incorrectVal){
                            found.add(cardToBeRemoved);
                        }
                    }
                }else if(potentialFlush){
                    for(Card cardToBeRemoved: players.get(i).getHand()){
                        if(cardToBeRemoved.getSuit() != potentialFlushSuit){
                            found.add(cardToBeRemoved);
                        }
                    }
                }else{
                    for(Card cardToBeRemoved: players.get(i).getHand()){
                        if(cardToBeRemoved.getNumericalValue() != (players.get(i).getSecondaryValue()-2)){
                            found.add(cardToBeRemoved);
                        }
                    }
                }
                players.get(i).getHand().removeAll(found);
            }else if(playerPoints == 4){
                //get rid of last card
                for(Card cardToBeRemoved: players.get(i).getHand()){
                    if(cardToBeRemoved.getNumericalValue() != (players.get(i).getSecondaryValue()-2) || cardToBeRemoved.getNumericalValue() != (players.get(i).getTertiaryValue()) - 2){
                        found.add(cardToBeRemoved);
                    }
                }
                players.get(i).getHand().removeAll(found);
            }else if(playerPoints == 5){
                //get rid of the other two cards
                for(Card cardToBeRemoved: players.get(i).getHand()){
                    if(cardToBeRemoved.getNumericalValue() != (players.get(i).getSecondaryValue()-2)){
                        found.add(cardToBeRemoved);
                    }
                }
                players.get(i).getHand().removeAll(found);
            }else if(playerPoints == 8){
                //get rid of last card
                for(Card cardToBeRemoved: players.get(i).getHand()){
                    if(cardToBeRemoved.getNumericalValue() != (players.get(i).getSecondaryValue()-2)){
                        found.add(cardToBeRemoved);
                    }
                }
                players.get(i).getHand().removeAll(found);
            }
            for(int j = players.get(i).getHand().size(); j < handSize; j++){
                players.get(i).addCard(deck.pop());
            }
        }
    }


    //These functions record the data
    public static float round2(float number, int scale) {
        int pow = 10;
        for (int i = 1; i < scale; i++)
            pow *= 10;
        float tmp = number * pow;
        return ( (float) ( (int) ((tmp - (int) tmp) >= 0.5f ? tmp + 1 : tmp) ) ) / pow;
    }
    public static int indexOf(String[] arr, String val) {
        return IntStream.range(0, arr.length).filter(i -> arr[i] == val).findFirst().orElse(-1);
    }
    public static int getArrayIndex(int[] arr, int value) {

        int k=0;
        for(int i=0;i<arr.length;i++){

            if(arr[i]==value){
                k=i;
                break;
            }
        }
        return k;
    }
}
