
/*
 *
 *  Copyright 2006 OCLC, Online Computer Library Center
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *  Thi file was modified to be part of LA Referencia software platform LRHarvester v4.x and
 *  is redistributed respecting the original licence.
 *
 *  For any further information please contact
 *  Lautaro Matas <lmatas@gmail.com>
 *
 */

package org.oclc.oai.harvester2.verb;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;
import java.util.zip.ZipInputStream;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.IOUtils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.xpath.XPathAPI;
import org.lareferencia.core.util.InvalidXMLCharFilterReader;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * HarvesterVerb is the parent class for each of the OAI verbs.
 * 
 * @author Jefffrey A. Young, OCLC Online Computer Library Center
 */
public abstract class HarvesterVerb {
	private static Logger logger = LogManager.getLogger(HarvesterVerb.class);

	private static int MAX_REQUEST_TIME = 120; // SEGUNDOS
	
	/* Primary OAI namespaces */
	public static final String SCHEMA_LOCATION_V2_0 = "http://www.openarchives.org/OAI/2.0/ http://www.openarchives.org/OAI/2.0/OAI-PMH.xsd";
	public static final String SCHEMA_LOCATION_V1_1_GET_RECORD = "http://www.openarchives.org/OAI/1.1/OAI_GetRecord http://www.openarchives.org/OAI/1.1/OAI_GetRecord.xsd";
	public static final String SCHEMA_LOCATION_V1_1_IDENTIFY = "http://www.openarchives.org/OAI/1.1/OAI_Identify http://www.openarchives.org/OAI/1.1/OAI_Identify.xsd";
	public static final String SCHEMA_LOCATION_V1_1_LIST_IDENTIFIERS = "http://www.openarchives.org/OAI/1.1/OAI_ListIdentifiers http://www.openarchives.org/OAI/1.1/OAI_ListIdentifiers.xsd";
	public static final String SCHEMA_LOCATION_V1_1_LIST_METADATA_FORMATS = "http://www.openarchives.org/OAI/1.1/OAI_ListMetadataFormats http://www.openarchives.org/OAI/1.1/OAI_ListMetadataFormats.xsd";
	public static final String SCHEMA_LOCATION_V1_1_LIST_RECORDS = "http://www.openarchives.org/OAI/1.1/OAI_ListRecords http://www.openarchives.org/OAI/1.1/OAI_ListRecords.xsd";
	public static final String SCHEMA_LOCATION_V1_1_LIST_SETS = "http://www.openarchives.org/OAI/1.1/OAI_ListSets http://www.openarchives.org/OAI/1.1/OAI_ListSets.xsd";
	private Document doc = null;
	private String schemaLocation = null;
	private String requestURL = null;
	private static HashMap builderMap = new HashMap();
	private static Element namespaceElement = null;
	private static DocumentBuilderFactory factory = null;
	private static TransformerFactory xformFactory = new net.sf.saxon.TransformerFactoryImpl(); //TransformerFactory.newInstance();

