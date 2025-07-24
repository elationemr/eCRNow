package com.drajer.ecrapp.dao.impl;

import com.drajer.ecrapp.dao.AbstractDao;
import com.drajer.ecrapp.dao.EicrDao;
import com.drajer.ecrapp.model.Eicr;
import com.drajer.ecrapp.model.ReportabilityResponse;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class EicrDaoImpl extends AbstractDao implements EicrDao {

  private static final Logger logger = LoggerFactory.getLogger(EicrDaoImpl.class);
  public static final String FHIR_SERVER_URL = "fhirServerUrl";
  public static final String ENCOUNTER_ID = "encounterId";
  public static final String EICR_DOC_ID = "eicrDocId";
  public static final String RESPONSE_DOC_ID = "responseDocId";
  public static final String SET_ID = "setId";
  public static final String X_REQUEST_ID = "xRequestId";

  public Eicr saveOrUpdate(Eicr eicr) {
    getSession().saveOrUpdate(eicr);
    return eicr;
  }

  public Eicr getEicrById(Integer id) {
    return getSession().get(Eicr.class, id);
  }

  public ReportabilityResponse saveOrUpdate(ReportabilityResponse rr) {
    getSession().saveOrUpdate(rr);
    return rr;
  }

  public ReportabilityResponse getRRById(Integer id) {
    return getSession().get(ReportabilityResponse.class, id);
  }

  public Integer getMaxVersionId(Eicr eicr) {
    Criteria criteria = getSession().createCriteria(Eicr.class);
    criteria.add(Restrictions.eq(FHIR_SERVER_URL, eicr.getFhirServerUrl()));
    criteria.add(Restrictions.eq("launchPatientId", eicr.getLaunchPatientId()));
    criteria.add(Restrictions.eq(ENCOUNTER_ID, eicr.getEncounterId()));
    criteria.addOrder(Order.desc("docVersion"));
    criteria.setMaxResults(1);

    Eicr resultEicr = (Eicr) criteria.uniqueResult();

    if (resultEicr != null) {
      return resultEicr.getDocVersion();
    }
    return 0;
  }

  public Eicr getEicrByCorrelationId(String xcoorrId) {
    Criteria criteria = getSession().createCriteria(Eicr.class);
    criteria.add(Restrictions.eq("xCorrelationId", xcoorrId));

    return (Eicr) criteria.uniqueResult();
  }

  public List<Eicr> getEicrData(Map<String, String> searchParams) {
    Criteria criteria = getSession().createCriteria(Eicr.class);
    if (searchParams.get("eicrId") != null) {
      criteria.add(Restrictions.eq("id", Integer.parseInt(searchParams.get("eicrId"))));
    }
    prepareCriteria(criteria, searchParams);
    return criteria.addOrder(Order.desc("id")).list();
  }

  public List<Eicr> getRRData(Map<String, String> searchParams) {
    Criteria criteria = getSession().createCriteria(Eicr.class);
    if (searchParams.get(RESPONSE_DOC_ID) != null) {
      criteria.add(Restrictions.eq(RESPONSE_DOC_ID, searchParams.get(RESPONSE_DOC_ID)));
    }
    prepareCriteria(criteria, searchParams);
    return criteria.addOrder(Order.desc("id")).list();
  }

  public List<Eicr> getEicrAndRRByXRequestId(String xRequestId) {
    Criteria criteria = getSession().createCriteria(Eicr.class);
    criteria.add(Restrictions.eq(X_REQUEST_ID, xRequestId));
    return criteria.addOrder(Order.desc("id")).list();
  }

  @Override
  public Eicr getEicrByDocId(String docId) {
    Criteria criteria = getSession().createCriteria(Eicr.class);
    criteria.add(Restrictions.eq(EICR_DOC_ID, docId));

    return (Eicr) criteria.uniqueResult();
  }

  public static void prepareCriteria(Criteria criteria, Map<String, String> searchParams) {

    if (searchParams.get(EICR_DOC_ID) != null) {
      criteria.add(Restrictions.eq(EICR_DOC_ID, searchParams.get(EICR_DOC_ID)));
    }
    if (searchParams.get(FHIR_SERVER_URL) != null) {
      criteria.add(Restrictions.eq(FHIR_SERVER_URL, searchParams.get(FHIR_SERVER_URL)));
    }
    if (searchParams.get(SET_ID) != null) {
      criteria.add(Restrictions.eq(SET_ID, searchParams.get(SET_ID)));
    }
    if (searchParams.get("patientId") != null) {
      criteria.add(Restrictions.eq("launchPatientId", searchParams.get("patientId")));
    }
    if (searchParams.get(ENCOUNTER_ID) != null) {
      criteria.add(Restrictions.eq(ENCOUNTER_ID, searchParams.get(ENCOUNTER_ID)));
    }
    if (searchParams.get("version") != null) {
      criteria.add(Restrictions.eq("docVersion", Integer.parseInt(searchParams.get("version"))));
    }
    if (searchParams.get(X_REQUEST_ID) != null) {
      criteria.add(Restrictions.eq(X_REQUEST_ID, searchParams.get(X_REQUEST_ID)));
    }

    String startDate = searchParams.get("startDate");
    String endDate = searchParams.get("endDate");
    Date eicrStartDate = null;
    Date eicrEndDate = null;
    try {
      eicrStartDate = new SimpleDateFormat("yyyy-MM-dd").parse(startDate);
      eicrEndDate = new SimpleDateFormat("yyyy-MM-dd").parse(endDate);
    } catch (Exception e) {
      logger.error("Exception while converting into date format", e);
    }

    if (eicrStartDate != null) {
      criteria.add(Restrictions.ge("lastUpdated", eicrStartDate));
    }
    if (eicrEndDate != null) {
      criteria.add(Restrictions.le("lastUpdated", eicrEndDate));
    }
  }

  public void deleteEicr(Eicr eicr) {
    getSession().delete(eicr);
  }
}
