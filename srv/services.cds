using sap.capire.businessPartnerValidation as db from '../db/schema';
using API_BUSINESS_PARTNER as BUPA_API from './external/OP_API_BUSINESS_PARTNER_SRV';

service BusinessPartnerQueryService @(requires:'authenticated-user'){
    
    @odata.draft.enabled
    entity Notifications  @(restrict: [
         { grant: ['READ', 'UPDATE'], to: 'validator' },
         { grant: ['*'], to: 'admin'}
    ]) as projection on db.Notifications;
    
    entity Addresses @(restrict: [
         { grant: ['READ', 'UPDATE'], to: 'validator' },
         { grant: ['*'], to: 'admin'}
    ]) as projection on db.Addresses;

    @readonly entity BusinessPartnerAddress as projection on BUPA_API.A_BusinessPartnerAddress{
        
        key BusinessPartner as businessPartnerId,
        AddressID as addressId,
        Country as country,
        CityName as cityName ,
        StreetName as streetName,
        PostalCode as postalCode
  };

    @readonly entity BusinessPartner as projection on BUPA_API.A_BusinessPartner{
        
        key BusinessPartner as businessPartnerId,
        BusinessPartnerFullName as businessPartnerName,
        SearchTerm1 as searchTerm1,
        BusinessPartnerIsBlocked as businessPartnerIsBlocked
  };

}

service BusinessPartnerValidationService{

	type ValidateBusinessPartnerResponse{
		
		validationResponse: String;
		validationMsg: 		String;
	}

	action validateBusinessPartner(bp: String not null) returns ValidateBusinessPartnerResponse;
}