	static {
		try {
			/* Load DOM Document */
			factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			Thread t = Thread.currentThread();
			DocumentBuilder builder = factory.newDocumentBuilder();
			builderMap.put(t, builder);

			DOMImplementation impl = builder.getDOMImplementation();
			Document namespaceHolder = impl.createDocument("http://www.oclc.org/research/software/oai/harvester", "harvester:namespaceHolder", null);
			namespaceElement = namespaceHolder.getDocumentElement();
			namespaceElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:harvester", "http://www.oclc.org/research/software/oai/harvester");
			namespaceElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
			namespaceElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:oai20", "http://www.openarchives.org/OAI/2.0/");
			namespaceElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:oai11_GetRecord", "http://www.openarchives.org/OAI/1.1/OAI_GetRecord");
			namespaceElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:oai11_Identify", "http://www.openarchives.org/OAI/1.1/OAI_Identify");
			namespaceElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:oai11_ListIdentifiers", "http://www.openarchives.org/OAI/1.1/OAI_ListIdentifiers");
			namespaceElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:oai11_ListMetadataFormats", "http://www.openarchives.org/OAI/1.1/OAI_ListMetadataFormats");
			namespaceElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:oai11_ListRecords", "http://www.openarchives.org/OAI/1.1/OAI_ListRecords");
			namespaceElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:oai11_ListSets", "http://www.openarchives.org/OAI/1.1/OAI_ListSets");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Get the OAI response as a DOM object
	 * 
	 * @return the DOM for the OAI response
	 */
	public Document getDocument() {
		return doc;
	}

	/**
	 * Get the xsi:schemaLocation for the OAI response
	 * 
	 * @return the xsi:schemaLocation value
	 */
	public String getSchemaLocation() {
		return schemaLocation;
	}

	/**
	 * Get the OAI errors
	 * 
	 * @return a NodeList of /oai:OAI-PMH/oai:error elements
	 * @throws TransformerException
	 */
	public NodeList getErrors() throws FatalHarvestingException {
		
		try {
			if (SCHEMA_LOCATION_V2_0.equals(getSchemaLocation())) {
				return getNodeList("/oai20:OAI-PMH/oai20:error");
			} else {
				return null;
			} 
		} catch (TransformerException e) {
			throw new FatalHarvestingException("TransformerException during getErrors:: " +  e.getMessage(), e);
		}
	}

	/**
	 * Get the OAI request URL for this response
	 * 
	 * @return the OAI request URL as a String
	 */
	public String getRequestURL() {
		return requestURL;
	}

	/**
	 * Mock object creator (for unit testing purposes)
	 */
	public HarvesterVerb() {
	}

	/**
	 * Performs the OAI request
	 * 
	 * @param requestURL
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws TransformerException
	 */
	public HarvesterVerb(String requestURL) throws FatalHarvestingException, RecoverableHarvestingException {
		harvest(requestURL);
	}

	/**
	 * Preforms the OAI request
	 * 
	 * @param requestURL
	 * @throws RecoverableHarvestingException 
	 * @throws MalformedURLException 
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws TransformerException
	 */
	public void harvest(String requestURL) throws FatalHarvestingException, RecoverableHarvestingException  {
		this.requestURL = requestURL;
		logger.debug("requestURL=" + requestURL);
		InputStream in = null;
		
	
		try {
			URL url = new URL(requestURL);
		} catch (MalformedURLException e) {
			throw new FatalHarvestingException("Malformed URL: " + requestURL, e);
		}
			
		HttpURLConnection con = null;
		int responseCode = 0;
		
		int i = 0;
		do {

			logger.debug("try:" + i + " getting requestURL: "+ requestURL);
			// " :Comenzando: " + requestURL);

			
			try { // get the connection
				if ( requestURL.startsWith("https") ) {
					con = (HttpsURLConnection) new URL(requestURL).openConnection();
			    }
			    else {
			    	con  = (HttpURLConnection) new URL(requestURL).openConnection();
			    }
			} catch ( IOException e) {
				throw new RecoverableHarvestingException("Connection Error: " + requestURL + " :: " + e.getMessage(), e);
			}
			
			con.setReadTimeout(MAX_REQUEST_TIME * 1000);

			con.setRequestProperty("User-Agent", "OAIHarvester/2.0");
			con.setRequestProperty("Accept-Encoding", "compress, gzip, identify");

			try {
				responseCode = con.getResponseCode();
				logger.debug(requestURL + " responseCode=" + responseCode);

			} catch (FileNotFoundException e) {
				// assume it's a 503 response
				logger.debug(requestURL + " File not found", e);
				responseCode = HttpURLConnection.HTTP_UNAVAILABLE;
			} catch (IOException e) {
				throw new RecoverableHarvestingException("Connection Error: " + requestURL + " :: " + e.getMessage(), e);
			}

			if (responseCode == HttpURLConnection.HTTP_UNAVAILABLE) {
				long retrySeconds = con.getHeaderFieldInt("Retry-After", -1);
				if (retrySeconds == -1) {
					long now = (new Date()).getTime();
					long retryDate = con.getHeaderFieldDate("Retry-After", now);
					retrySeconds = retryDate - now;
				}
				if (retrySeconds == 0) { // Apparently, it's a bad URL
					throw new RecoverableHarvestingException("Invalid URL: " + requestURL + " :: HTTP_UNAVAILABLE " );
				}
				logger.error("Server response: Retry-After=" +retrySeconds);
				if (retrySeconds > 0) {
					try {
						Thread.sleep(retrySeconds * 1000);
					} catch (InterruptedException ex) {
						ex.printStackTrace();
					}
				}
			}

			i++;
		} while (responseCode == HttpURLConnection.HTTP_UNAVAILABLE);

		// logger.debug("OCLC Harvester 2: Finalizando Get: "+
		// requestURL);

		String contentEncoding = con.getHeaderField("Content-Encoding");
		logger.debug("contentEncoding=" + contentEncoding);

		try {
			if ("compress".equals(contentEncoding)) {
				ZipInputStream zis = new ZipInputStream(con.getInputStream());
				zis.getNextEntry();
				in = zis;
			} else if ("gzip".equals(contentEncoding)) {
				in = new GZIPInputStream(con.getInputStream());
			} else if ("deflate".equals(contentEncoding)) {
				in = new InflaterInputStream(con.getInputStream());
			} else {
				in = con.getInputStream();
			}
		} catch (IOException e) {
			throw new RecoverableHarvestingException("Connection error (getting/decompressing bytes from input stream): " + requestURL + " :: " + e.getMessage(), e);
		}

		
		// logger.debug("OCLC Harvester 2: Leido input stream: "+
		// requestURL);

		String xmlString = "";
		Boolean debug = true;
		InputSource data;
		
		try {
			
			// si esta en modo debug va a conservar el xmlString para poder informar en caso de errores
			if (logger.isDebugEnabled()) {		
				StringWriter writer = new StringWriter();
				IOUtils.copy(in, writer, "UTF8");
				xmlString = writer.toString();
				data = new InputSource(new java.io.StringReader(xmlString));				
			} else {
				InputStreamReader isr = new InputStreamReader(in, "UTF8");
				InvalidXMLCharFilterReader reader = new InvalidXMLCharFilterReader(isr);
				data = new InputSource(reader);
			}
			
			Thread t = Thread.currentThread();
			DocumentBuilder builder = (DocumentBuilder) builderMap.get(t);
			if (builder == null) {
				builder = factory.newDocumentBuilder();
				builderMap.put(t, builder);
			}
	
			// logger.debug("OCLC Harvester 2: parser preparado: "+
			// requestURL);
	
			doc = builder.parse(data);
			
			// logger.debug("OCLC Harvester 2: parseado el documento : "+
			// requestURL);
	
			StringTokenizer tokenizer = new StringTokenizer(getSingleString("/*/@xsi:schemaLocation"), " ");
			StringBuffer sb = new StringBuffer();
			while (tokenizer.hasMoreTokens()) {
				if (sb.length() > 0)
					sb.append(" ");
				sb.append(tokenizer.nextToken());
			}
			this.schemaLocation = sb.toString();
	
			// logger.debug("OCLC Harvester 2: Finalizado: " +requestURL);
	
			con.disconnect();
			
		} catch (UnsupportedEncodingException e) {
			throw new FatalHarvestingException("Parsing XML (UnsupportedEncodingException): " + requestURL + " :: " + e.getMessage(), e);
		} catch (ParserConfigurationException e) {
			throw new FatalHarvestingException("Parsing XML (ParserConfigurationException): " + requestURL + " :: " + e.getMessage(), e);
		} catch (TransformerException e) {
			throw new FatalHarvestingException("Parsing XML (TransformerException gettin SchemaLocation): " + requestURL + " :: " + e.getMessage(), e);
		} catch (SAXException e) {
			logger.error( "Error in initial xml parsing (from connection) ::" + requestURL + " :: " + e.getMessage() + " :: "  + "If org.oclc.oai.harvester2 Logger level=DEBUG xml body will be displayed below:" );		
			logger.error( xmlString );		
			throw new FatalHarvestingException("Parsing XML (SAXException - set org.oclc.oai.harvester2 Logger level=DEBUG and see file log for xml body details): " + requestURL + " :: " + e.getMessage(), e);

		} catch (IOException e) {
			throw new FatalHarvestingException("Parsing XML (SAXException): " + requestURL + " :: " + e.getMessage(), e);
		}

	}

	/**
	 * Get the String value for the given XPath location in the response DOM
	 * 
	 * @param xpath
	 * @return a String containing the value of the XPath location.
	 * @throws TransformerException
	 */
	public String getSingleString(String xpath) throws TransformerException {
		return getSingleString(getDocument(), xpath);
		// return XPathAPI.eval(getDocument(), xpath, namespaceElement).str();
		// String str = null;
		// Node node = XPathAPI.selectSingleNode(getDocument(), xpath,
		// namespaceElement);
		// if (node != null) {
		// XObject xObject = XPathAPI.eval(node, "string()");
		// str = xObject.str();
		// }
		// return str;
	}

	public String getSingleString(Node node, String xpath) throws TransformerException {
		return XPathAPI.eval(node, xpath, namespaceElement).str();
	}

	/**
	 * Get a NodeList containing the nodes in the response DOM for the specified
	 * xpath
	 * 
	 * @param xpath
	 * @return the NodeList for the xpath into the response DOM
	 * @throws TransformerException
	 */
	public NodeList getNodeList(String xpath) throws TransformerException {
		return XPathAPI.selectNodeList(getDocument(), xpath, namespaceElement);
	}

	public String toString() {
		// Element docEl = getDocument().getDocumentElement();
		// return docEl.toString();
		Source input = new DOMSource(getDocument());
		StringWriter sw = new StringWriter();
		Result output = new StreamResult(sw);
		try {
			Transformer idTransformer = xformFactory.newTransformer();
			idTransformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			idTransformer.transform(input, output);
			return sw.toString();
		} catch (TransformerException e) {
			return e.getMessage();
		}
	}
}
