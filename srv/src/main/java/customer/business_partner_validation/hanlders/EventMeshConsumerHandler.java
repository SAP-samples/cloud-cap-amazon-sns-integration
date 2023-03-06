package customer.business_partner_validation.hanlders;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sap.cds.Result;
import com.sap.cds.ql.Select;
import com.sap.cds.ql.Update;
import com.sap.cds.ql.cqn.CqnSelect;
import com.sap.cds.ql.cqn.CqnUpdate;
import com.sap.cds.services.ServiceCatalog;
import com.sap.cds.services.cds.ApplicationService;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.messaging.TopicMessageEventContext;
import com.sap.cds.services.persistence.PersistenceService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cds.gen.businesspartnerqueryservice.Notifications;
import cds.gen.businesspartnerqueryservice.Notifications_;
import cds.gen.businesspartnervalidationservice.ValidateBusinessPartnerContext;
import customer.business_partner_validation.clients.em.EMPayload;

@Component
public class EventMeshConsumerHandler implements EventHandler{

    @Autowired
	PersistenceService db;
	
	private static Logger logger = LoggerFactory.getLogger(EventMeshConsumerHandler.class);

    @On(service = "messaging-em", event = { "sap/s4/beh/businesspartner/v1/BusinessPartner/Created/v1"})
    public void listenOnBPCreation(TopicMessageEventContext context){

        logger.info("S/4HANA Business Partner Creation Data Received From Event Mesh");
		String payload = context.getData();
		logger.info("Event Mesh Message Payload {}", payload);
		
		ServiceCatalog catalog = context.getServiceCatalog();
		ApplicationService businessPartnerValidationService = catalog.getService(ApplicationService.class, "BusinessPartnerValidationService");
		
		ValidateBusinessPartnerContext validateBPContext = ValidateBusinessPartnerContext.create();
		validateBPContext.setBp(payload);
		
		businessPartnerValidationService.emit(validateBPContext);
    }

    @On(service = "messaging-em", event = { "sap/s4/beh/businesspartner/v1/BusinessPartner/Changed/v1"})
    public void listenOnBPChanges(TopicMessageEventContext context) throws JsonMappingException, JsonProcessingException{

        logger.info("S/4HANA Business Partner Correction Data Received From Event Mesh");

        ObjectMapper mapper = new ObjectMapper();
		EMPayload emPayload = mapper.readValue(context.getData(), EMPayload.class);
        logger.info("Event Mesh Message Payload {}", emPayload.toString());

        String bpId = emPayload.getData().getBusinessPartner();
        CqnSelect selectQry = Select.from(Notifications_.class).where(bp -> bp.businessPartnerId().eq(bpId));
        if(db.run(selectQry).rowCount() == 0L){
            
            ServiceCatalog catalog = context.getServiceCatalog();
            ApplicationService businessPartnerValidationService = catalog.getService(ApplicationService.class, "BusinessPartnerValidationService");
            ValidateBusinessPartnerContext validateBPContext = ValidateBusinessPartnerContext.create();
            validateBPContext.setBp(context.getData());
            
            businessPartnerValidationService.emit(validateBPContext);
        }else{

            Notifications notification = db.run(selectQry).single(Notifications.class);
            if(notification.getVerificationStatusCode() != null && "V".equals(notification.getVerificationStatusCode())){
                
                Map<String, Object> updatedBPData = new HashMap<>();
                updatedBPData.put(notification.VERIFICATION_STATUS_CODE, "C");
                
                CqnUpdate updateQry = Update.entity(Notifications_.class).data(updatedBPData).where(n -> n.businessPartnerId().eq(bpId));
                db.run(updateQry);
                logger.info("Business Partner {} Validation Complete Success", bpId);
            }
        }
    }
}
