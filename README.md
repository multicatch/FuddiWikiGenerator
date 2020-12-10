# FuddiWikiGenerator
A tool to generate wiki pages from ontology

This tools reads an ontology and gathers all subjects from it, then it uses a template file to generate articles describing
the contents of this ontology. Finally, it connects to a MediaWiki API and creates/updates articles.

Read more at:
* https://fuddi.eu
* https://kb.fuddi.eu

## Under the hood

It uses [Apache Jena](https://jena.apache.org/) to read a given ontology and [Pebble Templates](https://pebbletemplates.io/).

The source file and template file have to be specified by the program arguments.

## Running it

There are the following arguments that have to be set in order to run the generator:

* `source-file` - specifies the path to an RDF file containing the ontology.
* `template-directory` - specifies the path to a directory with templates used to generate articles (NOTE: the path has to start with `./` or `/` to be read properly by Pebble Templates).
* `wikimedia.url` - URL to the WikiMedia API.
* `wikimedia.login` - login of the bot account.
* `wikimedia.password` - password of the bot account.

The `template-directory` needs to contain at least `default.txt` file. 
This tool expects files with the `.txt` extension for any language used in the ontology.
For example, there can be `en.txt` file.

Example:

```bash
java -jar fuddi-wiki-generator-1.0-SNAPSHOT-jar-with-dependencies.jar \
    -source-file ontology.rdf \
    -template-directory ./templates/ \
    -wikimedia.url https://kb.fuddi.eu/api.php \
    -wikimedia.login WikiBot \
    -wikimedia.password BotPassword
```

## Available variables

When creating a custom template, keep in mind that there are some variables that are available to use:

* `subjectUri` - an object of type [URIRef](src/main/java/eu/fuddi/rdf/namespaces.kt) describing a subject URI
* `subjectType` - an object of type [URIRef](src/main/java/eu/fuddi/rdf/namespaces.kt) describing a subject type
* all namespaces from the source ontology as [Namespace](src/main/java/eu/fuddi/rdf/namespaces.kt) objects (eg. `owl`, `rdf` or others)
* `nsName` - a map with keys of type [Namespace](src/main/java/eu/fuddi/rdf/namespaces.kt) and values with namespace prefixes 
used to lookup namespaces (eg. you can use this to find out that `Namespace(http://www.w3.org/2002/07/owl#)` is prefixed as `owl`)
* `properties` - a map containing all subject properties grouped by the predicate (of type [URIRef](src/main/java/eu/fuddi/rdf/namespaces.kt))

### Example usage

Retrieving full subject URI:
```
{{ subjectUri.uri }}
```

Retrieving full subject type URI and only the identifier:
```
URI: {{ subjectType.uri }}
Identifier: {{ subjectType.identifier }}
```

Subject type namespace lookup (checking prefix associated with namespace):
```
{{ nsName[subjectType.namespace] }}
```

Retrieving an URI of owl:Thing:
```
{{ owl.get("Thing").uri }}
```

Retrieving all labels associated with subject (NOTE: it returns a list of object of type [SubjectProperty](src/main/java/eu/fuddi/rdf/parser.kt)):
```
{{ properties.get(rdfs.get("label")) }}
```

Displaying subject's label in a readable form:
```
{{ properties.get(rdfs.get("label"))[0].valueLiteral.value }}
```

## License

All code stored here is licensed under MIT License.