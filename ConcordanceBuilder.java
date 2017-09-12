/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//package labb1adk17;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.LinkedList;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;

/**
 *
 * @author Josephine
 */
public class ConcordanceBuilder {
        private String inputFile = "index"; //var/tmp/Index.txt"; //"home/j/o/josthu/workspace/Lab1Konkordans/testtext"; // //"CorpusWords2.txt";
        private long[] hashArray = new long[30*30*30];
        
        private long ptrInKorpusPositions = 0;
        
        private String word;
        private long pos;

        private String prevWord = "";
        private int blockSize = 8;
        private int blockSizeWords = 50;
        private LinkedList<byte[]> currentWordPtrs = new LinkedList<byte[]>();

        FileOutputStream korpusWords;
        BufferedOutputStream korpusPositions;

        private boolean firstTime = true;


	private String alphabet = "\tabcdefghijklmnopqrstuvwxyzåäö";

        
        public ConcordanceBuilder() {
            
            initializeArray();
            
            try {
                FileInputStream fis = new FileInputStream(inputFile);
                Kattio io = new Kattio(fis);

                korpusWords = new FileOutputStream("KorpusWords");
                korpusPositions = new BufferedOutputStream(new FileOutputStream("KorpusPositions"));
                
                while (io.hasMoreTokens()) {
                    
                    word = io.getWord();

                    //putPointerInArray(word,ptr);
                    //ptr += word.getBytes().length + "\t".getBytes().length;

                    pos = io.getLong();
                    //ptr += pos.getBytes().length + "\n".getBytes().length;

                    putToIndexFiles(word, pos);
                   
                }
                savePrevWordToFile();
            try{
            	createHashFromKorpusWord();

            } catch(Exception e){
            	System.out.println("ERROR: " + e.getMessage());
            }

            	//tryToReadFromKorpusWords();
                //saveArrayToFile();
                
  
            } catch (FileNotFoundException ex) {
                Logger.getLogger(ConcordanceBuilder.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        

        private void putToIndexFiles(String w1, long korpusPtr) {
        	if (w1.equals(prevWord)) {
                saveStringToLinkedList(korpusPtr);

            } else {
                
                //First time
                if(prevWord.equals("")) {
                    saveStringToLinkedList(korpusPtr);

                    prevWord = w1;
                } else {

                	savePrevWordToFile();
                	
                    // Relocate the ptr in KorpusPositions file
                    ptrInKorpusPositions += blockSize * currentWordPtrs.size();

                    prevWord = w1;

                    saveLinkedListToFile();

                    currentWordPtrs.clear(); // Clear the linked list

                    saveStringToLinkedList(korpusPtr);
                                        
                }

            }

        }

        private void savePrevWordToFile(){
        	try{
        		// Save word and ptr to the KorpusWord file
            	byte[] wordByteArray = new byte[blockSizeWords];
				wordByteArray = putPaddingOnString(prevWord, blockSizeWords);

				byte[] positionByteArray = (ByteBuffer.allocate(blockSize)).putLong(ptrInKorpusPositions).array();

                korpusWords.write(wordByteArray);
                korpusWords.write(positionByteArray);
        	} catch(UnsupportedEncodingException e){
        		System.out.println("fail when saving to file" + e.getMessage());
            } catch(IOException e){
            	System.out.println("fail when saving to file" + e.getMessage());
            }
        }

        private void saveStringToLinkedList(long pointerAsString){
    		byte[] korpusPtrArray = (ByteBuffer.allocate(blockSize)).putLong(pointerAsString).array();
	    	currentWordPtrs.add(korpusPtrArray);        	 
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

        private byte[] putPaddingOnString(String stringToPadd, int sizeOfBlock){
        try{
        	if(stringToPadd.length() == sizeOfBlock){
        		return stringToPadd.getBytes("ISO-8859-1");
        	} else {
        		char[] paddingString = new char[sizeOfBlock];
	        	for(int i = 0; i < sizeOfBlock; i++){
	        		paddingString[i] = '\t';
	        	}

        		for(int i = 0; i < stringToPadd.length(); i++){
        			paddingString[i] = stringToPadd.charAt(i);
        		}
        		String tempString = new String(paddingString);
        		return tempString.getBytes("ISO-8859-1");
        	}
        } catch(UnsupportedEncodingException e){

        }
        	return new byte[50];
        }

        private void saveLinkedListToFile(){
            
        	try{
        		byte[] largeBlock = new byte[blockSize * currentWordPtrs.size()];
        		for(byte[] bPos : currentWordPtrs){
        			
                    korpusPositions.write(bPos);
        		}
        	} catch (IOException e){

        	}
        }

        private void createHashFromKorpusWord() throws Exception{
        	RandomAccessFile raf = new RandomAccessFile("KorpusWords","r");
        	Hash hashGenerator = new Hash();

        	System.out.println("Börjar läsa");

        	
        	while(raf.getFilePointer() < raf.length()){
        		byte[] currentWordBytes = new byte[blockSizeWords];
	        	raf.read(currentWordBytes);
	        	String word = new String(currentWordBytes, "ISO-8859-1");
	        	String trimmedWord = word.trim();

	        	int hashValue = hashGenerator.WordToIntHash(trimmedWord);
	        	long pointer = raf.readLong();

	        	if(hashArray[hashValue] == -1){
	        		hashArray[hashValue] = raf.getFilePointer() - (blockSizeWords + blockSize);
	        	}
        	}
        	saveArrayToFile();
        }



        private void tryToReadFromKorpusWords(){
        	try{
        		RandomAccessFile raf = new RandomAccessFile("KorpusWords","r");
        		RandomAccessFile raf2 = new RandomAccessFile("KorpusPositions","r");
                RandomAccessFile raf3 = new RandomAccessFile("../korpus","r");

	        	byte[] wordInBytes = new byte[blockSizeWords];

	        	raf.read(wordInBytes);
	        	//System.out.println("pointer is:" + raf.getFilePointer());
	        	long ptrForWord = raf.readLong();
	        	//System.out.println("pointer is now :" + raf.getFilePointer());


	        	System.out.println(new String(wordInBytes, "ISO-8859-1"));
	        	System.out.println(ptrForWord);


	        	byte[] wordInBytes2 = new byte[blockSizeWords];
	        	raf.read(wordInBytes2);
	        	//System.out.println("pointer is:" + raf.getFilePointer());
	        	long ptrForWord2 = raf.readLong();
	        	//System.out.println("pointer is now :" + raf.getFilePointer());
	        	raf2.seek(ptrForWord2);
	        	long firstPointerOfSecondWord = raf2.readLong();

	        	System.out.println(new String(wordInBytes2, "ISO-8859-1"));
	        	System.out.println(ptrForWord2);
	        	System.out.println(firstPointerOfSecondWord);

                raf3.seek(firstPointerOfSecondWord);

                byte[] aa = new byte[2];
                raf3.read(aa);
                System.out.println("aa = " + new String(aa, "ISO-8859-1"));

	        	raf.close();
        	} catch(IOException e){
                System.out.println(e.getMessage());
        	}
    		
        }
        
        
        private void initializeArray() {
            for (int i=0; i<hashArray.length; i++) {
                hashArray[i] = -1;
            }
        }

        private void saveArrayToFile() throws Exception{
        	FileOutputStream hashFile = new FileOutputStream("HashValues");

        	for(int i = 0; i < hashArray.length; i++){

				byte[] position = (ByteBuffer.allocate(blockSize)).putLong(hashArray[i]).array();
                hashFile.write(position);
        	}
        }
        
        
    public static void main(String[] args) {
        new ConcordanceBuilder();
    }
    
        
    
    
    
}
