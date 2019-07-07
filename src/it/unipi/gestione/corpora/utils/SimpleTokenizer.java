package it.unipi.gestione.corpora.utils;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class SimpleTokenizer implements Tokenizer {
	private static final Pattern[] NOT_CONTRACTIONS = { 
			Pattern.compile("(?i)\\b(can)('t|not)\\b"),
			Pattern.compile("(?i)(.)(n't)\\b") };

	/**
	 * List of contractions adapted from Robert MacIntyre's tokenizer.
	 */
	private static final Pattern[] CONTRACTIONS2 = {
			Pattern.compile("(?i)(.)('ll|'re|'ve|'s|'m|'d)\\b"),
			Pattern.compile("(?i)\\b(D)('ye)\\b"), Pattern.compile("(?i)\\b(Gim)(me)\\b"),
			Pattern.compile("(?i)\\b(Gon)(na)\\b"), Pattern.compile("(?i)\\b(Got)(ta)\\b"),
			Pattern.compile("(?i)\\b(Lem)(me)\\b"), Pattern.compile("(?i)\\b(Mor)('n)\\b"),
			Pattern.compile("(?i)\\b(T)(is)\\b"), Pattern.compile("(?i)\\b(T)(was)\\b"),
			Pattern.compile("(?i)\\b(Wan)(na)\\b") };

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
