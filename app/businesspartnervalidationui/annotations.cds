using BusinessPartnerQueryService as BusinessPartnerQueryService from '../../srv/services';

annotate BusinessPartnerQueryService.Addresses with @(

    UI: {   
        HeaderInfo: {
            TypeName: 'Address',
            TypeNamePlural: 'Addresses',
            Title: { $Type: 'UI.DataField', Value: addressId }
        },
        SelectionFields: [],
        LineItem: [
            {$Type: 'UI.DataField', Value: addressId, ![@HTML5.CssDefaults] : {width : '16.66%'}},
            {$Type: 'UI.DataField', Value: businessPartnerId, ![@HTML5.CssDefaults] : {width : '16.66%'}},
            {$Type: 'UI.DataField', Value: streetName,![@HTML5.CssDefaults] : {width : '16.66%'}},
            {$Type: 'UI.DataField', Value: cityName,![@HTML5.CssDefaults] : {width : '16.66%'}},
            {$Type: 'UI.DataField', Value: country,![@HTML5.CssDefaults] : {width : '16.66%'}},
            {$Type: 'UI.DataField', Value: postalCode,![@HTML5.CssDefaults] : {width : '16.66%'}}
        ],
        HeaderFacets: [
            {$Type: 'UI.ReferenceFacet', ID: 'BP', Target: '@UI.DataPoint#BP'},
            {$Type: 'UI.ReferenceFacet', ID: 'Status', Target: '@UI.DataPoint#Status'}
        ],
        DataPoint#BP: {Value: businessPartnerId, Title: 'Business Partner ID'},
        DataPoint#Status: {Value: 'notification.verificationStatus', Title: 'Verification Status'}
    }
);

annotate BusinessPartnerQueryService.Addresses with {
    addressId @( Common.Label : 'Address ID' ) @readonly;
    businessPartnerId @( Common.Label : 'Business Partner ID' ) @readonly;
    streetName @( Common.Label : 'Street Name' );
    cityName @( Common.Label : 'City Name' ) @readonly;
    country @( Common.Label : 'Country' ) @readonly;
    postalCode @( Common.Label : 'Postal Code' );
}

annotate BusinessPartnerQueryService.Notifications with {

    businessPartnerId @( Common.Label : 'Business Partner ID' );
    businessPartnerName @( Common.Label : 'Business Partner Name' ) @readonly;
    verificationStatus @( Common.Label : 'Verification Status' );
}
annotate BusinessPartnerQueryService.Notifications with @(

    UI: {
        UpdateHidden: verificationStatus.updateCode,
        HeaderInfo: {
            TypeName: '{i18n>Notification}',
            TypeNamePlural: '{i18n>Notifications}',
            Title: { $Type: 'UI.DataField', Value: businessPartnerId }
        },
        SelectionFields: [businessPartnerId, businessPartnerName,verificationStatus_code],
        LineItem: [
          {$Type: 'UI.DataField', Value: businessPartnerId, ![@HTML5.CssDefaults] : {width : '33.33%'}},
          {$Type: 'UI.DataField', Value: businessPartnerName, ![@HTML5.CssDefaults] : {width : '33.33%'}},
          {$Type: 'UI.DataField', Value: verificationStatus.value, Criticality: verificationStatus.criticality,![@HTML5.CssDefaults] : {width : '33.33%'}}
        ],
        HeaderFacets: [
             {$Type: 'UI.ReferenceFacet', ID: 'HeaderBpStatus', Target: '@UI.DataPoint#BpName'},
             {$Type: 'UI.ReferenceFacet', ID: 'HeaderBpStatusCode', Target: '@UI.FieldGroup#Detail'}
        ],
        Facets: [
            {$Type: 'UI.ReferenceFacet', Target: 'addresses/@UI.LineItem', Label: 'Address Facet'},
        ],
        DataPoint#BpName: {Value: businessPartnerName, Title: 'Business Partner Name'},
        FieldGroup#Detail  : {
            $Type : 'UI.FieldGroupType',
            Data: [
                {$Type: 'UI.DataField', Value: verificationStatus_code}
            ] 
        }
    }
);

annotate BusinessPartnerQueryService.Notifications @(
    Capabilities: {
        Insertable : false,
        Deletable : true,
        Updatable : true,
});

annotate BusinessPartnerQueryService.Addresses @(
    Capabilities: {
        Deletable : false,
        Insertable : false,
});

annotate BusinessPartnerQueryService.Notifications with {
    verificationStatus @(
        Common: {
            ValueList: {
                Label: 'StatusValues',
                CollectionPath : 'StatusValues',
                Parameters: [
                    {
                        $Type: 'Common.ValueListParameterInOut',
                        LocalDataProperty: verificationStatus_code,
                        ValueListProperty:'code'
                    },
                    {
                        $Type: 'Common.ValueListParameterDisplayOnly',
                        ValueListProperty: 'value'
                    }
                ]
            },
            ValueListWithFixedValues,
            FieldControl: #Mandatory
    }
    );
};

annotate BusinessPartnerQueryService.StatusValues with {
   code @Common : {
        Text            : value,
        TextArrangement : #TextOnly
    } @title :  'Code';
    value @title: 'Verification Status';
};