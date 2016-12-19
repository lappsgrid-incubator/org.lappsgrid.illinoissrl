## org.lappsgrid.illinoissrl
This repository includes the Semantic Role Labeler from the University of Illinois at Urbana-Champaign.

# Input
Each tool requires a LAPPS Grid Data object with the discriminator: [Discriminators.Uri.TEXT](http://vocab.lappsgrid.org/ns/media/text) and the text in the payload. There are no parameters for the SRL.
 
# Output
The output will be a Data object in the LAPPS Interchange format ([LIF](http://vocab.lappsgrid.org/ns/media/jsonld#lif)) with 2 views: "nom" and "verb", corresponding to the Noun SRL and the Verb SRL outputs, each with [SemanticRole](http://vocab.lappsgrid.org/SemanticRole) annotations to mark the head of each semantic role, along with [Markable](http://vocab.lappsgrid.org/Markable) annotations for the arguments of each semantic role. 

<details>
<summary>Example Output</summary>
````json
{
  "discriminator" : "http://vocab.lappsgrid.org/ns/media/jsonld#lif",
  "payload" : {
    "@context" : "http://vocab.lappsgrid.org/context-1.0.0.jsonld",
    "metadata" : { },
    "text" : {
      "@value" : "In order to succeed, we must first believe that we can."
    },
    "views" : [ {
      "id" : "nom",
      "metadata" : {
        "contains" : {
          "http://vocab.lappsgrid.org/SemanticRole" : {
            "producer" : "org.lappsgrid.illinoissrl.IllinoisSRL",
            "type" : "srl:uiuc"
          }
        }
      },
      "annotations" : [ {
        "id" : "nom-0",
        "start" : 3,
        "end" : 8,
        "@type" : "http://vocab.lappsgrid.org/SemanticRole",
        "label" : "Role",
        "features" : {
          "head" : "order",
          "argument" : [ "nom-0-f0" ]
        }
      }, {
        "id" : "nom-0-f0",
        "start" : 9,
        "end" : 19,
        "@type" : "http://vocab.lappsgrid.org/Markable",
        "label" : "A1",
        "features" : {
          "http://vocab.lappsgrid.org/ns/media/text" : "to succeed"
        }
      } ]
    }, {
      "id" : "verb",
      "metadata" : {
        "contains" : {
          "http://vocab.lappsgrid.org/SemanticRole" : {
            "producer" : "org.lappsgrid.illinoissrl.IllinoisSRL",
            "type" : "srl:uiuc"
          }
        }
      },
      "annotations" : [ {
        "id" : "verb-0",
        "start" : 35,
        "end" : 42,
        "@type" : "http://vocab.lappsgrid.org/SemanticRole",
        "label" : "Role",
        "features" : {
          "head" : "believe",
          "argument" : [ "verb-0-f0", "verb-0-f1", "verb-0-f2", "verb-0-f3", "verb-0-f4" ]
        }
      }, {
        "id" : "verb-0-f0",
        "start" : 21,
        "end" : 23,
        "@type" : "http://vocab.lappsgrid.org/Markable",
        "label" : "A0",
        "features" : {
          "http://vocab.lappsgrid.org/ns/media/text" : "we"
        }
      }, {
        "id" : "verb-0-f1",
        "start" : 43,
        "end" : 54,
        "@type" : "http://vocab.lappsgrid.org/Markable",
        "label" : "A1",
        "features" : {
          "http://vocab.lappsgrid.org/ns/media/text" : "that we can"
        }
      }, {
        "id" : "verb-0-f2",
        "start" : 0,
        "end" : 19,
        "@type" : "http://vocab.lappsgrid.org/Markable",
        "label" : "AM-LOC",
        "features" : {
          "http://vocab.lappsgrid.org/ns/media/text" : "In order to succeed"
        }
      }, {
        "id" : "verb-0-f3",
        "start" : 24,
        "end" : 28,
        "@type" : "http://vocab.lappsgrid.org/Markable",
        "label" : "AM-MOD",
        "features" : {
          "http://vocab.lappsgrid.org/ns/media/text" : "must"
        }
      }, {
        "id" : "verb-0-f4",
        "start" : 29,
        "end" : 34,
        "@type" : "http://vocab.lappsgrid.org/Markable",
        "label" : "AM-TMP",
        "features" : {
          "http://vocab.lappsgrid.org/ns/media/text" : "first"
        }
      }, {
        "id" : "verb-1",
        "start" : 35,
        "end" : 42,
        "@type" : "http://vocab.lappsgrid.org/SemanticRole",
        "label" : "Role",
        "features" : {
          "head" : "believe",
          "argument" : [ "verb-1-f0", "verb-1-f1", "verb-1-f2", "verb-1-f3", "verb-1-f4" ]
        }
      }, {
        "id" : "verb-1-f0",
        "start" : 21,
        "end" : 23,
        "@type" : "http://vocab.lappsgrid.org/Markable",
        "label" : "A0",
        "features" : {
          "http://vocab.lappsgrid.org/ns/media/text" : "we"
        }
      }, {
        "id" : "verb-1-f1",
        "start" : 43,
        "end" : 54,
        "@type" : "http://vocab.lappsgrid.org/Markable",
        "label" : "A1",
        "features" : {
          "http://vocab.lappsgrid.org/ns/media/text" : "that we can"
        }
      }, {
        "id" : "verb-1-f2",
        "start" : 0,
        "end" : 19,
        "@type" : "http://vocab.lappsgrid.org/Markable",
        "label" : "AM-LOC",
        "features" : {
          "http://vocab.lappsgrid.org/ns/media/text" : "In order to succeed"
        }
      }, {
        "id" : "verb-1-f3",
        "start" : 24,
        "end" : 28,
        "@type" : "http://vocab.lappsgrid.org/Markable",
        "label" : "AM-MOD",
        "features" : {
          "http://vocab.lappsgrid.org/ns/media/text" : "must"
        }
      }, {
        "id" : "verb-1-f4",
        "start" : 29,
        "end" : 34,
        "@type" : "http://vocab.lappsgrid.org/Markable",
        "label" : "AM-TMP",
        "features" : {
          "http://vocab.lappsgrid.org/ns/media/text" : "first"
        }
      } ]
    } ]
  },
  "parameters" : { }
}
````
</details>