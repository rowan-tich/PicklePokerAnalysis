
import java.util.*;
import java.util.stream.IntStream;


public class Player {
    private double points;
    private String name;
    private String[] validValues = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};
    private String straightAsString = "";
    private ArrayList<Card> trueHand = new ArrayList<Card>();
    private ArrayList<Card> falseHand = new ArrayList<Card>();
    private ArrayList<Card> hand = new ArrayList<Card>();
    private boolean flush = false;
    private boolean potentialStraight = false;
    private boolean straight = false;
    private int handValue = 0;
    private int secondaryValue = 0;
    private int tertiaryValue = 0;
    public Player(String name, float points){
        setName(name);
        setPoints(points);
    }

    public void determineHands(){
        //System.out.println("");
        points =0;
        secondaryValue = 0;
        tertiaryValue = 0;
        handValue = 0;

        ArrayList<String> cardVals = new ArrayList<>();
        ArrayList<String> cardSuits = new ArrayList<>();
        ArrayList<Card> hand = getHand();
        for(Card card: hand){
            cardVals.add(card.getValue());
            cardSuits.add(card.getSuit());
        }
        setFlush(checkEquality(cardSuits));
        cardVals.sort(Comparator.comparingInt(Arrays.asList(validValues)::indexOf));

        //get frequency of card values
        int[] frequencyOfValues = {0,0,0,0,0,0,0,0,0,0,0,0,0};
        for(String i: validValues){
            frequencyOfValues[indexOf(validValues, i)] = Collections.frequency(cardVals, i);
        }
        for(int val: frequencyOfValues){
            straightAsString.concat(String.valueOf(val));
        }

        straight = checkStraight(frequencyOfValues);
        potentialStraight = checkPotentialStraight(straightAsString);

        /**
        System.out.print(name + "-- ");
        for(int i = 0; i < frequencyOfValues.length; i++){
            System.out.print(validValues[i] + ":" + frequencyOfValues[i] + " | ");
        }
        System.out.println("");
        **/

        //determine secondary vals set/pairs/quads
        for(int i = 0; i < frequencyOfValues.length; i++){
            if(frequencyOfValues[i] == 2){
                points+=2;
                secondaryValue = lastIndexOf(frequencyOfValues,2);
            }
            if(frequencyOfValues[i] == 3){
                points+=5;
                secondaryValue = i;
            }
            if(frequencyOfValues[i] == 4){
                points=8.0;
                secondaryValue = i;
            }
        }
        //determine secondary vals flushes/straights
        if(flush && straight){
            points=8.5;
            secondaryValue = lastIndexOf(frequencyOfValues,1);
        }else if(flush){
            points=6.5;
            secondaryValue = lastIndexOf(frequencyOfValues, 1);
        }else if(straight) {
            points=6.0;
            secondaryValue = lastIndexOf(frequencyOfValues, 1);
        }
        //determine tertiary vals for sets/pairs/quads/ and determine secondary vals for highs
        if(points == 4){
            secondaryValue = lastIndexOf(frequencyOfValues,2);
            tertiaryValue = indexOf(frequencyOfValues,2);
        }else if(points == 5){
            secondaryValue = lastIndexOf(frequencyOfValues,3);
            tertiaryValue = indexOf(frequencyOfValues,1);
        }else if(points == 2){
            tertiaryValue = lastIndexOf(frequencyOfValues,1);
        } else if (points == 0){
            secondaryValue = lastIndexOf(frequencyOfValues,1);
        }

        secondaryValue+=2;
        tertiaryValue+=2;
        //System.out.println(name + " " +points + " " + secondaryValue + " " + tertiaryValue + " ");

    }
    public static int indexOf(String[] arr, String val) {
        return IntStream.range(0, arr.length).filter(i -> arr[i] == val).findFirst().orElse(-1);
    }
    public static int indexOf(int[] arr, int val) {
        return Arrays.asList(arr).indexOf(val);
    }
    public boolean checkEquality(List<String> list) {
        return list.isEmpty() || Collections.frequency(list, list.get(0)) == list.size();
    }
    public boolean checkStraight(int[] arr){
        return Arrays.toString(arr).contains("1, 1, 1, 1, 1");
    }
    public boolean checkPotentialStraight(String str){return str.contains("1111");}

    public static int lastIndexOf(int[] a, int v) {
        for (int i = a.length-1; i >= 0; i--)
            if (a[i] == v)
                return i;
        return -1;
    }

    public void resetPlayer(){
        hand.clear();
        points=0;

    }


    //Hand Functions
    public void addCard(Card card){
        hand.add(card);
    }
    public void resetHand(){
        hand.clear();
    }
    public void addTrueCard(Card card){
        trueHand.add(card);
    }
    public void resetTrueHand(){
        trueHand.clear();
    }
    public void addFalseCard(Card card){
        falseHand.add(card);
    }
    public void resetFalseHand(){
        falseHand.clear();
    }

    public void printHand(){
        System.out.print(getName() + ": ");
        for(int i = 0; i < hand.size(); i++){
            hand.get(i).printCard();
        }
        System.out.println("");
    }
    public ArrayList<Card> getTrueHand(){
        return trueHand;
    }
    public ArrayList<Card> getFalseHand(){
        return falseHand;
    }
    public ArrayList<Card> getHand(){
        return hand;
    }



    public String getName(){
        return name;
    }
    public String getStraightAsString(){
        return straightAsString;
    }
    public double getPoints(){
        return points;
    }
    public void setPoints(double newPoints){
        points = newPoints;
    }
    public void setHandValue(int newHandValue){
        handValue = newHandValue;
    }
    public int getHandValue(){
        return handValue;
    }
    public int getSecondaryValue(){
        return secondaryValue;
    }
    public int getTertiaryValue(){
        return tertiaryValue;
    }
    public void setName(String newName){
        name = newName;
    }
    public void addPoints(int points){
        setPoints(getPoints()+points);
    }
    public void subtractPoints(int points){
        setPoints(getPoints()-points);
    }
    public void printPLayer(){
        System.out.println(getName() + " @ " + getPoints());
    }

    public boolean isPotentialStraight() {
        return potentialStraight;
    }

    public void setFlush(boolean flush) {
        this.flush = flush;
    }
}
