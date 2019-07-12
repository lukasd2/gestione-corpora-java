package it.unipi.gestione.corpora.utils;

public interface Tokenizer {
	/**
	 * Divide the given string into a list of words.
	 */
	public String[] split(String text);
}
