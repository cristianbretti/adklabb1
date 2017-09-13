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
        private String inputFile = "/var/tmp/index";
        private long[] hashArray = new long[30*30*30];
        
        private long ptrInKorpusPositions = 0;
        
        private String word;
        private long pos;

        private String prevWord = "";
        private int blockSize = 8;
        private int blockSizeWords = 50;
        private LinkedList<byte[]> currentWordPtrs = new LinkedList<byte[]>();

        FileOutputStream korpusWords;
        FileOutputStream korpusPositions;

        private boolean firstTime = true;


	private String alphabet = "\tabcdefghijklmnopqrstuvwxyzäåö";

        
        public ConcordanceBuilder() {
            
            initializeArray();
            
            try {
                FileInputStream fis = new FileInputStream(inputFile);
                Kattio io = new Kattio(fis);

                korpusWords = new FileOutputStream("KorpusWords");
                korpusPositions = new FileOutputStream("KorpusPositions");
                
                while (io.hasMoreTokens()) {
                    
                    word = io.getWord();

                    pos = io.getLong();

                    putToIndexFiles(word, pos);
                   
                }
                savePrevWordToFile();
				saveLinkedListToFile();
            try{
            	createHashFromKorpusWord();

            } catch(Exception e){
            	System.out.println("ERROR: " + e.getMessage());
            }
                
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
                    saveLinkedListToFile();

                    // Relocate the ptr in KorpusPositions file
                    ptrInKorpusPositions += blockSize * currentWordPtrs.size();
                    prevWord = w1;

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
				if(ptrInKorpusPositions == 100783528){
					System.out.println("de är lika");				
				}
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
        		for(byte[] bPos : currentWordPtrs){
        			
                    korpusPositions.write(bPos);
        		}
        	} catch (IOException e){

        	}
        }

        private void createHashFromKorpusWord() throws Exception{
        	RandomAccessFile raf = new RandomAccessFile("KorpusWords","r");
        	Hash hashGenerator = new Hash();
        	
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
