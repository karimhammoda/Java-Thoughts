

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
/**
 * XML Writer.
 * 
 * @author karim.hammouda
 */

public class XMLWriter {
	private String rootNode;

	private String elementNode;

	public static final String DEFAULT_ROOT_NODE = "root";

	public static final String DEFAULT_ELEMENT_NODE = "element";

	public static final String ENCODING_UTF8 = "UTF-8";

	public static final String XMLVERSION = "1.0";

	public XMLWriter() {
		this(DEFAULT_ROOT_NODE, DEFAULT_ELEMENT_NODE);
	}

	public XMLWriter(String rootNode, String elementNode) {
		this.rootNode = rootNode;
		this.elementNode = elementNode;
	}

	public void writeXMLFile(String filePath, String[] headerArr, List<String[]> valueList) throws XMLStreamException, IOException {
		StringWriter swr = new StringWriter();

		XMLOutputFactory xMLOutputFactory = XMLOutputFactory.newInstance();
		XMLStreamWriter xMLStreamWriter = xMLOutputFactory.createXMLStreamWriter(swr);

		xMLStreamWriter.writeStartDocument(ENCODING_UTF8, XMLVERSION);
		xMLStreamWriter.writeStartElement(this.rootNode);

		for (String[] line : valueList) {
			xMLStreamWriter.writeStartElement(this.elementNode);
			for (int j = 0; j < headerArr.length; ++j) {
				xMLStreamWriter.writeStartElement(headerArr[j]);
				xMLStreamWriter.writeCharacters(line[j]);
				xMLStreamWriter.writeEndElement();
			}
			xMLStreamWriter.writeEndElement();
		}

		xMLStreamWriter.writeEndElement();
		xMLStreamWriter.writeEndDocument();

		xMLStreamWriter.flush();
		xMLStreamWriter.close();

		swr.close();
		
		Writer writer = new OutputStreamWriter(new FileOutputStream(filePath), ENCODING_UTF8);
		BufferedWriter bw = new BufferedWriter(writer);
		bw.write(swr.getBuffer().toString());
		bw.close();
		

	}

}
