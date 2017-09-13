public class Hash {

	private static String alphabet = "\tabcdefghijklmnopqrstuvwxyzäåö";

	public int WordToIntHash(String input){

		String inputString = input.toLowerCase();
          
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
}
