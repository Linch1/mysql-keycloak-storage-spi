# A custom Keycloak User Storage Provider

- This example demonstrates how to deploy custom Keycloak User storage provider as an `.ear`.  Deploying it as `.ear` **allows** to use **custom dependencies** that are **not part of the keycloak module space**.  
- The custom storage provider is implemented in the `jar-module` derectory.
- This custom storage uses **mysql** as **database**, in the `mysql-keycloak-storage-spi/jar-module/src/main/resources/hibernate.cfg.xml` file **you must update** the mysql **access credentials**.

>This example is based on [keycloak-user-spi-demo](https://github.com/dasniko/keycloak-user-spi-demo) by [@dasniko](https://github.com/dasniko).

### Prepare
- `$ KEYCLOAK_HOME=/home/tom/dev/playground/keycloak/keycloak-3.3.0.CR1 { <-- replace the path with your keycloak path }`

### Prepare Keycloak
Run [setup.cli](./setup.cli) with
This configures the custom logger and registers the static configuration 
of the user federation provider. 
- `$ echo "yes" | $KEYCLOAK_HOME/bin/jboss-cli.sh --file=$KEYCLOAK_HOME/setup.cli`

### Start Keycloak
- `$ cd $KEYCLOAK_HOME`
- `$ bin/standalone.sh -c standalone-ha.xml`

### Deployment ( Build and Deploy the .ear )
Build and Deploy `.ear` package to wildfly.
This copies the `.ear` file to `standalone/deployments` folder.
- `$ mvn clean install wildfly:deploy -Djboss-as.home=$KEYCLOAK_HOME`
