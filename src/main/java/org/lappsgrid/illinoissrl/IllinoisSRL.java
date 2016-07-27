package org.lappsgrid.illinoissrl;


import edu.illinois.cs.cogcomp.core.datastructures.textannotation.*;
import edu.illinois.cs.cogcomp.core.utilities.configuration.ResourceManager;
import edu.illinois.cs.cogcomp.srl.SemanticRoleLabeler;
import edu.illinois.cs.cogcomp.srl.core.SRLType;
import edu.illinois.cs.cogcomp.srl.experiment.TextPreProcessor;
import org.lappsgrid.api.ProcessingService;
import org.lappsgrid.discriminator.Discriminators;
import org.lappsgrid.serialization.Data;
import org.lappsgrid.serialization.DataContainer;
import org.lappsgrid.serialization.Serializer;
import org.lappsgrid.serialization.lif.Annotation;
import org.lappsgrid.serialization.lif.Container;
import org.lappsgrid.serialization.lif.View;

import java.io.IOException;
import java.util.*;

public class IllinoisSRL implements ProcessingService {
    Comparator<Relation> relationComparator = new Comparator<Relation>() {
        @Override
        public int compare(Relation arg0, Relation arg1) {
            return arg0.getRelationName().compareTo(arg1.getRelationName());
        }
    };

    public IllinoisSRL() {
    }

    @Override
    public String getMetadata() {
        return null;
    }

    private ResourceManager rm;

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
        Container container = null;
        if (discriminator.equals(Discriminators.Uri.TEXT)) {
            container = new Container();
            container.setText(data.getPayload().toString());
        } else if (discriminator.equals(Discriminators.Uri.LAPPS)) {
            container = new Container((Map) data.getPayload());
        } else {
            // This is a format we don't accept.
            String message = String.format("Unsupported discriminator type: %s", discriminator);
            return new Data<String>(Discriminators.Uri.ERROR, message).asJson();
        }

        String text = container.getText();


        // loading SRL configuration file
        try {
            rm = new ResourceManager("config/srl-config.properties");
        } catch (IOException e) {
            e.printStackTrace();
            String message = "Unable to load SRL configuration file.";
            return new Data<>(Discriminators.Uri.ERROR, message).asJson();
        }


        // types are Nom SRL and Verb SRL
        for (SRLType type : SRLType.values()) {
            // loading the SRL
            SemanticRoleLabeler srl;
            try {
                srl = new SemanticRoleLabeler(rm, type.name(), true);
            } catch (Exception e) {
                e.printStackTrace();
                String message = "Unable to load the semantic role labeler.";
                return new Data<>(Discriminators.Uri.ERROR, message).asJson();
            }

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
            org.lappsgrid.serialization.lif.View view = new View();
            view.addContains(Discriminators.Uri.JSON, this.getClass().getName(), type.name() + " SRL");

            // add annotations to view
            List<Constituent> predicates = new ArrayList<>(pav.getPredicates());
            Collections.sort(predicates, TextAnnotationUtilities.constituentStartComparator);
            for (Constituent predicate : predicates) {
                Annotation a = new Annotation(pav.getPredicateLemma(predicate), 0, 0);

                List<Relation> outgoingRelations = new ArrayList<>(predicate.getOutgoingRelations());
                Collections.sort(outgoingRelations, relationComparator);
                for (Relation r : outgoingRelations) {
                    Constituent target = r.getTarget();
                    a.addFeature(r.getRelationName(), target.getTokenizedSurfaceForm());
                }
                view.add(a);
            }
            container.addView(view);
        } // end processing one type of SRL

        data = new DataContainer(container);

        return data.asPrettyJson();
    }
}



