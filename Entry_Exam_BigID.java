import java.io.*;
import java.util.ArrayList;
import java.util.Collections;

/*
Author: Phillip Salowe
Email: pjsalowe@gmail.com
Phone: (408) 763 - 9443

This code is the intro project for the summer 2021 internship position at BigID.
This code reads from a .txt file and searches for the 50 most common American names. WHen a name is found,
its position in the document is recorded by printing out the line offset and the amount of charactors after the offset.

The code uses Arrays, ArrayLists, threads, file readers, and more

This code WILL find (using Carl as the example name):  "Carl", "Carl" proceeded by or followed by any symbol
                                                       or "Carl" surrounded by any amount of whitespace, "Carl-jimmy"

This code will NOT find (using Carl as the example name): "Carlsbad", "Carlo", "Carls", "Carl123"

 */

public class Entry_Exam_BigID implements Runnable
{

    private String filename;
    private String[] reader_array1;          //these 6 arrays are the different arrays that the 6 threads are reading
    private String[] reader_array2;          //  from. Each array will contain 5000 lines from the .txt document
    private String[] reader_array3;
    private String[] reader_array4;
    private String[] reader_array5;
    private String[] reader_array6;

    ArrayList<String> final_list = new ArrayList<>();     //this is the final ArrayList and every name and position is stored in


    final Object object = new Object();        //this is the object that the threads will Synchronize around

    //below is an array full of all the names that the program will search for

    String[] names = {"James","John","Robert","Michael","William","David","Richard","Charles","Joseph",
            "Thomas","Christopher","Daniel","Paul","Mark","Donald","George","Kenneth","Steven",
            "Edward","Brian","Ronald","Anthony","Kevin","Jason","Matthew","Gary","Timothy","Jose","Larry","Jeffrey",
            "Frank","Scott","Eric","Stephen","Andrew","Raymond","Gregory","Joshua","Jerry","Dennis","Walter","Patrick",
            "Peter","Harold","Douglas","Henry","Carl","Arthur","Ryan","Roger"};


    public static void main(String[] args) throws InterruptedException {
        String filename = "text.txt";            //this stores the name of the .txt file the program will read from

        Entry_Exam_BigID entry_exam_bigID = new Entry_Exam_BigID(filename);
        Thread t1 = new Thread(entry_exam_bigID);     //id 14
        Thread t2 = new Thread(entry_exam_bigID);     //id 15
        Thread t3 = new Thread(entry_exam_bigID);     //id 16
        Thread t4 = new Thread(entry_exam_bigID);     //id 17
        Thread t5 = new Thread(entry_exam_bigID);     //id 18
        Thread t6 = new Thread(entry_exam_bigID);     //id 19

        t1.start();           //starts all of the threads. Each thread will sort through 5000 lines of the article
        t2.start();
        t3.start();
        t4.start();
        t5.start();
        t6.start();

        t1.join();           //will join all of the threads. This will force all threads to finish before moving on
        t2.join();           //    and printing the names and positions to the screen
        t3.join();
        t4.join();
        t5.join();
        t6.join();

        entry_exam_bigID.aggregator();             //when all threads are joined and finished, this will print everything
    }


