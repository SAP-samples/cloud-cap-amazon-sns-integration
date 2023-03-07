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
    
    ![1](https://user-images.githubusercontent.com/29527722/223292413-f663889b-ac3b-4385-92b6-aed6457d0dbc.png)
    
    
2. In the **Add Subaccount** pop-up window, fill out the information of your subaccount, and then click the Save button. Then we have added the SAP BTP subaccount into the Cloud Connector successfully.
   
    ![2](https://user-images.githubusercontent.com/29527722/223292428-4ab47db4-04c4-4ac5-a93a-0bae1e73f086.png)
    
    > ### Note
    > - Please use below information to enter your BTP Subaccount data, you could get your **BTP Subaccount data** in the **BTP Subaccount overview** page.
    > - Region: The BTP Subaccount region
    > - Subaccount: **The BTP Subaccount ID**
    > - Display Name: User friendly name will be good enough
    > - Login E-mail: A working email address that could login to your BTP subaccount successfully
    > - Password: The password of the email address entered above
 
 
3. Select your newly added subaccount, then click **Cloud To On-Premise** on the left panel, then click **Add** button under the **Mapping Virtual to Internal System** section, to add a mapping of your S/4HANA On-Premise System.
 
    ![3](https://user-images.githubusercontent.com/29527722/223292459-5fb0b821-d263-4e20-aea0-d679682235f9.png)
    
4. Select below values in the Add System Mapping pop-up window.

   - Bck-end Type: ABAP System
   - Protocal: HTTPS
   - Internal Host & Port: Your S/4HANA On-Premise System host name and port
   - Virtual Host & Port: Read friendly name would be good enough. 
   - Principle Type: X.509 Certificate (Strict Usage)
   - Host In Request Header: Use Virtual Host
   - **Remember to check the Check Internal Host check box**
   
   Once the system mapping created successfully, it will be listing in the table of the Mapping Virtual To Internal System section. Click the **Check availability of internal host** icon to check if the system mapping is working or not. You will see **Reachable** show up under the **Check Result column** if this it is working fine.
   
   ![4](https://user-images.githubusercontent.com/29527722/223292485-9015812a-c060-4d03-bd44-df2b86c91935.png)

5. Select the system mapping we just created, then click **Add** icon under the **Resource Of** section. Here we need to give the URL path of the S/4HANA On-Premise resource URL that we would like to consume.
    
    ![5](https://user-images.githubusercontent.com/29527722/223292507-198d3196-7ae1-4524-894f-f401c30b6940.png)
    
    In the Add Resource pop-up window, give "**/**" in the **URL Path**, check the **Path And All Sub-Paths** check box, then click Save.
    
    ![6](https://user-images.githubusercontent.com/29527722/223292528-d040025f-fcbc-4545-98a1-7014c275b365.png)
    
6. After that, lets clikc the **Check availability of internal host** icon again to verify if everything is working fine. If the check result still shows Reachable, then we are all set and good to go.

## Configure the Destination in the SAP Business Technology Platform

Now lets setup the BTP destination, which pointing to the Virtual Host in your cloud connector, to enable Business Partner Validation application consume S/4HANA On-Premise business partner ODATA API in the BTP env.

1. Log into your SAP BTP subaccount, select **Instances and Subscriptions** on the left panel, then click **Create** button to create a **Destination Service instance**.
    
    ![1](https://user-images.githubusercontent.com/29527722/223292732-a44b1f78-6d76-43a4-8f7e-d4729b443824.png)
    
    ![2](https://user-images.githubusercontent.com/29527722/223292807-04d30027-611a-44d8-85ba-5008489159af.png)
    
2. Once the Destination Service instance created, select the instance and then click **Manage Instance** button on the right side to go into the destination service instance, and create the destination. So that this destination could be used by the Business Partner Validation application throuh the service binding during the run time.
    
    ![3](https://user-images.githubusercontent.com/29527722/223292839-da795ac2-57c5-40f4-baeb-31812a435085.png)
    
3. In the Manage Instance page, select the **Destinations** on the left panel, then click the **New Destination** icon on the right side to configure a new destination record.

    ![4](https://user-images.githubusercontent.com/29527722/223292862-fecfbfd8-049e-4b08-8d92-43bcf5020a46.png)
    
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
    
    ![5](https://user-images.githubusercontent.com/29527722/223292895-231eaeef-a310-4350-9a8b-90ef7c402687.png)

5. Once destination record created, click the **Check Connection** button to verify if the connection define in the destination record works ot not. If you see the message like "**Connection to {your destination name} successfully**", then we are all set and good to go. 

    ![6](https://user-images.githubusercontent.com/29527722/223292936-620ccd3f-39b6-4653-9f3a-662d44d19c5f.png)

    > ### Note
    > - **Remember to note down the destination record name, since we will need to use this destination in the Business Partner Verfication application's property file**.

    

