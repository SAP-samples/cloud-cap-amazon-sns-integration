# Setup Connectivity From SAP BTP to SAP S/4HANA On-Premise System

### You will learn
  - How to consume S/4HANA On-Premise OData API from CAP Application deploy on Business Technology Platform
  
> ### Prerequisites
> - SAP Business Technology Platform Global Account & SUbaccount
> - SAP S/4HANA On-Premise 2020 System.

In the last tutorial, we setup the configuration in both SAP S/4HANA On-Premise system and SAP Event Mesh to enable the communication from S/4HANA On-Premise to SAP BTP via enterprise messaging mechanism. Now lets finsh the setup to enable the communication from SAP BTP to On-Premise system.

## Configure Cloud Connector For S/4HANA System

In this step, we will focus on configure the Cloud Connector to enable the Cloud to On-Premise communication from SAP BTP to S/4HANA On-Premise system.

1. Open the Cloud Connector administration UI of for your S/4HANA On-Premise system. Click the **Add Subaccount** button to add your BTP Subaccount into the Cloud Connector

    > ### Note
    > - Please contact your S/4HANA On-Premise system administrator to get below S/4HANA On-Premise system's information
    > - Internal host name
    > - Internal port number
    > - Cloud Connector administration UI URL
    
    ![tempsnip](https://github.wdf.sap.corp/storage/user/105079/files/c38afc5c-4dae-4a36-9acd-c5bee2ac1330)
    
    
2. In the **Add Subaccount** pop-up window, fill out the information of your subaccount, and then click the Save button. Then we have added the SAP BTP subaccount into the Cloud Connector successfully.
   
    ![Capture](https://github.wdf.sap.corp/storage/user/105079/files/e43a0376-1117-4295-94ac-43f3da28769c)
    
    > ### Note
    > - Please use below information to enter your BTP Subaccount data, you could get your **BTP Subaccount data** in the **BTP Subaccount overview** page.
    > - Region: The BTP Subaccount region
    > - Subaccount: **The BTP Subaccount ID**
    > - Display Name: User friendly name will be good enough
    > - Login E-mail: A working email address that could login to your BTP subaccount successfully
    > - Password: The password of the email address entered above
 
 
3. Select your newly added subaccount, then click **Cloud To On-Premise** on the left panel, then click **Add** button under the **Mapping Virtual to Internal System** section, to add a mapping of your S/4HANA On-Premise System.
 
    ![tempsnip](https://github.wdf.sap.corp/storage/user/105079/files/86831927-7d06-4a54-8b92-c46a355138c0)
    
4. Select below values in the Add System Mapping pop-up window.

   - Bck-end Type: ABAP System
   - Protocal: HTTPS
   - Internal Host & Port: Your S/4HANA On-Premise System host name and port
   - Virtual Host & Port: Read friendly name would be good enough. 
   - Principle Type: X.509 Certificate (Strict Usage)
   - Host In Request Header: Use Virtual Host
   - **Remember to check the Check Internal Host check box**
   
   Once the system mapping created successfully, it will be listing in the table of the Mapping Virtual To Internal System section. Click the **Check availability of internal host** icon to check if the system mapping is working or not. You will see **Reachable** show up under the **Check Result column** if this it is working fine.
   
   ![tempsnip](https://github.wdf.sap.corp/storage/user/105079/files/bdd06301-023c-4381-86da-6c06064ed420)

5. Select the system mapping we just created, then click **Add** icon under the **Resource Of** section. Here we need to give the URL path of the S/4HANA On-Premise resource URL that we would like to consume.
    
    ![tempsnip](https://github.wdf.sap.corp/storage/user/105079/files/985e5afd-528e-439f-9cdc-6f7949d96164)
    
    In the Add Resource pop-up window, give "**/**" in the **URL Path**, check the **Path And All Sub-Paths** check box, then click Save.
    
    ![Capture](https://github.wdf.sap.corp/storage/user/105079/files/d52a4aec-66c9-49cd-b85c-9cf205b8067a)
    
6. After that, lets clikc the **Check availability of internal host** icon again to verify if everything is working fine. If the check result still shows Reachable, then we are all set and good to go.

## Configure the Destination in the SAP Business Technology Platform

Now lets setup the BTP destination, which pointing to the Virtual Host in your cloud connector, to enable Business Partner Validation application consume S/4HANA On-Premise business partner ODATA API in the BTP env.

1. Log into your SAP BTP subaccount, select **Instances and Subscriptions** on the left panel, then click **Create** button to create a **Destination Service instance**.
    
    ![7bd5fe9c-8254-4c0a-96de-986fe5fafc0d](https://github.wdf.sap.corp/storage/user/105079/files/e59f470e-052f-40a0-923c-6667687f3169)
    
    ![Capture](https://github.wdf.sap.corp/storage/user/105079/files/2316c829-5813-4a31-b9ff-fae91bc53dde)
    
2. Once the Destination Service instance created, select the instance and then click **Manage Instance** button on the right side to go into the destination service instance, and create the destination. So that this destination could be used by the Business Partner Validation application throuh the service binding during the run time.
    
    ![d3a3377d-12ea-4470-894b-83560f65f841](https://github.wdf.sap.corp/storage/user/105079/files/a18c5a1f-ecd5-43a2-81fc-82a79eedf971)
    
3. In the Manage Instance page, select the **Destinations** on the left panel, then click the **New Destination** icon on the right side to configure a new destination record.

    ![3431ad03-1462-49a3-9cff-b29ed0d32f71](https://github.wdf.sap.corp/storage/user/105079/files/4d859cfd-e943-41b6-bc20-95669dda44cd)
    
4. Enter the destination configuration data based on the virtual to internal system mapping in the cloud connector. 

    > ### **Value Help**
      - Name: the name of your destination record, user-friendly name would be ood enough
      - Type: HTTP
      - URL: https:// virtual host + virtual port in cloud connector + /sap/opu/odata/sap
      - Proxy Type: OnPremise
      - Authentication: BasicAuthentication
      - User: the technology user we created in the previous step
      - Password: the technology user's password
      - **Dont't forget to add three additional properties shows in the below image**
    
    ![f24ba55d-33a3-4838-9935-b0156d69cce2](https://github.wdf.sap.corp/storage/user/105079/files/3cb0acf7-957c-404f-a0e3-1c9af7573740)

5. Once destination record created, click the **Check Connection** button to verify if the connection define in the destination record works ot not. If you see the message like "**Connection to {your destination name} successfully**", then we are all set and good to go. 

    ![Capture](https://github.wdf.sap.corp/storage/user/105079/files/ffe4d37d-384d-4c2a-a578-deb45cb1ce66)

    > ### Note
    > - **Remember to note down the destination record name, since we will need to use this destination in the Business Partner Verfication application's property file**.

    

