package org.lappsgrid.illinoissrl;


import edu.illinois.cs.cogcomp.core.datastructures.textannotation.*;
import edu.illinois.cs.cogcomp.core.utilities.configuration.ResourceManager;
import edu.illinois.cs.cogcomp.srl.SemanticRoleLabeler;
import edu.illinois.cs.cogcomp.srl.experiment.TextPreProcessor;
import org.lappsgrid.api.ProcessingService;
import org.lappsgrid.discriminator.Discriminators;
import org.lappsgrid.metadata.IOSpecification;
import org.lappsgrid.metadata.ServiceMetadata;
import org.lappsgrid.serialization.Data;
import org.lappsgrid.serialization.DataContainer;
import org.lappsgrid.serialization.Serializer;
import org.lappsgrid.serialization.lif.Annotation;
import org.lappsgrid.serialization.lif.Container;
import org.lappsgrid.serialization.lif.View;
import org.lappsgrid.vocabulary.Features;

import java.net.URL;
import java.util.*;

public class IllinoisSRL implements ProcessingService {
    Comparator<Relation> relationComparator = new Comparator<Relation>() {
        @Override
        public int compare(Relation arg0, Relation arg1) {
            return arg0.getRelationName().compareTo(arg1.getRelationName());
        }
    };

    private ResourceManager rm;
    private SemanticRoleLabeler nomSRL, verbSRL;
    private String metadata;

    public IllinoisSRL() throws Exception{
        URL url = this.getClass().getResource("/config/srlconfig.properties");
        this.rm = new ResourceManager(url.getPath());
        this.nomSRL = new SemanticRoleLabeler(rm, "Nom", true);
        this.verbSRL = new SemanticRoleLabeler(rm, "Verb", true);

        ServiceMetadata md = new ServiceMetadata();

        md.setName(this.getClass().getName());
        md.setAllow(Discriminators.Uri.ANY);
        md.setDescription("UIUC Semantic Role Labeler");
        md.setVendor("http://www.lappsgrid.org");
        md.setLicense(Discriminators.Uri.APACHE2);

        IOSpecification requires = new IOSpecification();
        requires.addFormat(Discriminators.Uri.TEXT);
        requires.addLanguage("en");

        IOSpecification produces = new IOSpecification();
        produces.addFormat(Discriminators.Uri.LAPPS);
        produces.addLanguage("en");

        md.setRequires(requires);
        md.setProduces(produces);

        Data<ServiceMetadata> data = new Data<>(Discriminators.Uri.META, md);
        metadata = data.asPrettyJson();
    }

    @Override
    public String getMetadata() {
        return metadata;
    }

    @Override
    public String execute(String input) {

        // Step #1: Parse the input.
        Data data = Serializer.parse(input, Data.class);

        // Step #2: Check the discriminator
        final String discriminator = data.getDiscriminator();
        if (discriminator.equals(Discriminators.Uri.ERROR)) {
            // Return the input unchanged.
            return input;
        }

        // Step #3: Extract the text.
        Container container;
        if (discriminator.equals(Discriminators.Uri.TEXT)) {
            container = new Container();
            container.setText(data.getPayload().toString());
        } else if (discriminator.equals(Discriminators.Uri.LAPPS)) {
            container = new Container((Map) data.getPayload());
        } else {
            // This is a format we don't accept.
            String message = String.format("Unsupported discriminator type: %s", discriminator);
            return new Data<>(Discriminators.Uri.ERROR, message).asJson();
        }

        String text = container.getText();



        SemanticRoleLabeler[] srls = {nomSRL, verbSRL};
        for (int i = 0; i < srls.length; i++) {
            SemanticRoleLabeler srl = srls[i];

            // Process the input text
            TextAnnotation ta;
            try {
                ta = TextPreProcessor.getInstance().preProcessText(text);
            } catch (Exception e) {
                e.printStackTrace();
                String message = "Unable to process the input text.";
                return new Data<>(Discriminators.Uri.ERROR, message).asJson();
            }

            // Labeling the semantic roles
            PredicateArgumentView pav;
            try {
                pav = srl.getSRL(ta);
            } catch (Exception e) {
                e.printStackTrace();
                String message = "Unable to label the semantic roles.";
                return new Data<>(Discriminators.Uri.ERROR, message).asJson();
            }

            // get a new view to hold annotations
            View view = new View();

            if (i == 0) {
                view.setId("nom");
            }
            if (i == 1) {
                view.setId("verb");
            }
            view.addContains(Discriminators.Uri.SEMANTIC_ROLE, this.getClass().getName(), "srl:uiuc");

            // adding annotations to nom view or verb view
            // get and sort the heads of each semantic role
            List<Constituent> predicates = new ArrayList<>(pav.getPredicates());
            Collections.sort(predicates, TextAnnotationUtilities.constituentStartComparator);

            int numOfNodes = predicates.size();
            for (int j = 0; j < numOfNodes; j++) { // for each head (node)
                Constituent predicate = predicates.get(i);
                int start = predicate.getStartCharOffset();
                int end = predicate.getEndCharOffset();

                // annotation for head
                Annotation a = new Annotation(view.getId() + '-' + j, "Role", start, end);
                a.setAtType(Discriminators.Uri.SEMANTIC_ROLE);
                a.addFeature(Features.SemanticRole.HEAD, pav.getPredicateLemma(predicate));

                // get and sort arguments for the head
                List<Relation> outgoingRelations = new ArrayList<>(predicate.getOutgoingRelations());
                Collections.sort(outgoingRelations, relationComparator);

                List<String> arguments = new ArrayList<>();
                view.add(a);

                int numOfArguments = outgoingRelations.size();
                for (int k = 0; k < numOfArguments; k++) { // for each argument
                    Relation r = outgoingRelations.get(k);
                    Constituent argument = r.getTarget();

                    String relationName = r.getRelationName();
                    String relationId = a.getId() + "-f" + k;
                    arguments.add(relationId); // getting id for arguments to add to the head annotation

                    int argStart = argument.getStartCharOffset();
                    int argEnd = argument.getEndCharOffset();

                    // annotation for the argument
                    Annotation argAnnotation = new Annotation(relationId, relationName, argStart, argEnd);
                    argAnnotation.setAtType(Discriminators.Uri.MARKABLE);
                    argAnnotation.addFeature(Discriminators.Uri.TEXT, argument.getTokenizedSurfaceForm());
                    view.add(argAnnotation);
                }

                a.addFeature(Features.SemanticRole.ARGUMENT, arguments);
            }
            container.addView(view);
        } // end processing one type of SRL

        data = new DataContainer(container);

        return data.asPrettyJson();
    }
}



