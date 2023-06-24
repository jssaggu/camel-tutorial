package com.jss.experiment.plainjava;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Scanner;

public class ReadFile {
    public static void main(String[] args) {
        String sfile = "/Users/jasvinder.saggu/projects/temp/camel-demo-in.txt";
        String dfile = "/Users/jasvinder.saggu/projects/temp/camel-demo-out.txt";
        copyFile(sfile, dfile);
    }

    public static void read() {
        try {
            File source = new File("/Users/jasvinder.saggu/projects/temp/camel-demo-in.txt");
            Scanner sourceReader = new Scanner(source);

            while (sourceReader.hasNextLine()) {
                String data = sourceReader.nextLine();
                System.out.println(data);
            }
            sourceReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public static void copyFile(String sfile, String dfile) {
        try {
            FileReader fin = new FileReader(sfile);
            FileWriter fout = new FileWriter(dfile, true);
            int c;
            while ((c = fin.read()) != -1) {
                fout.write(c);
            }
            System.out.println("Copy finish...");
            fin.close();
            fout.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
