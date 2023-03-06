package customer.business_partner_validation.hanlders;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import cds.gen.api_business_partner.ABusinessPartner;
import cds.gen.api_business_partner.ABusinessPartnerAddress;
import cds.gen.api_business_partner.ABusinessPartnerAddress_;
import cds.gen.api_business_partner.ABusinessPartner_;
import cds.gen.api_business_partner.ApiBusinessPartner_;
import cds.gen.businesspartnervalidationservice.ValidateBusinessPartnerContext;
import cds.gen.businesspartnervalidationservice.ValidateBusinessPartnerResponse;
import cds.gen.sap.capire.businesspartnervalidation.Addresses;
import cds.gen.sap.capire.businesspartnervalidation.Addresses_;
import cds.gen.sap.capire.businesspartnervalidation.Notifications;
import cds.gen.sap.capire.businesspartnervalidation.Notifications_;
import customer.business_partner_validation.clients.em.EMPayload;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;
import software.amazon.awssdk.services.sns.model.SnsException;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sap.cds.Result;
import com.sap.cds.ql.Insert;
import com.sap.cds.ql.Select;
import com.sap.cds.ql.Update;
import com.sap.cds.ql.cqn.CqnInsert;
import com.sap.cds.ql.cqn.CqnSelect;
import com.sap.cds.ql.cqn.CqnUpdate;
import com.sap.cds.services.cds.CqnService;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;
import com.sap.cds.services.persistence.PersistenceService;
import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.cloudplatform.connectivity.DestinationAccessor;
import com.sap.cloud.sdk.datamodel.odata.client.exception.ODataException;
import com.sap.cloud.sdk.s4hana.connectivity.DefaultErpHttpDestination;
import com.sap.cloud.sdk.s4hana.connectivity.ErpHttpDestination;
import com.sap.cloud.sdk.s4hana.onpremise.datamodel.odata.namespaces.businesspartner.BusinessPartner;
import com.sap.cloud.sdk.s4hana.onpremise.datamodel.odata.namespaces.businesspartner.BusinessPartnerAddress;
import com.sap.cloud.sdk.s4hana.onpremise.datamodel.odata.namespaces.businesspartner.BusinessPartnerAddressByKeyFluentHelper;
import com.sap.cloud.sdk.s4hana.onpremise.datamodel.odata.services.BusinessPartnerService;
import com.sap.cloud.sdk.s4hana.onpremise.datamodel.odata.services.DefaultBusinessPartnerService;

@Component
@ServiceName("BusinessPartnerValidationService")
public class BusinessPartnerValidationServiceImpl implements EventHandler {

	@Value("${amazonProperties.sns.topic.arn.bpValidation}")
	private String bpValidationAWSTopic;

	@Value("${amazonProperties.region}")
	private String bpValidationTopicRegion;

	@Autowired
	PersistenceService db;

	@Autowired
	@Qualifier(ApiBusinessPartner_.CDS_NAME)
	CqnService bupa;

	private static final Logger logger = LoggerFactory.getLogger(BusinessPartnerValidationServiceImpl.class);

