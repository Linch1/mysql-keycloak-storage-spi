package mysql.storage.main;

import lombok.extern.jbosslog.JBossLog;
import mysql.storage.entity.User;
import mysql.storage.repo.UserRepo;

import org.keycloak.component.ComponentModel;
import org.keycloak.credential.CredentialInput;
import org.keycloak.credential.CredentialInputUpdater;
import org.keycloak.credential.CredentialInputValidator;
import org.keycloak.credential.CredentialModel;
import org.keycloak.models.GroupModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.RoleModel;
import org.keycloak.models.UserCredentialModel;
import org.keycloak.models.UserModel;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.storage.user.UserLookupProvider;
import org.keycloak.storage.user.UserQueryProvider;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@JBossLog
public class DemoUserStorageProvider implements UserStorageProvider,
  UserLookupProvider, UserQueryProvider, CredentialInputUpdater, CredentialInputValidator {

  private final KeycloakSession session;
  private final ComponentModel model;
  private final UserRepo repo;
  
  
  public DemoUserStorageProvider(KeycloakSession session, ComponentModel model, UserRepo repo) {
    this.repo = repo;
	this.session = session;
    this.model = model;
  }

  @Override
  public boolean supportsCredentialType(String credentialType) {
    return CredentialModel.PASSWORD.equals(credentialType);
  }

  @Override
  public boolean isConfiguredFor(RealmModel realm, UserModel user, String credentialType) {
    return supportsCredentialType(credentialType);
  }
  
  @Override
  public boolean isValid(RealmModel realm, UserModel user, CredentialInput input) {

    log.infov("\nStorage.isValid > user credential: userId={0}", user.getId());

    if (!supportsCredentialType(input.getType()) || !(input instanceof UserCredentialModel)) {
      return false;
    }
    UserCredentialModel cred = (UserCredentialModel) input;
    return repo.validateCredentials(user.getUsername(), cred.getChallengeResponse());
  }

  @Override
  public boolean updateCredential(RealmModel realm, UserModel user, CredentialInput input)  {
    log.infov("\nStorage.updateCredential > updating credential: realm={0} user={1}", realm.getId(), user.getUsername());
    if (!supportsCredentialType(input.getType()) || !(input instanceof UserCredentialModel)) {
      return false;
    }

    UserCredentialModel cred = (UserCredentialModel) input;
 
	return repo.updateCredentials(user.getUsername(), cred.getChallengeResponse());

  }

  @Override
  public void disableCredentialType(RealmModel realm, UserModel user, String credentialType) {
	  log.infov("\nStorage.disableCredentialType > [ NOT IMPLEMENTED ] ");
  }

  @Override
  public Set<String> getDisableableCredentialTypes(RealmModel realm, UserModel user) {
	log.infov("\nStorage.getDisableableCredentialTypes > [ NOT IMPLEMENTED ] ");
    return Collections.emptySet();
  }

  @Override
  public void preRemove(RealmModel realm) {
    log.infov("\nStorage.preRemove [REALM] > [ NOT IMPLEMENTED ] pre-remove realm");
  }

  @Override
  public void preRemove(RealmModel realm, GroupModel group) {

    log.infov("\nStorage.preRemove [GROUP] > [ NOT IMPLEMENTED ] pre-remove group");
  }

  @Override
  public void preRemove(RealmModel realm, RoleModel role) {
    log.infov("\nStorage.preRemove [ROLE] > [ NOT IMPLEMENTED ] pre-remove role");
  }

  @Override
  public void close() {
    log.infov("\nStorage.close > [ NOT IMPLEMENTED ] closing 0.");
  }

  @Override
  public UserModel getUserById(String id, RealmModel realm) {
    log.infov("\nStorage.getUserById > [ NOT IMPLEMENTED ] lookup user by id: realm={0} userId={1}", realm.getId(), id);
    String externalId = StorageId.externalId(id);
    return new UserAdapter(session, realm, model, repo.findUserById(externalId));
  }

  @Override
  public UserModel getUserByUsername(String username, RealmModel realm) {
    log.infov("\nStorage.getUserByUsername > 1. lookup user by username: realm={0} username={1}", realm.getId(), username);
    User found = repo.searchForUserByUsernameOrEmail(username);
    log.infov("\nStorage.getUserByUsername > Found: " + found.getNickName());
    return new UserAdapter(session, realm, model, found );
  }

  @Override
  public UserModel getUserByEmail(String email, RealmModel realm) {
    log.infov("\nStorage.getUserByEmail > lookup user by Email: realm={0} email={1}", realm.getId(), email);
    return getUserByUsername(email, realm);
  }

  @Override
  public int getUsersCount(RealmModel realm) {
	log.infov("\nStorage.getUsersCount > Getting user count");
    return repo.getUsersCount();
  }

  @Override
  public List<UserModel> getUsers(RealmModel realm) {

    log.infov("\nStorage.getUserslist > users: realm={0}", realm.getId());


	return repo.findAll().stream()
	  .map(user -> new UserAdapter(session, realm, model, user))
	  .collect(Collectors.toList());

  }

  @Override
  public List<UserModel> getUsers(RealmModel realm, int firstResult, int maxResults) {
    log.infov("\nStorage.getUsers [ MAX - MIN ] > list users: realm={0} firstResult={1} maxResults={2}", realm.getId(), firstResult, maxResults);
	return repo.findAll(firstResult, maxResults).stream()
	  .map(user -> new UserAdapter(session, realm, model, user))
	  .collect(Collectors.toList());
  }

  @Override
  public List<UserModel> searchForUser(String search, RealmModel realm) {
    log.infov("\nStorage.searchForUser > search for users: realm={0} search={1}", realm.getId(), search);
    return repo.findUsers(search, null, null).stream()
      .map(user -> new UserAdapter(session, realm, model, user))
      .collect(Collectors.toList());
  }

  @Override
  public List<UserModel> searchForUser(String search, RealmModel realm, int firstResult, int maxResults) {
    log.infov("\nStorage.searchForUser [ MAX - MIN ]  search for users: realm={0} search={1} firstResult={2} maxResults={3}", realm.getId(), search, firstResult, maxResults);
    return repo.findUsers(search, firstResult, maxResults).stream()
    	      .map(user -> new UserAdapter(session, realm, model, user))
    	      .collect(Collectors.toList());
  }

  @Override
  public List<UserModel> searchForUser(Map<String, String> params, RealmModel realm) {
    log.infov("\nStorage.searchForUser > [ NOT IMPLEMENTED ] search for users with params: realm={0} params={1}", realm.getId(), params);
    return null;
  }

  @Override
  public List<UserModel> searchForUser(Map<String, String> params, RealmModel realm, int firstResult, int maxResults) {
    log.infov("\nStorage.searchForUser > [ NOT TOTALLY IMPLEMENTED ] search for users with params: realm={0} params={1} firstResult={2} maxResults={3}", realm.getId(), params, firstResult, maxResults);
	log.infov("\nStorage.searchForUser > Querying db");
	return repo.findAll(firstResult, maxResults).stream()
	  .map(user -> new UserAdapter(session, realm, model, user))
	  .collect(Collectors.toList());
  }

  @Override
  public List<UserModel> getGroupMembers(RealmModel realm, GroupModel group, int firstResult, int maxResults) {
    log.infov("\nStorage.getGroupMembers > [ NOT IMPLEMENTED ] search for group members with params: realm={0} groupId={1} firstResult={2} maxResults={3}", realm.getId(), group.getId(), firstResult, maxResults);
    return Collections.emptyList();
  }

  @Override
  public List<UserModel> getGroupMembers(RealmModel realm, GroupModel group) {
    log.infov("\nStorage.getGroupMembers > [ NOT IMPLEMENTED ] search for group members: realm={0} groupId={1} firstResult={2} maxResults={3}", realm.getId(), group.getId());
    return Collections.emptyList();
  }

  @Override
  public List<UserModel> searchForUserByUserAttribute(String attrName, String attrValue, RealmModel realm) {
    log.infov("\nStorage.searchForUserByUserAttribute > [ NOT IMPLEMENTED ] search for group members: realm={0} attrName={1} attrValue={2}", realm.getId(), attrName, attrValue);
    return Collections.emptyList();
  }
}