package org.psygrid.data.export;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.regex.Pattern;

import com.csvreader.CsvWriter;

public class CustomCsvWriter extends CsvWriter {

	private static final Pattern whitespace = Pattern.compile(".*[\\s]+.*");

	public CustomCsvWriter(String fileName, char delimiter, Charset charset) {
		super(fileName, delimiter, charset);
	}

	public CustomCsvWriter(String fileName) {
		super(fileName);
	}

	public CustomCsvWriter(Writer outputStream, char delimiter) {
		super(outputStream, delimiter);
	}

	public CustomCsvWriter(OutputStream outputStream, char delimiter, Charset charset) {
		super(outputStream, delimiter, charset);
	}

	/**
	 * Writes another column of data to this record.
	 * 
	 * This method overrides the CsvWriter parent method to force quoting around
	 * strings containing whitespace.
	 * 
	 * @param content
	 *            The data for the new column.
	 * @param preserveSpaces
	 *            Whether to preserve leading and trailing whitespace in this
	 *            column of data.
	 * @exception IOException
	 *                Thrown if an error occurs while writing data to the
	 *                destination stream.
	 */
	public void write(String content, boolean preserveSpaces)
	throws IOException {
		if (content != null && content.length() > 1 
				&& whitespace.matcher(content).matches()) {
			this.setForceQualifier(true);
		}
		super.write(content, preserveSpaces);
		this.setForceQualifier(false);
	}
}