	@On(event = "validateBusinessPartner")
	public void onValidateBusinessPartner(ValidateBusinessPartnerContext context)
			throws JsonMappingException, JsonProcessingException {

		ValidateBusinessPartnerResponse resp = ValidateBusinessPartnerResponse.create();

		// Step 1. Receive Business Partner Data From Event Mesh Handler
		String payload = context.getBp();
		logger.info("Event Mesh Payload Received in Bussiness Partner Validation Service {} ", payload);
		ObjectMapper mapper = new ObjectMapper();
		EMPayload emPayload = mapper.readValue(payload, EMPayload.class);

		// Step 2. Pull Complete Business Partner Data From S/4HANA
		logger.info("Start Fetching Business Partner Data From S/4HANA");
        String bpId = emPayload.getData().getBusinessPartner();
		ABusinessPartner bp = null;
		try {
			/*
			 * final Destination destination =
			 * DestinationAccessor.getDestination("S4HANA_DEST"); final ErpHttpDestination
			 * httpDestination =
			 * destination.asHttp().decorate(DefaultErpHttpDestination::new);
			 * BusinessPartnerService service = new DefaultBusinessPartnerService();
			 * businessPartner =
			 * service.getBusinessPartnerByKey(emPayload.getData().getBusinessPartner())
			 * .select(BusinessPartner.BUSINESS_PARTNER,
			 * BusinessPartner.BUSINESS_PARTNER_NAME,
			 * BusinessPartner.TO_BUSINESS_PARTNER_ADDRESS).executeRequest(httpDestination);
			 */
			CqnSelect select = Select.from(ABusinessPartner_.class).where(a -> a.BusinessPartner().eq(bpId));
			bp = bupa.run(select).single(ABusinessPartner.class);
		} catch (ODataException e) {
			logger.error("Exception Happens While Query S/4HANA System For Business Partner {} Data", bpId);
			logger.error(e.getMessage());
			return;
		}
        logger.info("Fetch Business Partner {} Data From S/4HANA Success!", bpId);

		logger.info("Start Fetching Business Partner Address Data From S/4HANA");
		ABusinessPartnerAddress bpAddress = null;
		try {

			CqnSelect select = Select.from(ABusinessPartnerAddress_.class)
					.where(a -> a.BusinessPartner().eq(emPayload.getData().getBusinessPartner()));
			Result bpAddressResult = bupa.run(select);
            if(bpAddressResult.rowCount() != 0L){
                bpAddress = bpAddressResult.single(ABusinessPartnerAddress.class);
            }
		} catch (ODataException e) {
			logger.error("Exception Happens While Query S/4HANA System For Business Partner {} Address Data",
					emPayload.getData().getBusinessPartner());
			logger.error(e.getMessage());
			return;
		}
		logger.info("Fetch Business Partner {} Address Data From S/4HANA Success!", bpId);

		// Step 3. Store Business Partner Data Into SAP HANA Cloud DB
		switch (emPayload.getType()) {
		case "sap.s4.beh.businesspartner.v1.BusinessPartner.Changed.v1":
            persistBPChangeEventData(bpAddress, bp, bpId);
			break;

		case "sap.s4.beh.businesspartner.v1.BusinessPartner.Created.v1":
            persistBPCreationEventData(bpAddress, bp, bpId);
			break;

		default:
			break;
		}

		// Step 4. Send Email Notification To Validator Through AWS SNS
		PublishResponse publishResult = null;
		try {

			String subject = "New S/4HANA Business Partner Validation Notification";
			String bpvalidationURL = System.getenv("BP_VALIDATION_UI_URL");
			StringBuilder message = new StringBuilder();
			message.append("Business Partner " + bpId + " Needs Validation. Please Validate ASAP");
			message.append("\n");
			message.append("Perform Validation By Click: " + bpvalidationURL);

			SnsClient client = getAwsSnsClient();
			PublishRequest request = PublishRequest.builder()
					.topicArn(bpValidationAWSTopic)
					.subject(subject)
					.message(message.toString())
					.build();
			publishResult = client.publish(request);

		} catch (SnsException e) {

			resp.setValidationResponse("500");
			resp.setValidationMsg("Business Partner " + bpId + " Validation Completed, But Message Publish Failed");
			context.setResult(resp);
			context.setCompleted();
		}

		resp.setValidationResponse("200");
		resp.setValidationMsg(
				"Business Partner " + bpId + " Validation Completed.");
		context.setResult(resp);
		context.setCompleted();
	}

	private SnsClient getAwsSnsClient() {

		String accessKey = System.getenv("IAM_USER_ACCESS_KEY").toString();
        String secretKey = System.getenv("IAM_USER_SECRET_ACCESS_KEY").toString();
		Region region = Region.of(bpValidationTopicRegion);
		AwsBasicCredentials awsCreds = AwsBasicCredentials.create(accessKey, secretKey);
		SnsClient snsClient = SnsClient.builder().credentialsProvider(StaticCredentialsProvider.create(awsCreds)).region(region).build();

		return snsClient;
	}

	private void persistBPCreationEventData(ABusinessPartnerAddress bpAddress, ABusinessPartner bp, String bpId) {

		// Message Come From Business Partner Create Topic
		Map<String, Object> bpMainEntryMap = new HashMap<>();
		bpMainEntryMap.put(Notifications.BUSINESS_PARTNER_ID, bp.getBusinessPartner());
		bpMainEntryMap.put(Notifications.BUSINESS_PARTNER_NAME, bp.getBusinessPartnerName());
		bpMainEntryMap.put(Notifications.VERIFICATION_STATUS_CODE, "N");

		CqnInsert insert = Insert.into(Notifications_.CDS_NAME).entry(bpMainEntryMap);
		Result insertRes = db.run(insert);

		if (bpAddress != null) {

			Notifications notification = null;
			CqnSelect select = Select.from(Notifications_.CDS_NAME)
					.where(c -> c.get(Notifications.BUSINESS_PARTNER_ID).eq(bpId));
			notification = db.run(select).single(Notifications.class);

			Map<String, Object> bpAddressEntryMap = new HashMap<>();
			bpAddressEntryMap.put(Addresses.ADDRESS_ID, bpAddress.getAddressID());
			bpAddressEntryMap.put(Addresses.COUNTRY, bpAddress.getCountry());
			bpAddressEntryMap.put(Addresses.CITY_NAME, bpAddress.getCityName());
			bpAddressEntryMap.put(Addresses.STREET_NAME, bpAddress.getStreetName());
			bpAddressEntryMap.put(Addresses.POSTAL_CODE, bpAddress.getPostalCode());
			bpAddressEntryMap.put(Addresses.BUSINESS_PARTNER_ID, bpAddress.getBusinessPartner());
			bpAddressEntryMap.put(Addresses.NOTIFICATIONS_ID, notification.getId());

			CqnInsert bpAddressInsert = Insert.into(Addresses_.CDS_NAME).entry(bpAddressEntryMap);
			Result BpAddressInsertRes = db.run(bpAddressInsert);
			logger.info("Business Partner {} Address Data Inserted", bpId);
		}
	}

