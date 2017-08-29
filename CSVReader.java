
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * CSV reader.
 * 
 * @author karim.hammouda
 */
public class CSVReader {

	private BufferedReader br;
	private boolean hasNext = true;

	private char separator;

	private char[] quotechar;

	private int skipLines;

	private boolean linesSkiped;

	private boolean commentsSupported;

	private char commentchar;

	public static final char DEFAULT_SEPARATOR = ',';

	public static final char[] DEFAULT_QUOTE_CHARACTER = { '"' };

	public static final int DEFAULT_SKIP_LINES = 0;

	public static final char DEFAULT_COMMENT_CHARACTER = '#';

	public CSVReader(Reader reader) {
		this(reader, DEFAULT_SEPARATOR);
	}

	public CSVReader(Reader reader, char separator) {
		this(reader, separator, DEFAULT_QUOTE_CHARACTER);
	}

	public CSVReader(Reader reader, char separator, char[] quotechar) {
		this(reader, separator, quotechar, DEFAULT_SKIP_LINES);
	}

	public CSVReader(Reader reader, char separator, char[] quotechar, int line) {
		this(reader, separator, quotechar, line, DEFAULT_COMMENT_CHARACTER);
	}

	public CSVReader(Reader reader, char separator, char[] quotechar, int line, char commentchar) {
		if (reader != null) {
			this.br = new BufferedReader(reader);
			commentsSupported = false;
		} else {
			commentsSupported = true;
		}
		this.separator = separator;
		this.quotechar = quotechar;
		this.skipLines = line;
		this.commentchar = commentchar;
	}

	public List<String[]> readAll() throws IOException {
		return readAll(false);
	}

	public List<String[]> readAll(boolean skipcomments) throws IOException {
		List<String[]> allElements = new ArrayList<String[]>();
		while (hasNext) {
			String[] nextLineAsTokens = readNext(skipcomments);
			if (nextLineAsTokens != null) {
				allElements.add(nextLineAsTokens);
			}
		}
		return allElements;

	}

	public String[] readNext() throws IOException {
		return readNext(false);
	}

	public String[] readFromString(String csvString) {
		try {
			return parseLine(csvString, false);
		} catch (IOException e) {
			return null;
		}
	}

	public String[] readNext(boolean skipcomments) throws IOException {
		String nextLine = getNextLine(skipcomments);
		if (hasNext) {
			return parseLine(nextLine, skipcomments);
		} else {
			return null;
		}
	}

	private String getNextLine(boolean skipcomments) throws IOException {
		if (br == null) {
			return null;
		}
		if (!this.linesSkiped) {
			for (int i = 0; i < skipLines; i++) {
				br.readLine();
			}
			this.linesSkiped = true;
		}
		String nextLine = br.readLine();
		while (nextLine != null && commentsSupported && nextLine.indexOf(commentchar) == 0) {
			nextLine = br.readLine();
		}

		if (nextLine == null) {
			hasNext = false;
		}
		if (hasNext) {
			return nextLine;
		} else {
			return null;
		}
	}

	private String[] parseLine(String nextLine, boolean skipcomments) throws IOException {

		if (nextLine == null) {
			return null;
		}

		List<String> tokensOnThisLine = new ArrayList<String>();
		StringBuffer sb = new StringBuffer();
		boolean inQuotes = false;
		do {
			if (inQuotes) {
				sb.append("\n");
				nextLine = getNextLine(false);
				if (nextLine == null) {
					break;
				}
			}

			if (commentsSupported && nextLine.indexOf(commentchar) == 0) {
				if (skipcomments) {
					return null;
				} else {
					return new String[] { nextLine };
				}
			}

			for (int i = 0; i < nextLine.length(); i++) {

				char c = nextLine.charAt(i);
				if (isCharAQuoteChar(c)) {
					if (inQuotes && nextLine.length() > (i + 1) && isCharAQuoteChar(nextLine.charAt(i + 1))) {
						sb.append(nextLine.charAt(i + 1));
						i++;
					} else {
						inQuotes = !inQuotes;
						if (i > 2 && nextLine.charAt(i - 1) != this.separator && nextLine.length() > (i + 1)
								&& nextLine.charAt(i + 1) != this.separator) {
							sb.append(c);
						}
					}
				} else if (c == separator && !inQuotes) {
					tokensOnThisLine.add(sb.toString());
					sb = new StringBuffer();
				} else {
					sb.append(c);
				}
			}
		} while (inQuotes);
		tokensOnThisLine.add(sb.toString());
		return (String[]) tokensOnThisLine.toArray(new String[0]);

	}

	private boolean isCharAQuoteChar(char c) {
		for (int i = 0; i < quotechar.length; i++) {
			if (c == quotechar[i]) {
				return true;
			}
		}
		return false;
	}

	public void close() throws IOException {
		br.close();
	}
}