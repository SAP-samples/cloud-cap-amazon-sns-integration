# S/4HANA Business Partner Validation Application Walk Through

In this section, I will show you how could the validator perform the their work on this business partner validation application deployed on the SAP BTP platform, instance of login to the original S/4HANA On-Premise system to do the validation.

> ### **Prerequis**
> - Please assign **S/4HANA-Business-Partner-App-Validator** role collection to yourself on the SAP BTP in-order to perform validator action.
> - Please assign **S/4HANA-Business-Partner-App-Admin** role collection to yourself on the SAP BTP in-order to perform validator action.
>   ![b7c1f74e-bd68-4c47-832e-3af583fa88f8](https://github.wdf.sap.corp/storage/user/105079/files/6a6ff92d-e5f6-41c8-9668-49dd6e363526)
>  
> - User who have the **S/4HANA-Business-Partner-App-Validator** role have the **permission** to **update business partner address**, and **update the business partner verification status code** in the App.
> - User who have the **S/4HANA-Business-Partner-App-Admin** role have the **full permission (READ, WRITE, DELETE)** in the App



1. First of all, let's create a new business partner in the S/4HANA On-Premise system, and **note down** the newly created business partner ID. (**Business Partner ID: 1000634**)
    
    > ### **Note**
    > - Remember to mark the newly created business partner Status as **Central Block**

    ![Capture](https://github.wdf.sap.corp/storage/user/105079/files/c6d2ce75-41b1-4b5f-af77-edb9c5344d87)
    
    ![tempsnip](https://github.wdf.sap.corp/storage/user/105079/files/dad5a8ce-33af-4105-b161-ebee524bbca3)

2. This time the your email address subscribe on the S4HANA-Business-Partner-Validation topic would receive an email notification as below.

    ![Capture](https://github.wdf.sap.corp/storage/user/105079/files/a1f79bc8-2e62-4a59-9f8c-9041f91395ab)

3. Click on the link contains in the email notification, it will bring you directly into the Business Partner Validation Fiori UI. 

    ![tempsnip](https://github.wdf.sap.corp/storage/user/105079/files/9edffa0c-4d46-43d7-9e6c-be06c0a5cfb6)
    
4. In the S/4HANA Business Partner Validation Fiori UI, click the **Go** button and then you could see the newly created business partner (ID **1000634**) showing up there with the **Verification Status** as **NEW**

    ![tempsnip](https://github.wdf.sap.corp/storage/user/105079/files/7ce8b726-581a-44af-851d-6193e0bea321)

5. Select the record of the newly created business partner and then we could go deeper and see the address information of this business partner.

    ![Capture](https://github.wdf.sap.corp/storage/user/105079/files/5b0d8f9f-d602-4c9e-b1b0-5f17279266ba)

6. Click the edit button to perform the validation. We could make changes on the Street Name and Postcal Code field if necessary, and update the Verification Status code depends on the situation.

    ![Capture](https://github.wdf.sap.corp/storage/user/105079/files/c758c135-1e19-48b4-aa8e-018fd6cea806)
    
7. If everything looks good, then we could update the verification status code to VERIFIED and this will acutally release the business partner in the S/4HANA On-Premise system. 
    
    ![Capture](https://github.wdf.sap.corp/storage/user/105079/files/f3eb3ea6-192d-4886-acac-b54d4c376704)
    
8. Now lets go back to the S/4HANA On-Premise system and search for the business partner. 

    ![tempsnip](https://github.wdf.sap.corp/storage/user/105079/files/d605b0cb-f1b8-4b0f-b14c-eb54b114cf8f)
    
    ![tempsnip](https://github.wdf.sap.corp/storage/user/105079/files/8e946195-3742-4662-98ca-60ed44560538)
    
    As you can see, after we updated the verification status of the business partner to verified in the business partner validation application, the search term of business partner updated to the VERIFIED in the S/4HANA On-Premise system, and also the Central Block removed as well. 


