package mysql.storage.main;

import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.adapter.AbstractUserAdapterFederatedStorage;

import mysql.storage.entity.User;

public class UserAdapter extends AbstractUserAdapterFederatedStorage {

  private final User user;
  private final String keycloakId;

  public UserAdapter(KeycloakSession session, RealmModel realm, ComponentModel model, User user) {
    super(session, realm, model);
    this.user = user;
    this.keycloakId = StorageId.keycloakId(model, String.valueOf(user.getId()) ); 
  }

  @Override
  public String getId() {
    return keycloakId;
  }

  @Override
  public String getUsername() {
    return user.getEmail();
  }

  @Override
  public void setUsername(String username) {
    user.setEmail(username);
  }

  @Override
  public String getEmail() {
    return user.getEmail();
  }

  @Override
  public void setEmail(String email) {
    user.setEmail(email);
  }

  @Override
  public String getFirstName() {
    return user.getFirstName();
  }

  @Override
  public void setFirstName(String firstName) {
    user.setFirstName(firstName);
  }

  @Override
  public String getLastName() {
    return user.getLastName();
  }

  @Override
  public void setLastName(String lastName) {
    user.setLastName(lastName);
  }
}
