import java.io.*;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Scanner;

public class Questio3 {

    private static List<TextFileAnalysis> storage = new ArrayList<>();
    private static List<TextFileAnalysis> reverseCopy = new ArrayList<>();
    private static Hashtable<String, String> memorized = new Hashtable<String, String>();


    private static void ReadFile() {
        int count = 0;
        Scanner sc2 = null;
        try {
            sc2 = new Scanner(new File("Question3/Independence.txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        while (sc2.hasNextLine()) {
            Scanner s2 = new Scanner(sc2.nextLine());
            count = 1;
            while (s2.hasNext()) {
                String s = s2.next();
                storage.add(new TextFileAnalysis(s, count));
                count =0;
            }
            storage.add(new TextFileAnalysis("", count));
        }
    }

    public static String ReverseWord( String input) {
        StringBuilder builder = new StringBuilder(input);
        int length = builder.length();
        for (int i = 0; i < length/2 ; i++) {
            char current = builder.charAt(i);
            int otherEnd = length - i - 1;
            builder.setCharAt(i, builder.charAt(otherEnd)); // swap
            builder.setCharAt(otherEnd, current);
        }

        return builder.toString();
    }

    private static void Print() throws IOException {
        int count = 0;

        for(int i =0; i <storage.size(); i++){
            String combo = storage.get(i).getWord() + storage.get(i).getNewLine();
            if(memorized.contains(combo)){
                reverseCopy.add(new TextFileAnalysis(memorized.get(combo), storage.get(i).getNewLine()));

            }
            String word = (storage.get(i).getWord());
            memorized.put(combo, word);
            reverseCopy.add(new TextFileAnalysis(word, storage.get(i).getNewLine()));
        }
        String comboMatch ="";
        int stop = 0;
        int count2 = 0;
        for(int i =0; i <reverseCopy.size(); i++){

            if(reverseCopy.get(i).getNewLine() == 1){
                BufferedWriter writer = new BufferedWriter(
                        new FileWriter("Question1/output_question1.txt", true)  //Set true for append mode
                );
                for(int j = i-1; j >=stop; j--){
                       System.out.print(reverseCopy.get(j).getWord() + " ");
                        writer.write((reverseCopy.get(j).getWord() + " "));
                }
                writer.newLine();
                writer.close();
                stop = i;
                System.out.println();
            }
        }
        BufferedWriter writer = new BufferedWriter(
                new FileWriter("Question3/output_question1.txt", true)  //Set true for append mode
        );
        for(int j = reverseCopy.size()-1; j>=stop; j--){
            System.out.print(reverseCopy.get(j).getWord() + " ");
            writer.write((reverseCopy.get(j).getWord() + " "));

        }
        writer.close();

        System.out.println(count);
    }


    public static void main(String[] args) throws IOException {
        Instant start = Instant.now();
        ReadFile();
        Print();
        Instant finish = Instant.now();
        long timeElapsed = Duration.between(start, finish).toMillis();
        System.out.println("Create thread name " + Thread.currentThread().getName() + " " +
                timeElapsed +  " millis");

    }

}