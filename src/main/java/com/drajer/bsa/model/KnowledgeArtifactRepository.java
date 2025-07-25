package com.drajer.bsa.model;

import com.drajer.bsa.kar.model.KnowledgeArtifact;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * <h1>KnowledgeArtifactRepository</h1>
 *
 * An instance of this class is created for each FHIR Server that is hosting Knowledge Artifacts.
 *
 * @author nbashyam
 */
@Entity
@Table(name = "kar_repos")
@DynamicUpdate
@JsonInclude(Include.NON_NULL)
public class KnowledgeArtifactRepository {

  @Transient
  private final Logger logger = LoggerFactory.getLogger(KnowledgeArtifactRepository.class);

  /** The attribute represents the primary key for the table and is auto incremented. */
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer id;

  /** The attribute represents the FHIR Server URL which hosts the Knowledge Artifact. */
  @Column(name = "repo_fhir_url", nullable = false, columnDefinition = "TEXT")
  private String fhirServerURL;

  /**
   * The attribute represents the FHIR Server URL for the HealthcareSetting. This is unique for the
   * entire table.
   */
  @Column(name = "repo_name", nullable = false, unique = true)
  private String repoName;

  @Column(name = "repo_status", nullable = true)
  @Type(type = "org.hibernate.type.NumericBooleanType")
  private Boolean repoStatus;

  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  @Fetch(FetchMode.SELECT)
  @JoinColumn(name = "repo_id")
  private Set<KnowledgeArtifactSummaryInfo> karsInfo;

  public KnowledgeArtifactRepository() {
    karsInfo = new HashSet<>();
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getFhirServerURL() {
    return fhirServerURL;
  }

  public void setFhirServerURL(String fhirServerURL) {
    this.fhirServerURL = fhirServerURL;
  }

  public String getRepoName() {
    return repoName;
  }

  public void setRepoName(String repoName) {
    this.repoName = repoName;
  }

  public Set<KnowledgeArtifactSummaryInfo> getKarsInfo() {
    return karsInfo;
  }

  public void setKarsInfo(Set<KnowledgeArtifactSummaryInfo> karsInfo) {
    this.karsInfo = karsInfo;
  }

  public Boolean getRepoStatus() {
    return repoStatus;
  }

  public void setRepoStatus(Boolean repoStatus) {
    this.repoStatus = repoStatus;

    // All KARs should be not available.
    // karsInfo.forEach(kar -> kar.setKarAvailable(false));
  }

  public void addKar(KnowledgeArtifact kar) {

    KnowledgeArtifactSummaryInfo info =
        karsInfo.stream()
            .filter(art -> art.getVersionUniqueId().equals(kar.getVersionUniqueId()))
            .findAny()
            .orElse(null);

    if (info == null) {

      info = new KnowledgeArtifactSummaryInfo();
      info.setKarId(kar.getKarId());
      info.setKarName(kar.getKarName());
      info.setKarPublisher(kar.getKarPublisher());
      info.setKarVersion(kar.getKarVersion());
      info.setKarAvailable(true);
      karsInfo.add(info);

    } else {
      logger.info(" Not adding Kar {} as it already exists ", kar.getVersionUniqueId());
      // Make it available no matter the previous status
      karsInfo.remove(info);
      info.setKarAvailable(true);
      karsInfo.add(info);
    }
  }

  public void addKars(Set<KnowledgeArtifact> kars) {

    for (KnowledgeArtifact art : kars) {

      addKar(art);
    }
  }

  public void removeArtifactsNotAvailable() {

    if (karsInfo != null) {

      List<KnowledgeArtifactSummaryInfo> infos =
          karsInfo.stream()
              .filter(art -> art.getKarAvailable().equals(Boolean.FALSE))
              .collect(Collectors.toList());

      infos.forEach(info -> karsInfo.remove(info));
    }
  }
}
