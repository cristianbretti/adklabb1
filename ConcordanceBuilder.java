/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//package labb1adk17;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.LinkedList;
/**
 *
 * @author Josephine
 */
public class ConcordanceBuilder {

        private String inputFile = "index"; //var/tmp/Index.txt"; //"home/j/o/josthu/workspace/Lab1Konkordans/testtext"; // //"CorpusWords2.txt";
        private long[] hashArray = new long[30*30*30];
        
        private Long ptrInKorpusPositions = new Long(0);
        
        private String word;
        private String pos;

        private String prevWord = "";
        private int blockSize = 4;
        private int blockSizeWords = 50;
        private LinkedList currentWordPtrs = new LinkedList();

        private boolean firstTime = true;


	private String alphabet = "\tabcdefghijklmnopqrstuvwxyzåäö";

        
        public ConcordanceBuilder() {
            
            initializeArray();
            
            
            try {
                FileInputStream fis = new FileInputStream(inputFile);
                Kattio io = new Kattio(fis);
                
                FileOutputStream korpusWords = new FileOutputStream("KorpusWords");
                FileOutputStream korpusPositions = new FileOutputStream("KorpusPositions");
                
                while (io.hasMoreTokens()) {
                    
                    word = io.getWord();
                    
                    putPointerInArray(word,ptr);
                    //ptr += word.getBytes().length + "\t".getBytes().length;

                    pos = io.getWord();
                    //ptr += pos.getBytes().length + "\n".getBytes().length;

                    putToIndexFiles(word, pos);
                   
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
        

        private void putToIndexFiles(String w1, String korpusPtr) {
            if (w1.equals(prevWord)) {

                byte[] korpusPtrArray = new byte[blockSize];
                korpusPtrArray = korpusPtr.getBytes("ISO-8859-1");
                currentWordPtrs.add(korpusPtrArray);

            } else {
                
                if(prevWord.equals("")) {
                    byte[] korpusPtrArray = new byte[blockSize];
                    korpusPtrArray = korpusPtr.getBytes("ISO-8859-1");
                    currentWordPtrs.add(korpusPtrArray);

                    prevWord = w1;
                } else {

                    // Save word and ptr to the KorpusWord file 
                    byte[] wordByteArray = new byte[blockSizeWords];
                    wordByteArray = prevWord.getBytes("ISO-8859-1");

                    byte[] ptrArray = new byte[blockSize];
                    ptrArray = ptrInKorpusPositions.getBytes("ISO-8859-1");

                    korpusWords.write(wordByteArray);
                    korpusWords.write(ptrArray);


                    // Relocate the ptr in KorpusPositions file
                    ptrInKorpusPositions += blockSize * currentWordPtrs.length;

                    prevWord = w1;
                    currentWordPtrs.clear(); // Clear the linked list

                    // Save
                    byte[] korpusPtrArray = new byte[blockSize];
                    korpusPtrArray = korpusPtr.getBytes("ISO-8859-1");
                    currentWordPtrs.add(korpusPtrArray);

                }



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
