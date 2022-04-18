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
 * This class represents an Identify response on either the server or on the
 * client
 * 
 * @author Jeffrey A. Young, OCLC Online Computer Library Center
 */
public class Identify extends HarvesterVerb {
	/**
	 * Mock object constructor (for unit testing purposes)
	 */
	public Identify() {
		super();
	}

	/**
	 * Client-side Identify verb constructor
	 * 
	 * @param baseURL
	 *            the baseURL of the server to be queried
	 * @throws RecoverableHarvestingException 
	 * @exception MalformedURLException
	 *                the baseURL is bad
	 * @exception IOException
	 *                an I/O error occurred
	 */
	public Identify(String baseURL)  throws FatalHarvestingException, RecoverableHarvestingException {
		super(getRequestURL(baseURL));
	}

	/**
	 * Get the oai:protocolVersion value from the Identify response
	 * 
	 * @return the oai:protocolVersion value
	 * @throws TransformerException
	 * @throws NoSuchFieldException
	 */
	public String getProtocolVersion() throws TransformerException, NoSuchFieldException {
		if (SCHEMA_LOCATION_V2_0.equals(getSchemaLocation())) {
			return getSingleString("/oai20:OAI-PMH/oai20:Identify/oai20:protocolVersion");
		} else if (SCHEMA_LOCATION_V1_1_IDENTIFY.equals(getSchemaLocation())) {
			return getSingleString("/oai11_Identify:Identify/oai11_Identify:protocolVersion");
		} else {
			throw new NoSuchFieldException(getSchemaLocation());
		}
	}

	/**
	 * generate the Identify request URL for the specified baseURL
	 * 
	 * @param baseURL
	 * @return the requestURL
	 */
	private static String getRequestURL(String baseURL) {
		StringBuffer requestURL = new StringBuffer(baseURL);
		requestURL.append("?verb=Identify");
		return requestURL.toString();
	}
}
