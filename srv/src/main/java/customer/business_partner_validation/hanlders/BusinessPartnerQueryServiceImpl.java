package customer.business_partner_validation.hanlders;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.cds.services.cds.CqnService;

import java.util.HashMap;
import java.util.Map;

import com.sap.cds.Result;
import com.sap.cds.ql.Select;
import com.sap.cds.ql.Update;
import com.sap.cds.ql.cqn.CqnSelect;
import com.sap.cds.ql.cqn.CqnUpdate;
import com.sap.cds.services.cds.CdsUpdateEventContext;
import com.sap.cds.services.draft.DraftNewEventContext;
import com.sap.cds.services.draft.DraftPatchEventContext;
import com.sap.cds.services.draft.DraftSaveEventContext;
import com.sap.cds.services.draft.DraftService;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.After;
import com.sap.cds.services.handler.annotations.Before;
import com.sap.cds.services.handler.annotations.ServiceName;
import com.sap.cds.services.persistence.PersistenceService;
import com.sap.cloud.sdk.datamodel.odata.client.exception.ODataException;

import customer.business_partner_validation.exceptions.InvalidBPVerificationUpdateException;
import customer.business_partner_validation.exceptions.RemoteServiceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import cds.gen.api_business_partner.ABusinessPartner;
import cds.gen.api_business_partner.ABusinessPartnerAddress;
import cds.gen.api_business_partner.ABusinessPartnerAddress_;
import cds.gen.api_business_partner.ABusinessPartner_;
import cds.gen.api_business_partner.ApiBusinessPartner_;
import cds.gen.businesspartnerqueryservice.Addresses;
import cds.gen.businesspartnerqueryservice.Addresses_;
import cds.gen.businesspartnerqueryservice.DraftActivateContext;
import cds.gen.businesspartnerqueryservice.Notifications;
import cds.gen.businesspartnerqueryservice.Notifications_;

@Component
@ServiceName("BusinessPartnerQueryService")
public class BusinessPartnerQueryServiceImpl implements EventHandler{
    
    @Autowired
	PersistenceService db;

	@Autowired
	@Qualifier(ApiBusinessPartner_.CDS_NAME)
	CqnService bupa;

    private Map<String, String> verficationStatueValueHelp;

    private static final Logger logger = LoggerFactory.getLogger(BusinessPartnerQueryServiceImpl.class);

    public BusinessPartnerQueryServiceImpl(){

        this.verficationStatueValueHelp = new HashMap<String, String>();
        verficationStatueValueHelp.put("V", "VERIFIED");
        verficationStatueValueHelp.put("INV", "INVALID");
        verficationStatueValueHelp.put("P", "IN PROCESS");
        verficationStatueValueHelp.put("N", "NEW");
    }

    @Before(event = DraftService.EVENT_DRAFT_PATCH, entity = Addresses_.CDS_NAME)
    public void beforePatchAddress(DraftPatchEventContext context, Addresses address){

        address.setIsModified(true);
    }

    @Before(event = CqnService.EVENT_UPDATE, entity = Notifications_.CDS_NAME)
    public void beforeUpdateBusinessPartnerVerificationCode(CdsUpdateEventContext context, Notifications notification){
        
        logger.info("State Business Partner Verification Code Check");
        String verificationCode = notification.getVerificationStatusCode().toString();
        if(verificationCode != null && !verificationCode.isEmpty() && "C".equals(verificationCode)){
            logger.error("Business Partner Verification Code Check Failed!");
            logger.error("Failed Reason: Could Not Update Verification Code To C Directly");
            throw new InvalidBPVerificationUpdateException("Cannot Mark As COMPLETED. Please Verify the Buiness Partner Data And Change To VERIFIED First.");
        }
        logger.info("State Business Partner Verification Code Check Complete");
    }

    @After(event = CqnService.EVENT_UPDATE, entity = Notifications_.CDS_NAME)
    public void afterUpdateBusinessPartnerVerificationCode(CdsUpdateEventContext context, Notifications notification){

        logger.info("Start Release Business Partner {} Record in S/4HANA");
        String verificationCode = notification.getVerificationStatusCode().toString();

        if(verificationCode != null && !verificationCode.isEmpty() && ("V".equals(verificationCode) || "INV".equals(verificationCode))){
        	
        	updateBusinessPartnerDataToS4HANA(notification);
        }

        logger.info("Release Business Partner {} Record in S/4HANA Complete");
    }
    
    private void updateBusinessPartnerDataToS4HANA(Notifications notification){

        String verificationCode = notification.getVerificationStatusCode().toString();

        CqnSelect select = Select.from(Notifications_.class).where(n -> n.ID().eq(notification.getId()));
        Notifications notifyResult = db.run(select).single(Notifications.class);
        String bpId = notifyResult.getBusinessPartnerId();

		select = Select.from(Addresses_.class).where(a -> a.businessPartnerId().eq(notifyResult.getBusinessPartnerId()));
		Result bpAddressResult = db.run(select);
		if (bpAddressResult.rowCount() != 0L) {
			Addresses addressResult = db.run(select).single(Addresses.class);
			String bpAddressId = addressResult.getAddressId();

			if ("V".equals(verificationCode) && addressResult.getIsModified()) {

				Map<String, Object> updatedBPAddressData = new HashMap<>();
				updatedBPAddressData.put(ABusinessPartnerAddress.STREET_NAME, addressResult.getStreetName());
				updatedBPAddressData.put(ABusinessPartnerAddress.POSTAL_CODE, addressResult.getPostalCode());

				CqnUpdate updateQry = Update.entity(ABusinessPartnerAddress_.class).data(updatedBPAddressData)
						.where(a -> a.BusinessPartner().eq(bpId).and(a.AddressID().eq(bpAddressId)));
				try {
					logger.info("Start Update Business Partner {} Address Data Into S/4HANA", bpId);
					bupa.run(updateQry);
				} catch (ODataException e) {
					logger.error("Exception Happens While Updaing Business Partner {} Address Data In S/4HANA", bpId);
					logger.error(e.getMessage());
					throw new RemoteServiceException(
							"Exception Happens on S/4HANA Remote Services While Updating Business Partner Address");
				}
				logger.info("Updating Business Partner {} Address Data Into S/4HANA Complete", bpId);
			}
		}

        Map<String, Object> updatedBPData = new HashMap<>();
        updatedBPData.put(ABusinessPartner.SEARCH_TERM1, verficationStatueValueHelp.get(verificationCode));
        updatedBPData.put(ABusinessPartner.BUSINESS_PARTNER_IS_BLOCKED, "V".equals(verificationCode) ? false:true);

        CqnUpdate updateQry = Update.entity(ABusinessPartner_.class).data(updatedBPData).where(bp -> bp.BusinessPartner().eq(bpId));
        try{
            logger.info("Start Update Business Partner {} Record Into S/4HANA System", bpId);
            bupa.run(updateQry);
        }catch(ODataException e){
            logger.error("Exception Happens While Updaing Business Partner {} Data In S/4HANA", bpId);
            logger.error(e.getMessage());
            throw new RemoteServiceException("Exception Happens on S/4HANA Remote Services While Updating Business Partner Record");
        }catch(Exception e){
            logger.error("Exception Happens While Updaing Business Partner {} Data In S/4HANA", bpId);
            logger.error(e.getMessage());
            throw new RemoteServiceException("Exception Happens on S/4HANA Remote Services While Updating Business Partner Record");
        }
        logger.info("Updating Business Partner {} Data Into S/4HANA Complete", bpId);
    }
    
}
