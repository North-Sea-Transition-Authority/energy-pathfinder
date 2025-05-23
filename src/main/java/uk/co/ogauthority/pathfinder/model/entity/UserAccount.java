package uk.co.ogauthority.pathfinder.model.entity;

import com.google.common.base.Splitter;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PostLoad;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Formula;

@Entity
@Table(name = "user_accounts")
public class UserAccount implements Serializable {

  private static final long serialVersionUID = 1;

  @Id
  private String id;

  private String title;
  private String forename;
  private String surname;
  private String emailAddress;

  @Formula("user_system_privileges(id)")
  private String systemPrivilegesCsv;

  @Transient
  private Collection<String> systemPrivileges = Collections.emptySet();

  @PostLoad
  private void onLoad() {
    systemPrivileges = Splitter.on(",")
        .omitEmptyStrings()
        .trimResults()
        .splitToList(StringUtils.defaultString(systemPrivilegesCsv));
  }

  public UserAccount() {

  }

  public UserAccount(String id) {
    this.id = id;
  }

  public Collection<String> getSystemPrivileges() {
    return systemPrivileges;
  }

  public String getId() {
    return id;
  }

  public String getTitle() {
    return title;
  }

  public String getForename() {
    return forename;
  }

  public String getSurname() {
    return surname;
  }

  public String getEmailAddress() {
    return emailAddress;
  }

  public String getFullName() {
    return forename + " " + surname;
  }
}
