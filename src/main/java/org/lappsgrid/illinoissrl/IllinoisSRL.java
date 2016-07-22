package org.lappsgrid.illinoissrl;


import edu.illinois.cs.cogcomp.core.datastructures.textannotation.*;
import edu.illinois.cs.cogcomp.core.utilities.configuration.ResourceManager;
import edu.illinois.cs.cogcomp.srl.SemanticRoleLabeler;
import edu.illinois.cs.cogcomp.srl.experiment.TextPreProcessor;
import org.lappsgrid.api.ProcessingService;
import org.lappsgrid.discriminator.Discriminators;
import org.lappsgrid.serialization.Data;
import org.lappsgrid.serialization.DataContainer;
import org.lappsgrid.serialization.Serializer;
import org.lappsgrid.serialization.lif.*;
import org.lappsgrid.serialization.lif.View;

import java.io.IOException;
import java.util.*;

public class IllinoisSRL implements ProcessingService {
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
        }
        else if (discriminator.equals(Discriminators.Uri.LAPPS)) {
            container = new Container((Map) data.getPayload());
        }
        else {
            // This is a format we don't accept.
            String message = String.format("Unsupported discriminator type: %s", discriminator);
            return new Data<String>(Discriminators.Uri.ERROR, message).asJson();
        }

        String text = container.getText();

        try {
            rm = new ResourceManager("config/srl-config.properties");
        } catch (IOException e){
            e.printStackTrace();
            String s = "";
            return null;
        }

        SemanticRoleLabeler nomSRL, verbSRL;
        try {
            nomSRL = new SemanticRoleLabeler(rm, "Nom", true);
            verbSRL = new SemanticRoleLabeler(rm, "Verb", true);
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }


        TextAnnotation ta;
        try {
            ta = TextPreProcessor.getInstance().preProcessText(text);
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }

        PredicateArgumentView p1, p2;
        try {
            p1 = nomSRL.getSRL(ta);
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }


        try {
            p2 = verbSRL.getSRL(ta);
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }

        org.lappsgrid.serialization.lif.View view = new View();
        view.addContains(Discriminators.Uri.JSON, this.getClass().getName(), "Nom SRL");
        List<Constituent> predicates = new ArrayList<>(p1.getPredicates());
        Collections.sort(predicates, TextAnnotationUtilities.constituentStartComparator);
        for (Constituent predicate : predicates){
            System.out.println(p1.getPredicateLemma(predicate));
            Annotation a = new Annotation(p1.getPredicateLemma(predicate), 0, 0);

            List<Relation> outgoingRelations = new ArrayList<>(predicate.getOutgoingRelations());

            Collections.sort(outgoingRelations, new Comparator<Relation>() {

                @Override
                public int compare(Relation arg0, Relation arg1) {
                    return arg0.getRelationName().compareTo(arg1.getRelationName());
                }
            });

            for (Relation r : outgoingRelations) {
                Constituent target = r.getTarget();
                System.out.println(r.getRelationName());
                System.out.println(target.getTokenizedSurfaceForm());

                a.addFeature(r.getRelationName(), target.getTokenizedSurfaceForm());
            }
            view.add(a);
        }
        container.addView(view);

        view = new View();
        view.addContains(Discriminators.Uri.JSON, this.getClass().getName(), "Verb SRL");
        predicates = new ArrayList<>(p2.getPredicates());
        Collections.sort(predicates, TextAnnotationUtilities.constituentStartComparator);
        for (Constituent predicate : predicates){
            System.out.println(p2.getPredicateLemma(predicate));
            Annotation a = new Annotation(p2.getPredicateLemma(predicate), 0, 0);

            List<Relation> outgoingRelations = new ArrayList<>(predicate.getOutgoingRelations());

            Collections.sort(outgoingRelations, new Comparator<Relation>() {

                @Override
                public int compare(Relation arg0, Relation arg1) {
                    return arg0.getRelationName().compareTo(arg1.getRelationName());
                }
            });

            for (Relation r : outgoingRelations) {
                Constituent target = r.getTarget();
                System.out.println(r.getRelationName());
                System.out.println(target.getTokenizedSurfaceForm());

                a.addFeature(r.getRelationName(), target.getTokenizedSurfaceForm());
            }
            view.add(a);
        }
        container.addView(view);
        data = new DataContainer(container);

        System.out.println(p1);
        System.out.println(p2);


        return data.asPrettyJson();
    }
}



