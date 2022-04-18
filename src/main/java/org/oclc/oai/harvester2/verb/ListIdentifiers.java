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

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * This class represents an ListIdentifiers response on either the server or on
 * the client
 * 
 * @author Jeffrey A. Young, OCLC Online Computer Library Center
 */
public class ListIdentifiers extends HarvesterVerb {
	/**
	 * Mock object constructor (for unit testing purposes)
	 */
	public ListIdentifiers() {
		super();
	}

	/**
	 * Client-side ListIdentifiers verb constructor
	 * 
	 * @param baseURL
	 *            the baseURL of the server to be queried
	 * @throws RecoverableHarvestingException 
	 * @exception SAXException
	 *                the xml response is bad
	 * @exception IOException
	 *                an I/O error occurred
	 */
	public ListIdentifiers(String baseURL, String from, String until, String set, String metadataPrefix) throws FatalHarvestingException, RecoverableHarvestingException {
		super(getRequestURL(baseURL, from, until, set, metadataPrefix));
	}

	/**
	 * Client-side ListIdentifiers verb constructor (resumptionToken version)
	 * 
	 * @param baseURL
	 * @param resumptionToken
	 * @throws RecoverableHarvestingException 
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws TransformerException
	 */
	public ListIdentifiers(String baseURL, String resumptionToken) throws FatalHarvestingException, RecoverableHarvestingException {
		super(getRequestURL(baseURL, resumptionToken));
	}

	/**
	 * Get the oai:resumptionToken from the response
	 * 
	 * @return the oai:resumptionToken value
	 * @throws TransformerException
	 * @throws NoSuchFieldException
	 */
	public String getResumptionToken() throws TransformerException, NoSuchFieldException {
		if (SCHEMA_LOCATION_V2_0.equals(getSchemaLocation())) {
			return getSingleString("/oai20:OAI-PMH/oai20:ListIdentifiers/oai20:resumptionToken");
		} else if (SCHEMA_LOCATION_V1_1_LIST_IDENTIFIERS.equals(getSchemaLocation())) {
			return getSingleString("/oai11_ListIdentifiers:ListIdentifiers/oai11_ListIdentifiers:resumptionToken");
		} else {
			throw new NoSuchFieldException(getSchemaLocation());
		}
	}

	/**
	 * Construct the query portion of the http request
	 * 
	 * @return a String containing the query portion of the http request
	 */
	private static String getRequestURL(String baseURL, String from, String until, String set, String metadataPrefix) {
		StringBuffer requestURL = new StringBuffer(baseURL);
		requestURL.append("?verb=ListIdentifiers");
		if (from != null)
			requestURL.append("&from=").append(from);
		if (until != null)
			requestURL.append("&until=").append(until);
		if (set != null)
			requestURL.append("&set=").append(set);
		requestURL.append("&metadataPrefix=").append(metadataPrefix);
		return requestURL.toString();
	}

	/**
	 * Construct the query portion of the http request (resumptionToken version)
	 * 
	 * @param baseURL
	 * @param resumptionToken
	 * @return
	 */
	private static String getRequestURL(String baseURL, String resumptionToken) throws FatalHarvestingException {
		
		try {
			StringBuffer requestURL = new StringBuffer(baseURL);
			requestURL.append("?verb=ListIdentifiers");
			requestURL.append("&resumptionToken=").append(URLEncoder.encode(resumptionToken,"utf-8"));
			return requestURL.toString();
		} catch ( UnsupportedEncodingException e ) {
			throw new FatalHarvestingException("Unsupported Encoding during getRequestURL/resumptionToken: " + baseURL + " :: " + resumptionToken + " :: " +  e.getMessage(), e);
		}
	}
}
