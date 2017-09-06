/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//package labb1adk17;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Josephine
 */
public class ConcordanceBuilder {

        private String inputFile = "test.txt"; //var/tmp/Index.txt"; //"home/j/o/josthu/workspace/Lab1Konkordans/testtext"; // //"CorpusWords2.txt";

        public ConcordanceBuilder() {
            
            try {
                FileInputStream fis = new FileInputStream(inputFile);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(ConcordanceBuilder.class.getName()).log(Level.SEVERE, null, ex);
            }

            Kattio io = new Kattio(fis);
        }
    
        
    
    
    
}
