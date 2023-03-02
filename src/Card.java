import java.util.Arrays;
import java.util.stream.IntStream;

public class Card {
    private String value;
    private String suit;
    private int numericalValue = 0;
    private String[] validValues = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};
    public Card(){
        setValue("2");
        setSuit("Spade");
    }
    public Card(String suit, String value){
        setSuit(suit);
        setValue(value);
        setNumericalValue(indexOf(validValues, value));
    }

    public String getSuit(){
        return suit;
    }
    public String getValue(){
        return value;
    }
    public void setValue(String newValue){
        value = newValue;
    }
    // Values run from 2 to 10 for none face cards
    // and then J, Q, K, A for face cards
    public void setSuit(String newSuit){
        suit = newSuit;
    }
    public void printCard(){
        System.out.print(getSuit() + " " + getValue() + " ");
    }
    public String returnCard(){
        return getSuit() + " " + getValue();
    }

    public int getNumericalValue() {
        return numericalValue;
    }

    public void setNumericalValue(int numericalValue) {
        this.numericalValue = numericalValue;
    }
    public static int indexOf(String[] arr, String val) {
        return IntStream.range(0, arr.length).filter(i -> arr[i] == val).findFirst().orElse(-1);
    }
}
