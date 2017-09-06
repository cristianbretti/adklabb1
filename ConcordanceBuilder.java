/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//package labb1adk17;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Josephine
 */
public class ConcordanceBuilder {

        private String inputFile = "test.txt"; //var/tmp/Index.txt"; //"home/j/o/josthu/workspace/Lab1Konkordans/testtext"; // //"CorpusWords2.txt";
        private long[] hashArray = new long[30*30*30];
        
        private long ptr = 0;
        
        private String word;
        private String pos;
        
	private String alphabet = "\tabcdefghijklmnopqrstuvwxyzåäö";

        
        public ConcordanceBuilder() {
            
            initializeArray();
            
            
            try {
                FileInputStream fis = new FileInputStream(inputFile);
                Kattio io = new Kattio(fis);
                
                
                while (io.hasMoreTokens()) {
                    
                    word = io.getWord();
                    
                    putPointerInArray(word,ptr);
                    ptr += word.getBytes().length + "\t".getBytes().length;
                    
                    //System.out.println(word);
                    
                    //System.out.println("pos1 :" + ptr);
                    
                    pos = io.getWord();
                    ptr += pos.getBytes().length + "\n".getBytes().length;
                    
                    //System.out.println(pos);
                    
                    // System.out.println("pos2: " + ptr);
                    
                    
                    
                }
                RandomAccessFile raf = new RandomAccessFile(inputFile,"r");
                raf.seek(18);
                System.out.println(hashArray[1]);
                System.out.println(hashArray[62]);

                
                System.out.println("read in raf: " + 
                        new String(raf.readLine().getBytes(), "ISO-8859-1"));
                
  
            } catch (FileNotFoundException ex) {
                Logger.getLogger(ConcordanceBuilder.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(ConcordanceBuilder.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        
        
        private void putPointerInArray(String w, long p) {
            
            int hashValue = wordToIntHash(w);
            
            if (hashArray[hashValue] == -1) {
                System.out.println("hit!!!");
                hashArray[hashValue] = p;
            }
            
        }
        
        private int wordToIntHash(String inputString){
            
            if (inputString.length() == 1) {
                return alphabet.indexOf(inputString.charAt(0));
            } else if (inputString.length() == 2) {
                return alphabet.indexOf(inputString.charAt(0))*30
                        + alphabet.indexOf(inputString.charAt(1));
            } else {
                String firstThree = inputString.substring(0,3);
                return alphabet.indexOf(firstThree.charAt(0))*900 
			+ alphabet.indexOf(firstThree.charAt(1))*30
			+ alphabet.indexOf(firstThree.charAt(2));
            }   

	}
        
        
        private void initializeArray() {
            for (int i=0; i<hashArray.length; i++) {
                hashArray[i] = -1;
            }
        }
        
        
    public static void main(String[] args) {
        new ConcordanceBuilder();
    }
    
        
    
    
    
}
