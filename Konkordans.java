import java.io.RandomAccessFile;
import java.util.Scanner;

public class Konkordans {


private static int blockSize = 8;
private static int blockSizeWords = 50;
private static RandomAccessFile korpusWords;

public static void  main(String[] args) {
	try{

		korpusWords = new RandomAccessFile("KorpusWords", "r");

		String[] s = args;

		String input = s[0];

		String trimmedInput = input.trim();
		Hash hashCreater = new Hash();
		int hashKey = hashCreater.WordToIntHash(trimmedInput);
		System.out.println(hashKey);

		long pointerToFirstWordOfHash = getFirstPointer(hashKey);
		if(pointerToFirstWordOfHash == -1){
			System.out.println("No such word exists:");
			return;
		}

		long pointerToNextWordOfHash = getNextPointer(hashKey);


		long pointerToCorrectWord = searchForCorrectWord(pointerToFirstWordOfHash, pointerToNextWordOfHash, trimmedInput);
		long pointerToNextWord = getNextWord();

		nextPromt(pointerToCorrectWord, pointerToNextWord);
	} catch (Exception e){
		System.out.println("errooor: " + e.getMessage());
	}
	
	
}

private static long getFirstPointer(int hashKey) throws Exception{
	
	RandomAccessFile raf = new RandomAccessFile("HashValues", "r");
	long pointerInHash = hashKey * blockSize;
	raf.seek(pointerInHash);
	return raf.readLong();
	
}

private static long getNextPointer(int hashKey) throws Exception{
	RandomAccessFile raf = new RandomAccessFile("HashValues", "r");
	long nextPointerInHash = (hashKey + 1) * blockSize;
	raf.seek(nextPointerInHash);
	//If hashKey was the last key in the "array"
	if(raf.getFilePointer() == raf.length()){
		return -1;
	}
	long nextHashValue = raf.readLong();

	while(nextHashValue == -1){
		nextHashValue = raf.readLong();
	}

	return nextHashValue;
}

private static String getWordOfHashPointer(long pointerToKorpusWord) throws Exception{
	korpusWords.seek(pointerToKorpusWord);
	byte[] byteWord = new byte[blockSizeWords];
	korpusWords.read(byteWord);
	return new String(byteWord, "ISO-8859-1");

}

private static long searchForCorrectWord(long pointerToFirstWordOfHash, long pointerToNextWordOfHash, String searchString) throws Exception{
	String currentWord = getWordOfHashPointer(pointerToFirstWordOfHash);
	while(!currentWord.trim().equals(searchString) && korpusWords.getFilePointer() <= pointerToNextWordOfHash){
		korpusWords.skipBytes(8);
		currentWord = getWordOfHashPointer(korpusWords.getFilePointer());
	}
	if(currentWord.trim().equals(searchString)){
		return korpusWords.readLong();
	} else {
		return -1;
	}
}

private static long getNextWord() throws Exception{
	korpusWords.skipBytes(blockSizeWords);
	return korpusWords.readLong();
}

private static void nextPromt(long pointerToCorrectWord, long pointerToNextWord){
	long numberOfWords = (pointerToNextWord - pointerToCorrectWord) / blockSize;
	if(numberOfWords > 25){
		Scanner scanner = new Scanner(System.in);

		System.out.println("There is: " + numberOfWords + " instances of the word, do you want to print them all? (yes/no)");

		String answer = scanner.next();

		if(answer.equals("yes")){
			System.out.println("OMG OK!");
			printOutTheWords(pointerToCorrectWord, numberOfWords);
		} else {
			System.out.println("K BY");
		}

	}
	printOutTheWords(pointerToCorrectWord, numberOfWords);
}

private static void printOutTheWords(long pointerToWord, long numberOfWords){
	
}

}