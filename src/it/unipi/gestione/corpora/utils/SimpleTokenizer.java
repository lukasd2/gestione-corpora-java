package it.unipi.gestione.corpora.utils;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class SimpleTokenizer implements Tokenizer {

	private static final Pattern[] DELIMITERS = {
			// Separate most punctuation
			Pattern.compile("((?U)[^\\w\\.\\'\\-\\/,&])"),
			// Separate commas if they're followed by space (e.g., don't separate 2,500)
			Pattern.compile("(?U)(,\\s)"),
			// Separate single quotes if they're followed by a space.
			Pattern.compile("(?U)('\\s)"),
			// Separate periods that come before newline or end of string.
			Pattern.compile("(?U)\\. *(\\n|$)"),
			// Separate continuous periods such as ... in ToC.
			Pattern.compile("(?U)(\\.{3,})") 
	};
	
	private static final Pattern WHITESPACE = Pattern.compile("(?U)\\s+");

	@Override
	public String[] split(String text) {
		
		text = DELIMITERS[0].matcher(text).replaceAll(" $1 ");
        text = DELIMITERS[1].matcher(text).replaceAll(" $1");
        text = DELIMITERS[2].matcher(text).replaceAll(" $1");
        text = DELIMITERS[3].matcher(text).replaceAll(" . ");
        text = DELIMITERS[4].matcher(text).replaceAll(" $1 ");
		
        String[] words = WHITESPACE.split(text);
        
        ArrayList<String> result = new ArrayList<>();
        for (String token : words) {
                result.add(token);
        }
        return result.toArray(new String[result.size()]);
	}

}
