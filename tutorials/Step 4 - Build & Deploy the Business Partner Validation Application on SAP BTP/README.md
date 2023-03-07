# Build & Deploy Business Partner Validation Application into SAP BTP Subaccount

### You will learn
  - How to create, configure, and bind SAP managed BTP services to the business partner validation application
  - How to build & deploy business partner validation application into your SAP BTP subaccount

> ### Prerequisites
> - SAP Business Technology Platform Global Account & Subaccount
> - SAP Business Application Studio subscription

## Create, Configure, and Bind SAP Managed BTP Services

Inside of this application, the business partner validation application need to consume S/4HANA On-Premise Business Partner OData API and subscribe to the message queue inside of the SAP Event Mesh. To do so, we need to create serveral SAP managed BTP service instances, and then bind these instances to the business partner validation application through **Service Binding**. In the following section, I will show you how to create certain SAP managed BTP service instances, and then bind those instances to the business partner validation application. 

1. Clone the business partner validation application code from **main** branch into your SAP Business Application Studio dev space.

    ![1](https://user-images.githubusercontent.com/29527722/223293267-f1b75c25-3e80-40c2-b23a-2c54dbfaf6da.png)
    
    > ### **Notes**
    > - **app** folder contains the code for business partner validation Fiori Element UI
    > - **db** folder contains the configuration for Sqlite DB (Local Development & Testing), HANA Cloud DB (BTP Deployment). The data in csv files under the csv folder will be loading to the Sqlite DB for local deveploment and testing purpose.
    > - **srv** folder contains the code of business partner validation application backend Java code.
    > - **mta.yaml** file is the deployment script for deployment purpose.

2. Open the **mat.yaml** file, scrow down to the line #174, change the name of this resource to the event mesh instance's name we created in this [step](https://github.com/SAP-samples/cloud-cap-amazon-sns-integration/tree/main/tutorials/Step%202%20-%20Setup%20S4HANA%20Enterprise%20Messaging%20%26%20SAP%20BTP%20Event%20Mesh#setup-sap-event-mesh)
    
    ![2](https://user-images.githubusercontent.com/29527722/223293290-1f32fb85-d781-4434-840a-1a0d113bab82.png)

3. In the **mta.yaml** file, scrow down to the line #189, change the name of this resource to the SAP BTP Destination Service instance we created in this [step](https://github.com/SAP-samples/cloud-cap-amazon-sns-integration/tree/main/tutorials/Step%203%20-%20Setup%20Connectivity%20From%20SAP%20BTP%20To%20SAP%20S4HANA%20On-Premise%20System#configure-the-destination-in-the-sap-business-technology-platform)

    ![3](https://user-images.githubusercontent.com/29527722/223293317-5de305fc-1121-4227-b544-f77b55cc97a9.png)
    
4. Go back to the SAP BTP subaccount, create the **SAP Connectivity Service instance**. Make sure the give the name as **s4hana-connectivity** for the convenience of deployment purpose. Please refer to below image when you create Connectivity Service instance.
    
    ![4](https://user-images.githubusercontent.com/29527722/223293340-84f1eae0-35d1-4abb-8602-efcdd38e6fe0.png)

Now we have created & binded the SAP Event Mesh, Destination Service, and Connectivity Service instances for the business partner validation application successfully. Let's jump to the next section to make some configuration changes inside of the business partner validation application.

## Make Configuration Changes In the Business Partner Validation Application Backend Service

Since the backend service of the business partner validation application is build on SAP Cloud Application Programming Model (Java), so that we define several configuration and property in the application.yml file. Let make some changes there to make sure the deployment could be done without any error.

1. Open the **srv/src/main/resources/application.yaml** file. Change the Amazon SNS **Topic ARN** and **Region** value to the one that we created in this previous [step](https://github.com/SAP-samples/cloud-cap-amazon-sns-integration/tree/main/tutorials/Step%201%20-%20Setup%20Amazon%20Simple%20Notification%20Service#create-amazon-sns-topic).

2. In the business partner validation application, the backend service subscribe to the message queue in the Event Mesh, please refer to this [documentation](https://cap.cloud.sap/docs/java/messaging-foundation?q=mta.yaml#supported-message-brokers) for more details. So in the application.yaml file, we needs to **change the event mesh message queue subscription property** under the **default** and **cloud** spring profile.
    
    Please change the subscribePrefix and queue.name value in the application.yml file to the one we created in this previous [step](https://github.wdf.sap.corp/SCE/CAP-AWS-SNS-Integration/tree/main/tutorials/Step%202%20-%20Setup%20S4HANA%20Enterprise%20Messaging%20%26%20SAP%20BTP%20Event%20Mesh#setup-sap-event-mesh).
    
    ![1](https://user-images.githubusercontent.com/29527722/223293749-4aeacee2-92ae-48fb-bcc8-8b6f737fb0c5.png)

3. In the business partner validation application, the backend service consume S/4HANA On-Premise Business Partner OData API via **Remote Services mechanism** provided by the SAP Cloud Application Programming Model. Please refer to this [documentation](https://cap.cloud.sap/docs/java/remote-services?q=mta.yaml#configuring-remote-services) for more details.

    Please change the **destination name** to the one **we created in the step 4 in this previous** [**tutorial**](https://github.wdf.sap.corp/SCE/CAP-AWS-SNS-Integration/tree/main/tutorials/Step%203%20-%20Setup%20Connectivity%20From%20SAP%20BTP%20To%20SAP%20S4HANA%20On-Premise%20System#configure-the-destination-in-the-sap-business-technology-platform).
    
    ![2](https://user-images.githubusercontent.com/29527722/223293766-795e0294-f2d8-485b-b544-90e12804be39.png)

Now we have modified the property file for the business partner validation application backend service successfully. Let's go and deploy the entire full stack application in the next section.

## Deploy the Business Partner Validation App into BTP Subaccount

> ### Prerequisites
> - Login to the BTP subaccount with yor credential by using the followin CF CLI in the Business Application Studio terminal
> - **cf api** your BTP subaccount  api endpoint.
> - **cf login** with your BTP subaccount credentail (username + password)

In this section, I will show you how to build and deploy the business partner validation application to your SAP BTP subaccount.

1. Go back to the dev space in the **Business Application Studio**, right click on the **mat.yaml** file, then select **Build MTA Project** in the drop down to build and package the application.
    
    ![1](https://user-images.githubusercontent.com/29527722/223293939-64c4391e-9315-4330-9861-ebe4322b9382.png)
    
    Once the build process complete, there will be a new folder, which name is **mta_archives**, show up under the project root path. Inside of this folder, it contains a **business-partner-validation_1.0.0-SNAPSHOT.mtar** file which we could use for the BTP deployment.
    
    ![2](https://user-images.githubusercontent.com/29527722/223293956-9e2b554a-d0fe-4672-b3b2-191c21c56ccd.png)

2. Right click on the **business-partner-validation_1.0.0-SNAPSHOT.mtar** file, then select **Deploy MTA Archive** from the dropdown manul. It will trigger the deployment process of the business partner validation project.

    ![3](https://user-images.githubusercontent.com/29527722/223293976-d78589d3-1448-43a6-8383-ff4b58dfda66.png)
    
    Once the deployment finished, in the deployment task console we could see the words as **Process Finished**. This means that the BTP deployment done without any issues. Cheers!
    
    ![4](https://user-images.githubusercontent.com/29527722/223293994-ebc55a8d-e1b7-4d24-aecf-8333740364cb.png)

## Setup Fiori Launchpad For the Business Partner Validation Application

1. Go back to the **SAP BTP Subaccount**, click the **Instances and Subscription** on the left panel, then click the **Go to Application** icon of the Launchpad Service subscription.
    
    ![1](https://user-images.githubusercontent.com/29527722/223294253-a0dc6448-b54f-4833-9008-673d399a89ee.png)
    
2. In the Lanuchpad Service console, Select Chaneel Manager on the lft panel, then click Update Content icon for the HTML5 Apps.

    ![2](https://user-images.githubusercontent.com/29527722/223294271-6fe15d4e-8e66-43ff-aa57-534a3251f75f.png)
    
3. Select **Content Manager** on the left panel, then select **Content Explore** and click the **HTMP5 Apps** card.

    ![3](https://user-images.githubusercontent.com/29527722/223294306-e01452b5-518d-457d-a33f-244f77119fd3.png)

4. Check the **S/4HANA Business Partner Validation** row and then click **Add to My Content** button

    ![4](https://user-images.githubusercontent.com/29527722/223294345-9652c020-46c5-4e50-80c5-c226d2b2c54a.png)
    
    Go back to **My Content** page and this time you could see the **S/4HANA Business Partner Validation Fiori application** listing there.
    
    ![5](https://user-images.githubusercontent.com/29527722/223294378-c4716256-e08f-4c8e-9542-9f0a06aefcbf.png)

5. Click on the **Everyone** role lists in the **Items** table under **My Content** page. Inside of Everyone Role management page, click the **Edit** button, **assign the S/4HANA Business Partner Validation HTML5 Apps to this role**, then click **Save** button to save the assignment.

    ![6](https://user-images.githubusercontent.com/29527722/223294388-6bbf142c-d95b-4726-951e-1a7df9f5042b.png)
    
    Now everyone could see this S/4HANA Business Partner Validation Fiori application in the Fiori Launchpad. But don't worry about the security issue, this application is protected by the XSUAA service, which means that only the people whom have the certain roles could get access into this application.

6. Go back to the **My Content** page, click on the **+New** button, and then select the **Group icon** in the dropdown to create a new group.

    ![7](https://user-images.githubusercontent.com/29527722/223294406-206467c0-a1c2-4cd7-a138-a5be16c04c28.png)
    
    Give the group name as S/4HANA Business Partner Validation, then assign the S/4HANA Business Partner Validation HTML5 App to this group, then click **Save** button to save the assignment and create the group.
    
    ![8](https://user-images.githubusercontent.com/29527722/223294417-b47863ee-e3cb-4ca7-b98b-80e5b7f10114.png)
    
7. Select **Site Directory** form the left panel, then click **+ Create Site** button to create a new site for the Business Partner Validation application in the Fiori Launchpad
    
    ![9](https://user-images.githubusercontent.com/29527722/223294457-53674b38-11e4-463f-aa45-058238932bb6.png)
    
    Give the **Site Name** as **S/4HANA Business Partner Validation** in the pop up window and then click the **Create** button to create the site.
    
    ![10](https://user-images.githubusercontent.com/29527722/223294489-f9f9aee3-1469-4c49-aa58-f62b172a03c5.png)
    
    Once the site created, go back to the **Site Directory** page again. This time you could see that the new S/4HANA Business Partner Validation site has been created and showing up there. Click the **Go to site** icon to check if we could open the business partner validation application.
    
    ![11](https://user-images.githubusercontent.com/29527722/223294503-71c45d31-46b8-443b-8438-b234ed75567c.png)

## Create User-Provided Variables and Bind to Business Partner Validation Backend CAP App.
In-order to publish message to the Amazon SNS topic, we need to use the Amazon IAM programmatic user's access key and secret access key's value in our CAP application in-order to authenticate and authorize to the Amazon SNS topic we created previsouly. It's not an good idea to store these long-term AWS credentails in application configuration file or hard-coded in the code since it will bring security concerns and cause potential credentials leaking issue.
    
Thus, we will store these sensitive credentials as an user-provided variables and bind those variables to the backend CAP application. So that those user-provided variables will be considering as system envirounment variable, and our CAP application could consume the env variables during the run time.
    
1. Go back to the **SAP BTP Subaccount**, go into the space and click on the **business-partner-validation-srv**. Click on the **User-Provided Variables** icon on the left panel.

    ![1](https://user-images.githubusercontent.com/29527722/223294609-f66b4376-ad53-444e-a548-9b23d366a28a.png)

2. Create the User-Provided Variable **IAM_USER_ACCESS_KEY** by click the **Add Variable** button, give the value as the **Amazon IAM programmatic user's** **Access Key ID** we created in this [step](https://github.wdf.sap.corp/SCE/CAP-AWS-SNS-Integration/tree/main/tutorials/Step%201%20-%20Setup%20Amazon%20Simple%20Notification%20Service#grant-publish-message-permission-to-the-iam-programmatic-user-on-amazon-sns-topic). Click on Save.

    ![2](https://user-images.githubusercontent.com/29527722/223294626-338a5cd4-13d1-4cbe-bb9f-25b11a69138e.png)

3. Create the User-Provided Variable **IAM_USER_SECRET_ACCESS_KEY** by click the **Add Variable** button, give the value as the **Amazon IAM programmatic user's** **Secret Access Key** we created in this [step](https://github.wdf.sap.corp/SCE/CAP-AWS-SNS-Integration/tree/main/tutorials/Step%201%20-%20Setup%20Amazon%20Simple%20Notification%20Service#grant-publish-message-permission-to-the-iam-programmatic-user-on-amazon-sns-topic). Click on Save.

    ![3](https://user-images.githubusercontent.com/29527722/223294647-ed4163e9-7c9c-4103-bb81-262c63a4375b.png)

4. Create the User-Provided Variable **BP_VALIDATION_UI_URL** by click the **Add Variable** button, give the value as the Business Partner Validation UI URL. Click on Save.

   ![4](https://user-images.githubusercontent.com/29527722/223294660-5a3be6d4-c939-48cd-bfe5-6b823c3a8773.png)

5. Go back to the application overview page, click on the **Restart** button to re-start the backedn CAP app.


Now we have finished all the steps to build and deploy the business partner validation application on the Business Technology Platform successfully. I will see you in the next section to show you the End-to-End flow of validating business partner within the application.
    

    
    