    public Entry_Exam_BigID(String filename) {
        this.filename = filename;
        String[] reader_array1 = new String[5001];        //initialization for the arrays
        String[] reader_array2 = new String[5001];;
        String[] reader_array3 = new String[5001];;
        String[] reader_array4 = new String[5001];;
        String[] reader_array5 = new String[5001];;
        String[] reader_array6 = new String[5001];;

        this.reader_array1 = reader_array1;
        this.reader_array2 = reader_array2;
        this.reader_array3 = reader_array3;
        this.reader_array4 = reader_array4;
        this.reader_array5 = reader_array5;
        this.reader_array6 = reader_array6;

        int num, a = 0, b = 0, c = 0, d = 0, e = 0, f = 0;   //these are the variables used to keep track of position withing the arrays
        num = 0;       //this keeps count of the amount of lines read into the initial "reader" array

        try {
            FileReader fr = new FileReader(filename);
            BufferedReader bfr = new BufferedReader(fr);      //begins reading from the file

            while (true) {
                String line = bfr.readLine();
                if (line == null)                        //will exit loop when the array is finished
                    break;

                if (num < 5000) {
                    reader_array1[a] = line;       //this if/else will fill the 6 arrays with 5000 lines each from the file
                    a++;                           //  increases array element

                } else if (num < 10000) {
                    reader_array2[b] = line;
                    b++;

                } else if (num < 15000) {
                    reader_array3[c] = line;
                    c++;

                } else if (num < 20000) {
                    reader_array4[d] = line;
                    d++;

                } else if (num < 25000) {
                    reader_array5[e] = line;
                    e++;

                } else if (num < 30000) {
                    reader_array6[f] = line;
                    f++;
                }

                num++;                         //increase every line to keep track of line number
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }



    @Override
    public void run() {



        if (Thread.currentThread().getId() == 14)
            matcher(reader_array1, 0);
        else if (Thread.currentThread().getId() == 15)     //by using thread ID, this will send the correct array to the matcher
            matcher(reader_array2, 5000);
        else if (Thread.currentThread().getId() == 16)
            matcher(reader_array3, 10000);     //The array line number is off, so the offset needs to be added in
        else if (Thread.currentThread().getId() == 17)
            matcher(reader_array4, 15000);
        else if (Thread.currentThread().getId() == 18)
            matcher(reader_array5, 20000);
        else if (Thread.currentThread().getId() == 19)
            matcher(reader_array6, 25000);


        //System.out.println(Thread.currentThread().getId());

    }


    public void matcher(String[] reading_data, int line_Offset) {

        int line_num = 0;      //will be used to count the lines to create an offset every 1000 words
        int char_Offset = 0;      //will count the amount of charactors between line offset and the word
        line_num = line_Offset;  //sets line number to the correct value or else it would always start at 0
        int flag = 0;
        String[] counter;
        int num = 0;

            while (true) {

                String line = reading_data[num];       //reads a line from the array
                if (line == null)                      //ends loop if all values have been read
                    break;

                line = line.replaceAll("[^A-Za-z0-9]", " ");
                //line above replaces all not alphanumerical charactors with spaces

                //This code WILL find (using Carl as the example name):  "Carl", "Carl" proceeded by or followed by any symbol,
                //                                                        or "Carl" surrounded by any amount of whitespace

                //This code will NOT find: "Carlsbad", "Carlo", "Carls", "Carl123"


                String[] data = line.split(" ");      //splits up the line based on whitespace
                num++;                                       //keeps track of line number
                line_num++;


                for (int k = 0; k < names.length; k++) {      //goes through every name
                    for (int i = 0; i < data.length; i++) {    //goes through every word in the line
                        if (data[i].matches(names[k])) {

                            counter = line.split(data[i]);    //splits line in half. Second half is all charactors after the name that was found
                            if (counter.length > 1) {
                                flag = line.length() - (counter[1].length() + data[i].length());   //subtracts extra charactors from
                            }                                                                      //the end of the sentance

                            char_Offset += flag;            //flag is the unaccounted charactors before the end of the sentance

                            String sentence = (names[k] + "--> lineOffset = " + line_Offset + ", charOffset = " + char_Offset + "\n");
                                                             //above line creates the string that will later be printed
                            synchronized (object) {
                                final_list.add(sentence);       //this array is synchronized because every thread will add to it
                            }

                            char_Offset = char_Offset - flag;    //subtracts the beginning charactors from the charactor count
                        }                                        //because the next line will account for those charactors
                    }
                }

                char_Offset += line.length();               //adds on all the charactors from the line

                if (line_num % 1000 == 0) {     //needs to be 1000 so that the line offset is in increments of 1000
                    char_Offset = 0;
                    line_Offset = line_num;      //resets the line offset every 1000 charactors
                }
            }

    }

    public void aggregator() {

        Collections.sort(final_list);     //sorts the list Alphabetically so all names from every thread are together


        System.out.println(final_list);     //prints all names from the ArrayList to the screen



    }


}
