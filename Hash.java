class Hash {

	private static String alphabet = "\tabcdefghijklmnopqrstuvwxyzåäö";

	public static void main(String[] args) {
		String inputString = "hejhej";
		System.out.println(wordToIntHash(inputString));

		for(int i = 0; i < 3; i++){
			for(int j = 0; j < 30; j++){
				for(int k = 0; k < 30; k++){

					StringBuilder sb = new StringBuilder();
					sb.append(alphabet.charAt(i));
					sb.append(alphabet.charAt(j));
					sb.append(alphabet.charAt(k));	

					String currentString = sb.toString();				
					int currentHash = wordToIntHash(currentString);

					System.out.println(currentString + "  " + currentHash);
				}
			}
		}

	}


	private static int wordToIntHash(String inputString){
		String firstThree = inputString.substring(0,3);

		return alphabet.indexOf(firstThree.charAt(0))*900 
						+ alphabet.indexOf(firstThree.charAt(1))*30
						 + alphabet.indexOf(firstThree.charAt(2));

	}
}