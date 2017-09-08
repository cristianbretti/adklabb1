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

        private String inputFile = "index"; //var/tmp/Index.txt"; //"home/j/o/josthu/workspace/Lab1Konkordans/testtext"; // //"CorpusWords2.txt";
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
                    
                    
                    
                    pos = io.getWord();
                    ptr += pos.getBytes().length + "\n".getBytes().length;
                   
                }
                
                Hash hashCreater = new Hash();

            	int biggestHash = hashCreater.WordToIntHash("ööö");
            	long biggestIndexPointer = hashArray[biggestHash];
            	System.out.println("Biggest Pointer:" + biggestIndexPointer);
                //saveArrayToFile();
                
  
            } catch (FileNotFoundException ex) {
                Logger.getLogger(ConcordanceBuilder.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        
        
        private void putPointerInArray(String w, long p) {
            
            Hash hashCreater = new Hash();

            int hashValue = hashCreater.WordToIntHash(w);
            if(hashValue == -1){

            	System.out.println("-1!!!" + w);
            }
            
            if (hashArray[hashValue] == -1) {
                hashArray[hashValue] = p;
            }
            
        }
        
        
        private void initializeArray() {
            for (int i=0; i<hashArray.length; i++) {
                hashArray[i] = -1;
            }
        }

        private void saveArrayToFile(){
        	for(int i = 0; i < hashArray.length; i++){

        	}
        }
        
        
    public static void main(String[] args) {
        new ConcordanceBuilder();
    }
    
        
    
    
    
}