	private void persistBPChangeEventData(ABusinessPartnerAddress bpAddress, ABusinessPartner bp, String bpId) {

		// Message Come From Business Partner Change Topic
		logger.info("Update Business Partner Data");

		CqnSelect bpMainSelect = Select.from(Notifications_.CDS_NAME)
				.where(a -> a.get(Notifications.BUSINESS_PARTNER_ID).eq(bpId));
		CqnSelect bpAddressSelect = Select.from(Addresses_.CDS_NAME)
				.where(a -> a.get(Addresses.BUSINESS_PARTNER_ID).eq(bpId));

		Result notificationSelectResult = db.run(bpMainSelect);
		if (notificationSelectResult.rowCount() == 0) {

			logger.info("Business Partner {} Data Not Exist In DB, Insert Into DB Start", bpId);

			// Message Come From Business Partner Create Topic
			Map<String, Object> bpMainEntryMap = new HashMap<>();
			bpMainEntryMap.put(Notifications.BUSINESS_PARTNER_ID, bp.getBusinessPartner());
			bpMainEntryMap.put(Notifications.BUSINESS_PARTNER_NAME, bp.getBusinessPartnerName());
			bpMainEntryMap.put(Notifications.VERIFICATION_STATUS_CODE, "N");

			CqnInsert insert = Insert.into(Notifications_.CDS_NAME).entry(bpMainEntryMap);
			Result insertRes = db.run(insert);
			logger.info("Business Partner {} Data Inserted Into Notification Table", bpId);

			if (bpAddress != null) {

				Notifications notification = null;
				CqnSelect select = Select.from(Notifications_.CDS_NAME)
						.where(c -> c.get(Notifications.BUSINESS_PARTNER_ID).eq(bpId));
				notification = db.run(select).single(Notifications.class);

				Map<String, Object> bpAddressEntryMap = new HashMap<>();
				bpAddressEntryMap.put(Addresses.ADDRESS_ID, bpAddress.getAddressID());
				bpAddressEntryMap.put(Addresses.COUNTRY, bpAddress.getCountry());
				bpAddressEntryMap.put(Addresses.CITY_NAME, bpAddress.getCityName());
				bpAddressEntryMap.put(Addresses.STREET_NAME, bpAddress.getStreetName());
				bpAddressEntryMap.put(Addresses.POSTAL_CODE, bpAddress.getPostalCode());
				bpAddressEntryMap.put(Addresses.BUSINESS_PARTNER_ID, bpAddress.getBusinessPartner());
				bpAddressEntryMap.put(Addresses.NOTIFICATIONS_ID, notification.getId());

				CqnInsert bpAddressInsert = Insert.into(Addresses_.CDS_NAME).entry(bpAddressEntryMap);
				Result BpAddressInsertRes = db.run(bpAddressInsert);
				logger.info("Business Partner {} Address Data Inserted Into Address Table", bpId);
			}

		} else {

			Notifications notification = db.run(bpMainSelect).single(Notifications.class);
			Addresses address = db.run(bpAddressSelect).single(Addresses.class);

			Map<String, Object> bpMainEntryMap = new HashMap<>();
			bpMainEntryMap.put(Notifications.BUSINESS_PARTNER_ID, bp.getBusinessPartner());
			bpMainEntryMap.put(Notifications.BUSINESS_PARTNER_NAME, bp.getBusinessPartnerName());
			bpMainEntryMap.put(Notifications.VERIFICATION_STATUS_CODE, "N");

			CqnUpdate update = Update.entity(Notifications_.CDS_NAME).data(bpMainEntryMap);
			db.run(update);
			logger.info("Business Partner {} Data Updated", bpId);

			if (address != null) {

				Map<String, Object> bpAddressEntryMap = new HashMap<>();
				bpAddressEntryMap.put(Addresses.ADDRESS_ID, bpAddress.getAddressID());
				bpAddressEntryMap.put(Addresses.COUNTRY, bpAddress.getCountry());
				bpAddressEntryMap.put(Addresses.CITY_NAME, bpAddress.getCityName());
				bpAddressEntryMap.put(Addresses.STREET_NAME, bpAddress.getStreetName());
				bpAddressEntryMap.put(Addresses.POSTAL_CODE, bpAddress.getPostalCode());
				bpAddressEntryMap.put(Addresses.BUSINESS_PARTNER_ID, bpAddress.getBusinessPartner());
				bpAddressEntryMap.put(Addresses.NOTIFICATIONS_ID, notification.getId());

				CqnUpdate addressUpdate = Update.entity(Addresses_.CDS_NAME).data(bpAddressEntryMap);
				db.run(update);
				logger.info("Business Partner Address {} Data Updated", bpId);
			}
		}
	}
}
