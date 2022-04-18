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
 * This class represents an GetRecord response on either the server or on the
 * client
 * 
 * @author Jeffrey A. Young, OCLC Online Computer Library Center
 */
public class GetRecord extends HarvesterVerb {
	/**
	 * Mock object constructor (for unit testing purposes)
	 */
	public GetRecord() {
		super();
	}

	/**
	 * Client-side GetRecord verb constructor
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
	public GetRecord(String baseURL, String identifier, String metadataPrefix) throws FatalHarvestingException, RecoverableHarvestingException {
		super(getRequestURL(baseURL, identifier, metadataPrefix));
	}

	/**
	 * Get the oai:identifier from the oai:header
	 * 
	 * @return the oai:identifier as a String
	 * @throws TransformerException
	 * @throws NoSuchFieldException
	 */
	public String getIdentifier() throws TransformerException, NoSuchFieldException {
		if (SCHEMA_LOCATION_V2_0.equals(getSchemaLocation())) {
			return getSingleString("/oai20:OAI-PMH/oai20:GetRecord/oai20:record/oai20:header/oai20:identifier");
		} else if (SCHEMA_LOCATION_V1_1_GET_RECORD.equals(getSchemaLocation())) {
			return getSingleString("/oai11_GetRecord:GetRecord/oai11_GetRecord:record/oai11_GetRecord:header/oai11_GetRecord:identifier");
		} else {
			throw new NoSuchFieldException(getSchemaLocation());
		}
	}

	/**
	 * Construct the query portion of the http request
	 * 
	 * @return a String containing the query portion of the http request
	 */
	private static String getRequestURL(String baseURL, String identifier, String metadataPrefix) {
		StringBuffer requestURL = new StringBuffer(baseURL);
		requestURL.append("?verb=GetRecord");
		requestURL.append("&identifier=").append(identifier);
		requestURL.append("&metadataPrefix=").append(metadataPrefix);
		return requestURL.toString();
	}
}
