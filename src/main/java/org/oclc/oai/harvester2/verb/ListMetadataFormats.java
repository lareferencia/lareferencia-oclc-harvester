/*******************************************************************************
 * Copyright (c) 2013, 2019 LA Referencia / Red CLARA and others
 *
 * This file is part of LRHarvester v4.x software
 *
 *  This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *     
 *     For any further information please contact
 *     Lautaro Matas <lmatas@gmail.com>
 *******************************************************************************/

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
