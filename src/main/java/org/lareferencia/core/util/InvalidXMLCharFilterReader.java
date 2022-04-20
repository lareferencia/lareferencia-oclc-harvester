
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