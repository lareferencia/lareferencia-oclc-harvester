
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

import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.xml.sax.SAXException;

/**
 * This class represents an ListMetadataFormats response on either the server or
 * on the client
 * 
 * @author Jeffrey A. Young, OCLC Online Computer Library Center
 */
public class ListMetadataFormats extends HarvesterVerb {
	/**
	 * Mock object constructor (for unit testing purposes)
	 */
	public ListMetadataFormats() {
		super();
	}

	/**
	 * Client-side ListMetadataFormats verb constructor
	 * 
	 * @param baseURL
	 *            the baseURL of the server to be queried
	 * @throws RecoverableHarvestingException 
	 * @exception MalformedURLException
	 *                the baseURL is bad
	 * @exception SAXException
	 *                the xml response is bad
	 * @exception IOException
	 *                an I/O error occurred
	 */
	public ListMetadataFormats(String baseURL) throws FatalHarvestingException, RecoverableHarvestingException {
		this(baseURL, null);
	}

	/**
	 * Client-side ListMetadataFormats verb constructor (identifier version)
	 * 
	 * @param baseURL
	 * @param identifier
	 * @throws RecoverableHarvestingException 
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws TransformerException
	 */
	public ListMetadataFormats(String baseURL, String identifier) throws FatalHarvestingException, RecoverableHarvestingException {
		super(getRequestURL(baseURL, identifier));
	}

	/**
	 * Construct the query portion of the http request
	 * 
	 * @return a String containing the query portion of the http request
	 */
	private static String getRequestURL(String baseURL, String identifier) {
		StringBuffer requestURL = new StringBuffer(baseURL);
		requestURL.append("?verb=ListMetadataFormats");
		if (identifier != null)
			requestURL.append("&identifier=").append(identifier);
		return requestURL.toString();
	}
}
