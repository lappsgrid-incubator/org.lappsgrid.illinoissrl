package org.lappsgrid.illinoissrl;


import edu.illinois.cs.cogcomp.core.datastructures.textannotation.PredicateArgumentView;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.core.utilities.configuration.ResourceManager;
import edu.illinois.cs.cogcomp.srl.SemanticRoleLabeler;
import edu.illinois.cs.cogcomp.srl.experiment.TextPreProcessor;
import org.lappsgrid.api.ProcessingService;

import java.io.IOException;

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
            ta = TextPreProcessor.getInstance().preProcessText(input);
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }

        PredicateArgumentView p;
        try {
            p = nomSRL.getSRL(ta);
            System.out.println(p);
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }


        try {
            p = verbSRL.getSRL(ta);
            System.out.println(p);
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
        return null;
    }
}



