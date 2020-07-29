package mysql.storage.main;


import lombok.extern.jbosslog.JBossLog;
import mysql.storage.repo.UserRepo;

import org.keycloak.Config;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.provider.ProviderConfigurationBuilder;
import org.keycloak.storage.UserStorageProviderFactory;

import java.util.List;

@JBossLog
public class DemoUserStorageProviderFactory implements UserStorageProviderFactory<DemoUserStorageProvider> {

  @Override
  public void init(Config.Scope config) {

    // this configuration is pulled from the SPI configuration of this provider in the standalone[-ha] / domain.xml
    // see setup.cli

    String someProperty = config.get("someProperty");
    log.infov("Configured {0} with someProperty: {1}", this, someProperty);
  }

  @Override
  public DemoUserStorageProvider create(KeycloakSession session, ComponentModel model) {
    // here you can setup the user storage provider, initiate some connections, etc.
    return new DemoUserStorageProvider(session, model, new UserRepo());
  }

  @Override
  public String getId() {
    return "mysql-mysql-storage";
  }

  @Override
  public List<ProviderConfigProperty> getConfigProperties() {

    // this configuration is configurable in the admin-console
    return ProviderConfigurationBuilder.create()
      .property()
      .name("myParam")
      .label("My Param")
      .helpText("Some Description")
      .type(ProviderConfigProperty.STRING_TYPE)
      .defaultValue("some value")
      .add()
      // more properties
      // .property()
      // .add()
      .build();
  }
}