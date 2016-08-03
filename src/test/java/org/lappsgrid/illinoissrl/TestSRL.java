package org.lappsgrid.illinoissrl;

// JUnit modules for unit tests
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.lappsgrid.api.WebService;
import org.lappsgrid.serialization.Data;

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
    public void testMetadata() {  }

    @Test
    public void testExecute() {
        final String testString = "In order to succeed, we must first believe that we can.";

        Data data = new Data(Uri.TEXT, testString);
        String results = this.service.execute(data.asJson());
        System.out.println(results);
    }
}