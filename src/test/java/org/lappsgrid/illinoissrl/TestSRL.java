package org.lappsgrid.illinoissrl;

// JUnit modules for unit tests
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

// more APIs for testing code
import org.lappsgrid.api.WebService;
import static org.lappsgrid.discriminator.Discriminators.Uri;

import org.lappsgrid.metadata.IOSpecification;
import org.lappsgrid.metadata.ServiceMetadata;
import org.lappsgrid.serialization.Data;
import org.lappsgrid.serialization.DataContainer;
import org.lappsgrid.serialization.Serializer;
import org.lappsgrid.serialization.lif.Annotation;
import org.lappsgrid.serialization.lif.Container;
import org.lappsgrid.serialization.lif.View;
import org.lappsgrid.vocabulary.Features;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class TestSRL {

    // this will be the sandbag
    protected WebService service;

    // initiate the service before each test
    @Before
    public void setUp() throws IOException {
        service = new IllinoisSRL();
    }

    // then destroy it after the test
    @After
    public void tearDown() {
        service = null;
    }

    @Test
    public void testMetadata() {  }

    @Test
    public void testExecute() {
        final String testString = "In order to succeed, we must first believe that we can.";

        Data data = new Data(Uri.TEXT, testString);
        String results = this.service.execute(data.asJson());
        System.out.println(results);
    }
}