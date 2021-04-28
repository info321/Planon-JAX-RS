# Planon JAX-RS

## Setup

1. You will need to obtain the necessary Planon .jar files to compile and deploy

    * client.api.utils-1.0.4.jar
    * com.planonsoftware.enterprise.service.api.v2-3.5.0.0-1.jar
    * com.planonsoftware.hades-18.0.35.0-35.jar
    * com.planonsoftware.json.server.api.v7-9.0.8.0-1.jar
    * com.planonsoftware.logging-4.0.5.0-1.jar
    * gson-2.8.6.jar
    * log4j-1.2.17.jar
    * org.apache.felix.dependencymanager-3.1.0.jar

2. Mount /tms/upload/servicessdk to /Volumes/tms/upload/servicessdk, the jar will automatically be copied here
3. Build the jar

```shell
mvn clean package
```

## Install

1. Upload to /tms/sdk
2. Reload TMS
3. Should see a message similar to the following in the catalina.out log


    ```

4. Service can be accessed via [https://environment/sdk](https://environment/sdk)
5. In System settings --> Password security --> Security --> Access keys
    1. Generate key pair
6. Verify access keys is setup in layout for accounts
7. Setup access key for account


