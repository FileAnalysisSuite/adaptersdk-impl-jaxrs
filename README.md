# Java Adapter SDK JAX-RS Implementation

This module implements the [FAS Adapter REST Contract](https://github.com/FileAnalysisSuite/adapter-rest-contract/blob/main/adapter-rest-contract/src/main/resources/io/github/fileanalysissuite/adapters/rest/contract/swagger.yaml#L1) using [Jakarta RESTful Web Services 2.1](https://jakarta.ee/specifications/restful-ws/2.1/).

It implements the [Java Adapter SDK Framework Interfaces](https://github.com/FileAnalysisSuite/adaptersdk-interfaces#framework), so that adapters written against the [Java Adapter SDK](https://github.com/FileAnalysisSuite/adaptersdk-interfaces) can be hosted on any platform that supports [Jakarta EE 8](https://jakarta.ee/release/8/).

### Usage

There is only one method exposed - the [AdapterSdk::wrap](https://github.com/FileAnalysisSuite/adaptersdk-impl-jaxrs/blob/main/src/main/java/io/github/fileanalysissuite/adaptersdk/impls/jaxrs/AdapterSdk.java#L37) method.

This method takes a custom adapter's implementation of the [`RepositoryAdapter`](https://github.com/FileAnalysisSuite/adaptersdk-interfaces/blob/main/src/main/java/io/github/fileanalysissuite/adaptersdk/interfaces/extensibility/RepositoryAdapter.java#L16) interface, and it returns a set of objects which should be registered as singletons with the JAX-RS implementation being used for hosting.

#### DropWizard Example
Here is an example taken from the REST Filesystem Adapter that is hosted in DropWizard:  
[`restadapter-filesystem-dropwizard/.../FileSystemAdapterApplication.java:44-46`](https://github.com/FileAnalysisSuite/restadapter-filesystem/blob/aff557e20cd56b835d252d6d005e026731a730b0/restadapter-filesystem-dropwizard/src/main/java/io/github/fileanalysissuite/restadapters/filesystem/dropwizard/FileSystemAdapterApplication.java#L44-L46)

    for (final Object singleton : AdapterSdk.wrap(new FileSystemAdapter())) {
        jersey.register(singleton);
    }

#### WAR File Example
Here is another example taken from the REST Filesystem Adapter that is packaged as a [WAR file](https://eclipse-ee4j.github.io/jakartaee-tutorial/#packaging-web-archives):  
[`restadapter-filesystem-war/.../FileSystemAdapterApplication.java:31`](https://github.com/FileAnalysisSuite/restadapter-filesystem/blob/aff557e20cd56b835d252d6d005e026731a730b0/restadapter-filesystem-war/src/main/java/io/github/fileanalysissuite/restadapters/filesystem/restadapter/filesystem/war/FileSystemAdapterApplication.java#L31)
