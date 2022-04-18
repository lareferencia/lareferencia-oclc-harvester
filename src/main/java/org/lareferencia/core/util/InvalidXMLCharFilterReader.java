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
package org.lareferencia.core.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FilterReader;
import java.io.IOException;
import java.io.Reader;

public class InvalidXMLCharFilterReader extends FilterReader {
	
	private static Logger logger = LogManager.getLogger(InvalidXMLCharFilterReader.class);


	public InvalidXMLCharFilterReader(Reader in) {
		super(in);
	}

	public int read() throws IOException {
		char[] buf = new char[1];
		int result = read(buf, 0, 1);
		if (result == -1)
			return -1;
		else
			return (int) buf[0];
	}

	public int read(char[] buf, int from, int len) throws IOException {
		int count = 0;
		while (count == 0) {
			count = in.read(buf, from, len);
			if (count == -1)
				return -1;

			int last = from;
			for (int i = from; i < from + count; i++) {
				if (!isBadXMLChar(buf[i])) {
					buf[last++] = buf[i];
				} else {
					logger.debug("Char XML inválido eliminado: " + String.format("%04x", (int)buf[i]));
				}
			}

			count = last - from;
		}
		return count;
	}

	private boolean isBadXMLChar(char c) {

		if ((c == 0x9) || (c == 0xA) || (c == 0xD) || ((c >= 0x20) && (c <= 0xD7FF)) || ((c >= 0xE000) && (c <= 0xFFFD)) || ((c >= 0x10000) && (c <= 0x10FFFF))) {
			return false;
		}
		return true;
	}

}