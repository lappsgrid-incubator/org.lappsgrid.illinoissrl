package org.lappsgrid.illinoissrl;

// JUnit modules for unit tests
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.lappsgrid.api.WebService;
import org.lappsgrid.discriminator.Discriminators;
import org.lappsgrid.metadata.IOSpecification;
import org.lappsgrid.metadata.ServiceMetadata;
import org.lappsgrid.serialization.Data;
import org.lappsgrid.serialization.Serializer;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;
import static org.lappsgrid.discriminator.Discriminators.Uri;


public class TestSRL {

    // this will be the sandbag
    protected WebService service;

    // initiate the service before each test
    @Before
    public void setUp() throws Exception {
        service = new IllinoisSRL();
    }

    // then destroy it after the test
    @After
    public void tearDown() {
        service = null;
    }

    @Test
    public void testMetadata() {
        String json = service.getMetadata();
        assertNotNull("service.getMetadata() returned null", json);

        Data data = Serializer.parse(json, Data.class);
        assertNotNull("Unable to parse metadata json.", data);
        assertNotSame(data.getPayload().toString(), Discriminators.Uri.ERROR, data.getDiscriminator());

        ServiceMetadata metadata = new ServiceMetadata((Map) data.getPayload());
        IOSpecification produces = metadata.getProduces();
        IOSpecification requires = metadata.getRequires();

        assertEquals("Name is not correct", IllinoisSRL.class.getName(), metadata.getName());
        assertEquals("\"allow\" field not equal", Discriminators.Uri.ANY, metadata.getAllow());
        assertEquals("License not correct", Discriminators.Uri.APACHE2, metadata.getLicense());

        List<String> requiresList = requires.getFormat();
        assertTrue("Text not accepted", requiresList.contains(Discriminators.Uri.TEXT));
        assertTrue("Required languages do not contain English", requires.getLanguage().contains("en"));

        List<String> producesList = produces.getFormat();
        assertTrue("Tool does not produce LAPPSGRID format", producesList.contains(Discriminators.Uri.LAPPS));
    }

    @Test
    public void testExecute() {
        final String testString = "In order to succeed, we must first believe that we can.";

        Data data = new Data(Uri.TEXT, testString);
        String results = this.service.execute(data.asJson());
        System.out.println(results);
    }
}