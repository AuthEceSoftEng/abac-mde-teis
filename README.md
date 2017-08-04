# A Low-Code Platform for Generating Secure by Design Enterprise Services
This repository accompanies the paper entitled: "A Low-Code Platform for Generating Secure by Design Enterprise Services".

## Contents

- restreviews-src: contains the source code generated from the model-driven
engineering (MDE) engine.
- api-tests: contains the scripts for running RESTful API tests for checking the
validity of the authorization MDE mechanism

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
