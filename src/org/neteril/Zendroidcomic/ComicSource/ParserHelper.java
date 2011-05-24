package org.neteril.Zendroidcomic.ComicSource;

import java.io.IOException;
import java.io.Reader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.util.CharArrayBuffer;

public class ParserHelper {
	private enum ParseState {
		Eating,
		Finding,
		Acquiring
	}
	
	public static String findTagAttribute (Reader reader, String tagName, String tagAttribute, Pattern pattern) {
		CharArrayBuffer buffer = new CharArrayBuffer(10);
		ParseState state = ParseState.Eating;
		
		char firstName = tagName.charAt(0);
		char firstAttr = tagAttribute.charAt(0);
		tagName = tagName.substring(1);
		tagAttribute = tagAttribute.substring(1);
		char[] tagNameBuffer = new char[tagName.length()];
		char[] tagAttributeBuffer = new char[tagAttribute.length()];
		
		try {
			while (true) {
				char current = safeNext(reader);
				if (current == (char)-1)
					return null;
				switch (state) {
				case Eating:
					if (current == '<' && safeNext(reader) == firstName) {
						reader.read(tagNameBuffer);
						if (new String (tagNameBuffer).equalsIgnoreCase(tagName))
							state = ParseState.Finding;
					}
					break;
				case Finding:
					if (current == '>')
						state = ParseState.Eating;
					else if (current == firstAttr) {
						reader.read (tagAttributeBuffer);
						if (new String (tagAttributeBuffer).equalsIgnoreCase(tagAttribute))
							state = ParseState.Acquiring;
					}
					break;
				case Acquiring:
					if (current != '=') {
						state = ParseState.Eating;
					} else {
						buffer.clear ();
						char start = safeNext(reader);
						char end = start;
						if (Character.isLetterOrDigit (start)) {
							buffer.append(start);
							end = ' ';
						}
						while ((current = next (reader)) != end) {
							// Extra from spec
							if (current == '>') {
								state = ParseState.Eating;
								break;
							}
							buffer.append(current);
						}
						String tmp = buffer.toString();
						Matcher m = pattern.matcher(tmp);
						if (m.matches())
							return tmp;
						state = ParseState.Eating;
					}
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	static char safeNext (Reader reader) throws IOException {
		char c;
		do {
			c = (char)reader.read();
		} while (Character.isSpaceChar(c));
		
		return c;
	}

	static char next (Reader reader) throws IOException {
		return (char)reader.read ();
	}
}
