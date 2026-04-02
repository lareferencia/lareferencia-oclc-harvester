package org.oclc.oai.harvester2.verb;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;

import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

class ListRecordsSchemaInferenceTest {

	@Test
	void infersOaiPmh20SchemaLocationWhenAttributeIsMissing() throws Exception {
		ListRecords listRecords = createListRecordsWithoutSchemaLocation("""
			<?xml version="1.0" encoding="UTF-8"?>
			<OAI-PMH xmlns="http://www.openarchives.org/OAI/2.0/">
			  <responseDate>2026-03-26T11:13:17Z</responseDate>
			  <request>https://oai.impactu.colav.co/oai</request>
			  <ListRecords>
			    <record>
			      <header>
			        <identifier>oai:impactu.colav.co:1</identifier>
			        <datestamp>2025-08-26T17:06:22Z</datestamp>
			      </header>
			      <metadata>
			        <Publication xmlns="https://www.openaire.eu/cerif-profile/1.2/" id="1"/>
			      </metadata>
			    </record>
			    <resumptionToken cursor="0" completeListSize="2">next-page-token</resumptionToken>
			  </ListRecords>
			</OAI-PMH>
			""");

		assertEquals(HarvesterVerb.SCHEMA_LOCATION_V2_0, listRecords.getSchemaLocation());
		assertEquals("next-page-token", listRecords.getResumptionToken());
	}

	@Test
	void readsOaiErrorsWhenSchemaLocationAttributeIsMissing() throws Exception {
		ListRecords listRecords = createListRecordsWithoutSchemaLocation("""
			<?xml version="1.0" encoding="UTF-8"?>
			<OAI-PMH xmlns="http://www.openarchives.org/OAI/2.0/">
			  <responseDate>2026-03-26T11:13:17Z</responseDate>
			  <request>https://oai.impactu.colav.co/oai</request>
			  <error code="noRecordsMatch">No records</error>
			</OAI-PMH>
			""");

		NodeList errors = listRecords.getErrors();

		assertNotNull(errors);
		assertEquals(1, errors.getLength());
		assertEquals("noRecordsMatch", errors.item(0).getAttributes().getNamedItem("code").getNodeValue());
	}

	private static ListRecords createListRecordsWithoutSchemaLocation(String xml) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);

		Document document = factory.newDocumentBuilder()
				.parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));

		ListRecords listRecords = new ListRecords();
		setField(HarvesterVerb.class, listRecords, "doc", document);
		setField(HarvesterVerb.class, listRecords, "schemaLocation", null);
		return listRecords;
	}

	private static void setField(Class<?> type, Object target, String fieldName, Object value) throws Exception {
		Field field = type.getDeclaredField(fieldName);
		field.setAccessible(true);
		field.set(target, value);
	}
}
