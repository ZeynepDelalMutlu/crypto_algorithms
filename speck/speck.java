import java.util.Scanner;
import java.util.Arrays;
import java.io.*;
import java.math.BigInteger;
import java.net.URL;
import java.nio.charset.StandardCharsets;

class speck{

	private static String key  =  "D9DA7BEA1A31D8AB";
	private static int roundNum  =  22;
	private static String[] rk = new String[roundNum+2]; //round keys
	
	public static void main(String[] args){
		
		System.out.println("\n~~Speck Cryptographic Encryption Algorithm~~\n");
		Scanner scan = new Scanner(System.in);
		String cipherText = ""; 
		boolean checkCipherText = true;
		
		generateKeys();
		
		for(int i = 0; i <= 255; i++){
			String textNo = scan.next();
			String plainText = scan.next();
			String givenCipherText = scan.next();
			
			cipherText = Encrypt(plainText);
			checkCipherText = CheckTheCipherText(givenCipherText, cipherText);
			
			System.out.println(textNo + "\t"+ plainText + "\t" + givenCipherText + "\t" + cipherText + "\t" + checkCipherText);
		}
	}
	
	//SPECK Encryption Algorithm
	public static String Encrypt(String plainText){
		String plainTextBits = StringToBit(plainText);
		String pt0 = plainTextBits.substring(0, plainTextBits.length()/2);
		String pt1 = plainTextBits.substring(plainTextBits.length()/2); 

		String ct0 = pt0, ct1 = pt1;
		String[] newXY = new String[2];
		String i_binaryStr = "";
		
		for(int i = 0; i <roundNum; i++){
			newXY = EncRound(ct1, ct0, rk[i]);
			ct1 = newXY[0];
			ct0 = newXY[1];
		}
		
		String cipherText = ct1 + ct0;
		int decimal = Integer.parseUnsignedInt(cipherText, 2);
		String CT = Integer.toHexString(decimal);
		return CT;
	}
	
	//Generate round keys
	public static void generateKeys(){
		String keyBits = StringToBit(key);
		String[] encRoundResult = new String[2];
		String D = keyBits.substring(0, keyBits.length()/4);
		String C = keyBits.substring(keyBits.length()/4, keyBits.length()/2);
		String B = keyBits.substring(keyBits.length()/2, 3*keyBits.length()/4);
		String A = keyBits.substring(3*keyBits.length()/4);
		String iStr = "";
		
		for(int i = 0; i < 22;){
			rk[i] = A;
			iStr = intTo16BitString(i);
			encRoundResult = EncRound(B, A, iStr);
			B = encRoundResult[0];
			A = encRoundResult[1];
			i++;
			
			rk[i] = A;
			iStr = intTo16BitString(i);
			encRoundResult = EncRound(C, A, iStr);
			C = encRoundResult[0];
			A = encRoundResult[1];
			i++;
			
			rk[i] = A; 
			iStr = intTo16BitString(i);
			encRoundResult = EncRound(D, A, iStr);
			D = encRoundResult[0];
			A = encRoundResult[1];
			i++;
		}
	}
	
	//Algorithm for one encryption round
	public static String[] EncRound(String x, String y, String k){
		String[] result = new String[2];
		x = RotateRight(x, 7);
		x = AdditionWithModulo(x, y);
		x = ExclusiveOr(x, key);
		y = RotateLeft(y, 2);
		y = ExclusiveOr(y, x);
		result[0] = x;
		result[1] = y;

		return result;
	}
	
	//Adding mod operation:
	public static String AdditionWithModulo(String x, String y){
		int xInt = 0;
		int yInt = 0;
		int modResult = 0;
		int modDecimal = Integer.parseUnsignedInt("FFFF", 16);
		
		xInt = Integer.parseUnsignedInt(x, 2);
		yInt = Integer.parseUnsignedInt(y, 2);
		modResult = (xInt + yInt) % modDecimal;		
		return intTo16BitString(modResult);
	}

	//Exclusive Or - XOR
	public static String ExclusiveOr(String str1, String str2){
		String result = "";
		
		for(int i = 0; i < str1.length(); i++){
			if(str1.charAt(i) == str2.charAt(i))
				result += "0";
			else
				result += "1";
		}
		return result;
	}
	
	// Rotate left
	public static String RotateLeft(String str, int rotationAmount){
		//return number << rotationAmount | number >> (32 - rotationAmount);
		return str.substring(rotationAmount) + str.substring(0, rotationAmount);
	}
	
	// Rotate right
	public static String RotateRight(String str, int rotationAmount){
		//return number >> rotationAmount | number << (32 - rotationAmount);
		return str.substring(str.length()-rotationAmount) + str.substring(0, str.length()-rotationAmount);
	}
	
	//Checking the test vectors
	public static boolean CheckTheCipherText(String givenCipherText, String cipherText){
		return givenCipherText.equals(cipherText);
	}
	
	//Convert hexadecimal text to bit text
    	private static String StringToBit(String plaintext){
        	StringBuilder bitsOfWholeString = new StringBuilder();
        	char oneDigitOfPlainTxt;
        	int intValueOfHexChar;
        	
        	if (IsHex(plaintext)) {
            		for (int i = 0; i < plaintext.length(); i++) {
                		oneDigitOfPlainTxt = plaintext.charAt(i);
                		intValueOfHexChar = HexDigit(oneDigitOfPlainTxt);
                		String bitString = Integer.toBinaryString(intValueOfHexChar);
                		if (bitString.length() < 4) {
                    			StringBuilder padding = new StringBuilder();
                    			for (int k = bitString.length(); k < 4; k++)
                       			padding.append("0");
                    			bitString = padding + bitString;
                		}
                	
                		bitsOfWholeString.append(bitString);
            		}
        	}
        		
        	return bitsOfWholeString.toString();
    	}
    	
    	public static int HexDigit(char ch) {
        	if (ch >= '0' && ch <= '9')
            		return ch - '0';
        	if (ch >= 'A' && ch <= 'F')
            		return ch - 'A' + 10;
        	if (ch >= 'a' && ch <= 'f')
            		return ch - 'a' + 10;
        	return(0);	// any other char is treated as 0
	}
	
	public static boolean IsHex(String hex) {
       	int len = hex.length();
		int i = 0;
		char ch;

		while (i < len) {
	     		ch = hex.charAt(i++);
	     		if (! ((ch >= '0' && ch <= '9') || (ch >= 'A' && ch <= 'F') ||
	        		(ch >= 'a' && ch <= 'f'))) return false;
		}
		return true;
    	}
    	
    	public static String intTo16BitString(int i){
    		return String.format("%16s", Integer.toBinaryString(i)).replace(' ', '0');
    	}
    	
}//end main
