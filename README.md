## Pipeline Status

### Dev branch

[![Pipeline Status](https://gketaas.jaas-gcp.cloud.sap.corp/job/taasCF/job/tax-maestro/job/dev/badge/icon?subject=Pipeline)](https://gketaas.jaas-gcp.cloud.sap.corp/job/taasCF/job/tax-maestro/job/dev/)
[![CI Build Status](https://prod-build10000.wdf.sap.corp:443/job/tax-service/job/tax-service-tax-maestro-SP-MS-common/badge/icon?subject=CI%20Build)](https://prod-build10000.wdf.sap.corp:443/job/tax-service/job/tax-service-tax-maestro-SP-MS-common/)
[![Quality Gate](https://sonarci.wdf.sap.corp:8443/sonar/api/badges/gate?key=com.sap.slh.tax-tax-maestro-dev)](https://sonarci.wdf.sap.corp:8443/sonar/dashboard?id=com.sap.slh.tax-tax-maestro-dev)
[![Tests](https://sonarci.wdf.sap.corp:8443/sonar/api/badges/measure?key=com.sap.slh.tax-tax-maestro-dev&metric=tests)](https://sonarci.wdf.sap.corp:8443/sonar/component_measures/metric/tests?id=com.sap.slh.tax-tax-maestro-dev)
[![Coverage](https://sonarci.wdf.sap.corp:8443/sonar/api/badges/measure?key=com.sap.slh.tax-tax-maestro-dev&metric=coverage)](https://sonarci.wdf.sap.corp:8443/sonar/component_measures/domain/Coverage?id=com.sap.slh.tax-tax-maestro-dev)
[![Bugs](https://sonarci.wdf.sap.corp:8443/sonar/api/badges/measure?key=com.sap.slh.tax-tax-maestro-dev&metric=bugs)](https://sonarci.wdf.sap.corp:8443/sonar/project/issues?id=com.sap.slh.tax-tax-maestro-dev&resolved=false&types=BUG)
[![Vulnerabilities](https://sonarci.wdf.sap.corp:8443/sonar/api/badges/measure?key=com.sap.slh.tax-tax-maestro-dev&metric=vulnerabilities)](https://sonarci.wdf.sap.corp:8443/sonar/project/issues?id=com.sap.slh.tax-tax-maestro-dev&resolved=false&types=VULNERABILITY)
[![Code Smells](https://sonarci.wdf.sap.corp:8443/sonar/api/badges/measure?key=com.sap.slh.tax-tax-maestro-dev&metric=code_smells)](https://sonarci.wdf.sap.corp:8443/sonar/project/issues?com.sap.slh.tax-tax-maestro-dev&resolved=false&types=CODE_SMELL)

[Sonar](https://sonarci.wdf.sap.corp:8443/sonar/dashboard?id=com.sap.slh.tax-tax-maestro-dev), 
[Fortify results](https://fortify.mo.sap.corp/ssc/html/ssc/version/22405/fix/null/?filterSet=a243b195-0a59-3f8b-1403-d55b7a7d78e6)

## Package Structure

The packages are splited in two basically layers:

1. `com.sap.slh.tax.maestro.*`: packages that are relevant for the whole WebFlux application
2. `com.sap.slh.tax.maestro.{api}`: packages that are relevant for a specific API.

Packages can be created under one of these layers to help to organize the classes in a logical groups. For example, the `com.sap.slh.tax.maestro` has the `config` package that groups configuration classes for the WebFlux application. Similarly, an API could have a `config` package groups configuration classes relevant only for its API.

## Pack and Run

To pack the application and run do the following:

```console
$ mvn clean install
$ java -jar tax-maestro-web/target/tax-maestro-web.jar
```

Or to run using a specific profile, e.g.: `NoSecurity`, use:

```console
$ mvn clean install
$ java -jar tax-maestro-web/target/tax-maestro-web.jar --spring.profiles.active=NoSecurity
```

## VCAP_SERVICES

A `VCAP_SERVICES` enviroment variable correctly defined is required to run spring-boot locally with security using the remote XSUAA, e.g.:

```json
{"xsuaa": [{"label": "xsuaa","provider": null,"plan": "broker","name": "txs-xsuaa","tags": ["xsuaa"],"instance_name": "txs-xsuaa","binding_name": null,"credentials": {"tenantmode": "dedicated","sburl": "https://internal-xsuaa.authentication.sap.hana.ondemand.com","clientid": "sb-na-45faefcf-ccbf-4ee8-b340-54555099efad!b5520","xsappname": "na-45faefcf-ccbf-4ee8-b340-54555099efad!b5520","clientsecret": "uxdPANa/00WpdziHZYl7zYPXLzA=","url": "https://taasgs.authentication.sap.hana.ondemand.com","uaadomain": "authentication.sap.hana.ondemand.com","trustedclientidsuffix": "|na-45faefcf-ccbf-4ee8-b340-54555099efad!b5520","verificationkey": "-----BEGIN PUBLIC KEY-----MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAx/jN5v1mp/TVn9nTQoYVIUfCsUDHa3Upr5tDZC7mzlTrN2PnwruzyS7w1Jd+StqwW4/vn87ua2YlZzU8Ob0jR4lbOPCKaHIi0kyNtJXQvQ7LZPG8epQLbx0IIP/WLVVVtB8bL5OWuHma3pUnibbmATtbOh5LksQ2zLMngEjUF52JQyzTpjoQkahp0BNe/drlAqO253keiY63FL6belKjJGmSqdnotSXxB2ym+HQ0ShaNvTFLEvi2+ObkyjGWgFpQaoCcGq0KX0y0mPzOvdFsNT+rBFdkHiK+Jl638Sbim1z9fItFbH9hiVwY37R9rLtH1YKi3PuATMjf/DJ7mUluDQIDAQAB-----END PUBLIC KEY-----","identityzone": "taasgs","identityzoneid": "f2e8bf04-a952-4394-a67a-af22cdb9b524","tenantid": "f2e8bf04-a952-4394-a67a-af22cdb9b524"},"syslog_drain_url": null,"volume_mounts": []}],"rabbitmq": [{"label": "rabbitmq","provider": null,"plan": "v3.7-dev","name": "txs-rabbitmq","tags": ["rabbitmq","mbus","pubsub","amqp"],"instance_name": "txs-rabbitmq","binding_name": null,"credentials": {"hostname": "10.11.241.119","ports": {"15672/tcp": "46578","15674/tcp": "48320","15675/tcp": "54265","1883/tcp": "49336","5672/tcp": "40757","61613/tcp": "59260"},"port": "40757","username": "ZkmR9K4sQGH-1AQN","password": "zCNtqgcwK7XRHzn9","uri": "amqp://ZkmR9K4sQGH-1AQN:zCNtqgcwK7XRHzn9@10.11.241.119:40757","end_points": [{  "network_id": "SF", "host": "10.11.241.119",    "port": "40757"}]},"syslog_drain_url": null,"volume_mounts": []}],"redis": [{"label": "redis","provider": null,"plan": "v3.0-dev","name": "txs-redis","tags": ["redis","keyvalue"],"instance_name": "txs-redis","binding_name": null,"credentials": {"hostname": "10.11.241.119","ports": {"6379/tcp": "47907"},"port": "47907","password": "vH2FhzlDX5YwjgDK","end_points": [{   "network_id": "SF", "host": "10.11.241.119",    "port": "47907"}]},"syslog_drain_url": null,"volume_mounts": []}],"destination": [{"label": "destination","provider": null,"plan": "lite","name": "txs-destination","tags": ["destination","conn","connsvc"],"instance_name": "txs-destination","binding_name": null,"credentials": {"uaadomain": "authentication.sap.hana.ondemand.com","tenantmode": "dedicated","clientid": "sb-clone2441a26ec5cf4c32a410f2cf0991e2b1!b5520|destination-xsappname!b433","instanceid": "2441a26e-c5cf-4c32-a410-f2cf0991e2b1","verificationkey": "-----BEGIN PUBLIC KEY-----MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAx/jN5v1mp/TVn9nTQoYVIUfCsUDHa3Upr5tDZC7mzlTrN2PnwruzyS7w1Jd+StqwW4/vn87ua2YlZzU8Ob0jR4lbOPCKaHIi0kyNtJXQvQ7LZPG8epQLbx0IIP/WLVVVtB8bL5OWuHma3pUnibbmATtbOh5LksQ2zLMngEjUF52JQyzTpjoQkahp0BNe/drlAqO253keiY63FL6belKjJGmSqdnotSXxB2ym+HQ0ShaNvTFLEvi2+ObkyjGWgFpQaoCcGq0KX0y0mPzOvdFsNT+rBFdkHiK+Jl638Sbim1z9fItFbH9hiVwY37R9rLtH1YKi3PuATMjf/DJ7mUluDQIDAQAB-----END PUBLIC KEY-----","xsappname": "clone2441a26ec5cf4c32a410f2cf0991e2b1!b5520|destination-xsappname!b433","identityzone": "taasgs","clientsecret": "9GJ08VlyAIRTQP1/tCBGt0Gres4=","tenantid": "f2e8bf04-a952-4394-a67a-af22cdb9b524","uri": "https://destination-configuration.cfapps.sap.hana.ondemand.com","url": "https://taasgs.authentication.sap.hana.ondemand.com"},"syslog_drain_url": null,"volume_mounts": []}]}
```

### VS Code

On VS Code add `"env": { "VCAP_SERVICES": "..." }` to `launch.json` to provide the environment to the running application:

```json
{
    "configurations": [
        {
            "env": {
                "VCAP_SERVICES": "{\"xsuaa\":[{\"label\":\"xsuaa\",\"provider\":null,\"plan\":\"broker\",\"name\":\"txs-xsuaa\",\"tags\":[\"xsuaa\"],\"instance_name\":\"txs-xsuaa\",\"binding_name\":null,\"credentials\":{\"tenantmode\":\"dedicated\",\"sburl\":\"https:\/\/internal-xsuaa.authentication.sap.hana.ondemand.com\",\"clientid\":\"sb-na-45faefcf-ccbf-4ee8-b340-54555099efad!b5520\",\"xsappname\":\"na-45faefcf-ccbf-4ee8-b340-54555099efad!b5520\",\"clientsecret\":\"uxdPANa\/00WpdziHZYl7zYPXLzA=\",\"url\":\"https:\/\/taasgs.authentication.sap.hana.ondemand.com\",\"uaadomain\":\"authentication.sap.hana.ondemand.com\",\"trustedclientidsuffix\":\"|na-45faefcf-ccbf-4ee8-b340-54555099efad!b5520\",\"verificationkey\":\"-----BEGIN PUBLIC KEY-----MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAx\/jN5v1mp\/TVn9nTQoYVIUfCsUDHa3Upr5tDZC7mzlTrN2PnwruzyS7w1Jd+StqwW4\/vn87ua2YlZzU8Ob0jR4lbOPCKaHIi0kyNtJXQvQ7LZPG8epQLbx0IIP\/WLVVVtB8bL5OWuHma3pUnibbmATtbOh5LksQ2zLMngEjUF52JQyzTpjoQkahp0BNe\/drlAqO253keiY63FL6belKjJGmSqdnotSXxB2ym+HQ0ShaNvTFLEvi2+ObkyjGWgFpQaoCcGq0KX0y0mPzOvdFsNT+rBFdkHiK+Jl638Sbim1z9fItFbH9hiVwY37R9rLtH1YKi3PuATMjf\/DJ7mUluDQIDAQAB-----END PUBLIC KEY-----\",\"identityzone\":\"taasgs\",\"identityzoneid\":\"f2e8bf04-a952-4394-a67a-af22cdb9b524\",\"tenantid\":\"f2e8bf04-a952-4394-a67a-af22cdb9b524\"},\"syslog_drain_url\":null,\"volume_mounts\":[]}],\"rabbitmq\":[{\"label\":\"rabbitmq\",\"provider\":null,\"plan\":\"v3.7-dev\",\"name\":\"txs-rabbitmq\",\"tags\":[\"rabbitmq\",\"mbus\",\"pubsub\",\"amqp\"],\"instance_name\":\"txs-rabbitmq\",\"binding_name\":null,\"credentials\":{\"hostname\":\"10.11.241.119\",\"ports\":{\"15672\/tcp\":\"46578\",\"15674\/tcp\":\"48320\",\"15675\/tcp\":\"54265\",\"1883\/tcp\":\"49336\",\"5672\/tcp\":\"40757\",\"61613\/tcp\":\"59260\"},\"port\":\"40757\",\"username\":\"ZkmR9K4sQGH-1AQN\",\"password\":\"zCNtqgcwK7XRHzn9\",\"uri\":\"amqp:\/\/ZkmR9K4sQGH-1AQN:zCNtqgcwK7XRHzn9@10.11.241.119:40757\",\"end_points\":[{\"network_id\":\"SF\",\"host\":\"10.11.241.119\",\"port\":\"40757\"}]},\"syslog_drain_url\":null,\"volume_mounts\":[]}],\"redis\":[{\"label\":\"redis\",\"provider\":null,\"plan\":\"v3.0-dev\",\"name\":\"txs-redis\",\"tags\":[\"redis\",\"keyvalue\"],\"instance_name\":\"txs-redis\",\"binding_name\":null,\"credentials\":{\"hostname\":\"10.11.241.119\",\"ports\":{\"6379\/tcp\":\"47907\"},\"port\":\"47907\",\"password\":\"vH2FhzlDX5YwjgDK\",\"end_points\":[{\"network_id\":\"SF\",\"host\":\"10.11.241.119\",\"port\":\"47907\"}]},\"syslog_drain_url\":null,\"volume_mounts\":[]}],\"destination\":[{\"label\":\"destination\",\"provider\":null,\"plan\":\"lite\",\"name\":\"txs-destination\",\"tags\":[\"destination\",\"conn\",\"connsvc\"],\"instance_name\":\"txs-destination\",\"binding_name\":null,\"credentials\":{\"uaadomain\":\"authentication.sap.hana.ondemand.com\",\"tenantmode\":\"dedicated\",\"clientid\":\"sb-clone2441a26ec5cf4c32a410f2cf0991e2b1!b5520|destination-xsappname!b433\",\"instanceid\":\"2441a26e-c5cf-4c32-a410-f2cf0991e2b1\",\"verificationkey\":\"-----BEGIN PUBLIC KEY-----MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAx\/jN5v1mp\/TVn9nTQoYVIUfCsUDHa3Upr5tDZC7mzlTrN2PnwruzyS7w1Jd+StqwW4\/vn87ua2YlZzU8Ob0jR4lbOPCKaHIi0kyNtJXQvQ7LZPG8epQLbx0IIP\/WLVVVtB8bL5OWuHma3pUnibbmATtbOh5LksQ2zLMngEjUF52JQyzTpjoQkahp0BNe\/drlAqO253keiY63FL6belKjJGmSqdnotSXxB2ym+HQ0ShaNvTFLEvi2+ObkyjGWgFpQaoCcGq0KX0y0mPzOvdFsNT+rBFdkHiK+Jl638Sbim1z9fItFbH9hiVwY37R9rLtH1YKi3PuATMjf\/DJ7mUluDQIDAQAB-----END PUBLIC KEY-----\",\"xsappname\":\"clone2441a26ec5cf4c32a410f2cf0991e2b1!b5520|destination-xsappname!b433\",\"identityzone\":\"taasgs\",\"clientsecret\":\"9GJ08VlyAIRTQP1\/tCBGt0Gres4=\",\"tenantid\":\"f2e8bf04-a952-4394-a67a-af22cdb9b524\",\"uri\":\"https:\/\/destination-configuration.cfapps.sap.hana.ondemand.com\",\"url\":\"https:\/\/taasgs.authentication.sap.hana.ondemand.com\"},\"syslog_drain_url\":null,\"volume_mounts\":[]}]}",
            },
        }
    ]
}
```

## Profiles

**NoSecurity**

Run spring-boot with the `NoSecurity` profile so HTTP security is disable for all endpoint, check `com.sap.slh.tax.maestro.config.WebSecurityConfig#securityWebFilterChainNoSecurity` for more information.

This is usefull for local testing with no XSUAA.

### VS Code

On VS Code add `"args": "--spring.profiles.active=nosecurity"` to `launch.json` to select the profiles to run with:

```json
{
    "configurations": [
        {
            "args": "--spring.profiles.active=NoSecurity",
        }
    ]
}
```

## API Documentation

The API documentation of tax-maestro service is available in [OpenAPI](https://tax-maestro-swagger.internal.cfapps.sap.hana.ondemand.com/swagger-ui.html) format.
