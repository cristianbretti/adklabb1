public class Konkordans {



public static void  main(String[] args) {
	String[] s = args;

	String input = s[0];

	Hash hashCreater = new Hash();
	int hashValue = hashCreater.WordToIntHash(input);

	long getPointerToKorpusPointers = getFirstPointer(hashValue);

	System.out.println(hashValue);

	//RandomAccessFile raf = new RandomAccessFile(inputFile,"r");
    //raf.seek(18);
    //System.out.println(hashArray[1]);
    //System.out.println(hashArray[62]);

    //System.out.println("read in raf: " + 
    //                  new String(raf.readLine().getBytes(), "ISO-8859-1"));
	
}

private static long getFirstPointer(int hashValue){
	int middleOfKorpusWord = 
}

}