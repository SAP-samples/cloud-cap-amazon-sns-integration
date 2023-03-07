# S/4HANA Business Partner Validation Application Walk Through

In this section, I will show you how could the validator perform the their work on this business partner validation application deployed on the SAP BTP platform, instance of login to the original S/4HANA On-Premise system to do the validation.

> ### **Prerequis**
> - Please assign **S/4HANA-Business-Partner-App-Validator** role collection to yourself on the SAP BTP in-order to perform validator action.
> - Please assign **S/4HANA-Business-Partner-App-Admin** role collection to yourself on the SAP BTP in-order to perform validator action.
>   ![1](https://user-images.githubusercontent.com/29527722/223295676-15cadf6b-e941-443a-872e-f64db0342a26.png)
>  
> - User who have the **S/4HANA-Business-Partner-App-Validator** role have the **permission** to **update business partner address**, and **update the business partner verification status code** in the App.
> - User who have the **S/4HANA-Business-Partner-App-Admin** role have the **full permission (READ, WRITE, DELETE)** in the App



1. First of all, let's create a new business partner in the S/4HANA On-Premise system, and **note down** the newly created business partner ID. (**Business Partner ID: 1000634**)
    
    > ### **Note**
    > - Remember to mark the newly created business partner Status as **Central Block**

    ![2](https://user-images.githubusercontent.com/29527722/223295701-36302c27-8536-4c8e-8d2d-9159ba434bf7.png)
    
    ![3](https://user-images.githubusercontent.com/29527722/223295716-1200408d-c8e1-4760-85c9-e239fd557f93.png)

2. This time the your email address subscribe on the S4HANA-Business-Partner-Validation topic would receive an email notification as below.

    ![4](https://user-images.githubusercontent.com/29527722/223295746-09a3c89c-f2e6-4e53-9a50-5f83b7ede2f5.png)

3. Click on the link contains in the email notification, it will bring you directly into the Business Partner Validation Fiori UI. 

    ![5](https://user-images.githubusercontent.com/29527722/223295760-902022e0-084b-4054-93c0-bfb7f99e3e38.png)
    
4. In the S/4HANA Business Partner Validation Fiori UI, click the **Go** button and then you could see the newly created business partner (ID **1000634**) showing up there with the **Verification Status** as **NEW**

    ![6](https://user-images.githubusercontent.com/29527722/223295771-3808113f-2f2a-4541-a089-290de9bbe24e.png)

5. Select the record of the newly created business partner and then we could go deeper and see the address information of this business partner.

    ![7](https://user-images.githubusercontent.com/29527722/223295783-5e5e4dea-cc2f-496a-9273-c054833c7576.png)

6. Click the edit button to perform the validation. We could make changes on the Street Name and Postcal Code field if necessary, and update the Verification Status code depends on the situation.

    ![8](https://user-images.githubusercontent.com/29527722/223295796-8cb1ee3a-a196-44db-a839-194bfbc432be.png)
    
7. If everything looks good, then we could update the verification status code to VERIFIED and this will acutally release the business partner in the S/4HANA On-Premise system. 
    
    ![9](https://user-images.githubusercontent.com/29527722/223295807-ab75ecbf-f9e0-44bb-a49a-a6bd6be7071e.png)
    
8. Now lets go back to the S/4HANA On-Premise system and search for the business partner. 

    ![10](https://user-images.githubusercontent.com/29527722/223295820-0ae0de6a-fd34-4861-8877-5d9320b2fb73.png)
    
    ![11](https://user-images.githubusercontent.com/29527722/223295839-73cf6b4b-df27-4f71-b6df-0760ccf526a9.png)
    
    As you can see, after we updated the verification status of the business partner to verified in the business partner validation application, the search term of business partner updated to the VERIFIED in the S/4HANA On-Premise system, and also the Central Block removed as well. 
