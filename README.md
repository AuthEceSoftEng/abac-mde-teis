# A Low-Code Platform for Generating Secure by Design Enterprise Services
This repository accompanies the paper entitled: "A Low-Code Platform for Generating Secure by Design Enterprise Services".

## Contents

- restreviews-src: contains the source code generated from the model-driven
engineering (MDE) engine.
- api-tests: contains the scripts for running RESTful API tests for checking the
validity of the authorization MDE mechanism
- Tests-Reports.pdf: full list of all the performed tests for each resource of the RESTReviews Web Service as well as the related results in the same format as in the paper.
- Rule-operator-list.pdf: full list of all the available ABAC rule operators defined in the ABAC meta-model, alongside their explanation.
- Truth-tables.pdf: includes the truth tables for conditions, rules and policies evaluation.
- Output-layout.pdf: provides the template layout of any generated RESTful Service using our mechanism with respect to ABAC PDP, PIP and PEP conepts.
- Migration-behavioural-constraints.pdf: provides the list of the database migration meta-model behavioral constraints.
- Resource-Rules.pdf: provides the list with all the defined ABAC rules for the resources of the RESTReviews Web Service.
- RESTsec meta-models.pdf: provides the definition of PIM and PSM meta-models of the RESTsec system.
- IoT-App-Definition.pdf: provides the definition of the IoT application referenced in the evaluation section of the manuscript.
- Abac-metamodel-behavioral.pdf: provides the list with the ABAC meta-model principal behavioral constraints.
- Ent-app-presentation.pdf: provides the overview of the Ent application referenced in the evaluation section of the manuscript.
- MDEMigratorCIMMetamodel.ecore: the full Database Migration CIM meta-model.

## Running the tests

1. Configure the `hibernate.cfg.xml` (restreviews-rsc > src > main > webapp > WEB-INF > classes)
file to the setup of you current MySQL installation (basically the `username` and `password` fields)
2. Inside the restreviews-src run `mvn package` in order to generate the war file
inside the target directory
3. Download a Jetty distribution (in our case we used `jetty-distribution-9.4.6.v20170531`)
4. Copy and paste the generated web service folder from the `target` maven directory.
5. Start the Jetty container.
6. Execute the tests by running the script `restreviewtests.js`, which is found in the `api-tests` folder.
7. File `Tests-Report.pdf` contains the list of all the performed tests for each resource of the restreviews Web Service as well as the related results in the same format as in the paper.
