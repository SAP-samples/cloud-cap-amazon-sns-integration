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

    ![Capture](https://github.wdf.sap.corp/storage/user/105079/files/a3165425-8d7e-44a3-962f-883a4150dab2)
    
    > ### **Notes**
    > - **app** folder contains the code for business partner validation Fiori Element UI
    > - **db** folder contains the configuration for Sqlite DB (Local Development & Testing), HANA Cloud DB (BTP Deployment). The data in csv files under the csv folder will be loading to the Sqlite DB for local deveploment and testing purpose.
    > - **srv** folder contains the code of business partner validation application backend Java code.
    > - **mta.yaml** file is the deployment script for deployment purpose.

2. Open the **mat.yaml** file, scrow down to the line #174, change the name of this resource to the event mesh instance's name we created in this [step](https://github.wdf.sap.corp/SCE/CAP-AWS-SNS-Integration/tree/main/tutorials/Step%202%20-%20Setup%20S4HANA%20Enterprise%20Messaging%20%26%20SAP%20BTP%20Event%20Mesh#setup-sap-event-mesh)
    
    ![Capture](https://github.wdf.sap.corp/storage/user/105079/files/96bf23dc-314a-42f3-a06b-eb48c2bee438)

3. In the **mta.yaml** file, scrow down to the line #189, change the name of this resource to the SAP BTP Destination Service instance we created in this [step](https://github.wdf.sap.corp/SCE/CAP-AWS-SNS-Integration/tree/main/tutorials/Step%203%20-%20Setup%20Connectivity%20From%20SAP%20BTP%20To%20SAP%20S4HANA%20On-Premise%20System#configure-the-destination-in-the-sap-business-technology-platform)

    ![Capture](https://github.wdf.sap.corp/storage/user/105079/files/6f25990d-9126-4a72-bc9c-076093b39067)
    
4. Go back to the SAP BTP subaccount, create the **SAP Connectivity Service instance**. Make sure the give the name as **s4hana-connectivity** for the convenience of deployment purpose. Please refer to below image when you create Connectivity Service instance.
    
    ![3dd720ac-af72-4b30-b21a-d05843100809](https://github.wdf.sap.corp/storage/user/105079/files/12b898ee-e94a-4519-b314-65443ca7402f)

Now we have created & binded the SAP Event Mesh, Destination Service, and Connectivity Service instances for the business partner validation application successfully. Let's jump to the next section to make some configuration changes inside of the business partner validation application.

## Make Configuration Changes In the Business Partner Validation Application Backend Service

Since the backend service of the business partner validation application is build on SAP Cloud Application Programming Model (Java), so that we define several configuration and property in the application.yml file. Let make some changes there to make sure the deployment could be done without any error.

1. Open the **srv/src/main/resources/application.yaml** file. Change the Amazon SNS **Topic ARN** and **Region** value to the one that we created in this previous [step](https://github.wdf.sap.corp/SCE/CAP-AWS-SNS-Integration/tree/main/tutorials/Step%201%20-%20Setup%20Amazon%20Simple%20Notification%20Service#create-amazon-sns-topic).

2. In the business partner validation application, the backend service subscribe to the message queue in the Event Mesh, please refer to this [documentation](https://cap.cloud.sap/docs/java/messaging-foundation?q=mta.yaml#supported-message-brokers) for more details. So in the application.yaml file, we needs to **change the event mesh message queue subscription property** under the **default** and **cloud** spring profile.
    
    Please change the subscribePrefix and queue.name value in the application.yml file to the one we created in this previous [step](https://github.wdf.sap.corp/SCE/CAP-AWS-SNS-Integration/tree/main/tutorials/Step%202%20-%20Setup%20S4HANA%20Enterprise%20Messaging%20%26%20SAP%20BTP%20Event%20Mesh#setup-sap-event-mesh).
    
    ![tempsnip](https://github.wdf.sap.corp/storage/user/105079/files/b41c745c-1b28-425a-b8d5-8a49ccefcc3c)

3. In the business partner validation application, the backend service consume S/4HANA On-Premise Business Partner OData API via **Remote Services mechanism** provided by the SAP Cloud Application Programming Model. Please refer to this [documentation](https://cap.cloud.sap/docs/java/remote-services?q=mta.yaml#configuring-remote-services) for more details.

    Please change the **destination name** to the one **we created in the step 4 in this previous** [**tutorial**](https://github.wdf.sap.corp/SCE/CAP-AWS-SNS-Integration/tree/main/tutorials/Step%203%20-%20Setup%20Connectivity%20From%20SAP%20BTP%20To%20SAP%20S4HANA%20On-Premise%20System#configure-the-destination-in-the-sap-business-technology-platform).
    
    ![tempsnip](https://github.wdf.sap.corp/storage/user/105079/files/127834af-e7d9-4bd8-b166-d51a5dbe47c0)

Now we have modified the property file for the business partner validation application backend service successfully. Let's go and deploy the entire full stack application in the next section.

## Deploy the Business Partner Validation App into BTP Subaccount

> ### Prerequisites
> - Login to the BTP subaccount with yor credential by using the followin CF CLI in the Business Application Studio terminal
> - **cf api** your BTP subaccount  api endpoint.
> - **cf login** with your BTP subaccount credentail (username + password)

In this section, I will show you how to build and deploy the business partner validation application to your SAP BTP subaccount.

1. Go back to the dev space in the **Business Application Studio**, right click on the **mat.yaml** file, then select **Build MTA Project** in the drop down to build and package the application.

    ![b13b0c63-fc03-47e3-9be7-08e7c92ab0e5](https://github.wdf.sap.corp/storage/user/105079/files/08606baa-bfc9-4556-b092-0b89685e4d06)
    
    Once the build process complete, there will be a new folder, which name is **mta_archives**, show up under the project root path. Inside of this folder, it contains a **business-partner-validation_1.0.0-SNAPSHOT.mtar** file which we could use for the BTP deployment.
    
    ![tempsnip](https://github.wdf.sap.corp/storage/user/105079/files/0c770cb2-1bb2-4f0f-ac8d-5bc26f357ab6)

2. Right click on the **business-partner-validation_1.0.0-SNAPSHOT.mtar** file, then select **Deploy MTA Archive** from the dropdown manul. It will trigger the deployment process of the business partner validation project.

    ![tempsnip](https://github.wdf.sap.corp/storage/user/105079/files/6c04055e-75d4-485c-bc83-ec2ca98f8a84)
    
    Once the deployment finished, in the deployment task console we could see the words as **Process Finished**. This means that the BTP deployment done without any issues. Cheers!
    
    ![tempsnip](https://github.wdf.sap.corp/storage/user/105079/files/d8102d58-1d13-48a8-937e-a3497dc0c6aa)

## Setup Fiori Launchpad For the Business Partner Validation Application

1. Go back to the **SAP BTP Subaccount**, click the **Instances and Subscription** on the left panel, then click the **Go to Application** icon of the Launchpad Service subscription.
    
    ![f9ed2888-64ea-4833-a58a-b28887cadf8b](https://github.wdf.sap.corp/storage/user/105079/files/589f69d3-d106-4b7b-8554-297ea5262083)
    
2. In the Lanuchpad Service console, Select Chaneel Manager on the lft panel, then click Update Content icon for the HTML5 Apps.

    ![addf67fe-485e-42e2-b191-9c65cbfb88f3](https://github.wdf.sap.corp/storage/user/105079/files/d48f0bd5-e721-4739-b1d8-13e61430a6ab)
    
3. Select **Content Manager** on the left panel, then select **Content Explore** and click the **HTMP5 Apps** card.

    ![9eb40dd4-e119-4f4e-a5f6-99fd40741024](https://github.wdf.sap.corp/storage/user/105079/files/6da83a18-3f5c-4e5e-a3b7-43accc6ac313)

4. Check the **S/4HANA Business Partner Validation** row and then click **Add to My Content** button

    ![tempsnip](https://github.wdf.sap.corp/storage/user/105079/files/c9347ea3-e9ae-49fe-bf38-ee657a91cfad)
    
    Go back to **My Content** page and this time you could see the **S/4HANA Business Partner Validation Fiori application** listing there.
    
    ![tempsnip](https://github.wdf.sap.corp/storage/user/105079/files/1e6114bb-7d6f-478e-a017-af3bf61c6a3b)

5. Click on the **Everyone** role lists in the **Items** table under **My Content** page. Inside of Everyone Role management page, click the **Edit** button, **assign the S/4HANA Business Partner Validation HTML5 Apps to this role**, then click **Save** button to save the assignment.

    ![tempsnip](https://github.wdf.sap.corp/storage/user/105079/files/ca65952c-2561-4046-b7a6-cbcaaa2e6222)
    
    Now everyone could see this S/4HANA Business Partner Validation Fiori application in the Fiori Launchpad. But don't worry about the security issue, this application is protected by the XSUAA service, which means that only the people whom have the certain roles could get access into this application.

6. Go back to the **My Content** page, click on the **+New** button, and then select the **Group icon** in the dropdown to create a new group.

    ![tempsnip](https://github.wdf.sap.corp/storage/user/105079/files/b5ff4c15-eaf1-4c98-9c6a-8b2b308daa89)
    
    Give the group name as S/4HANA Business Partner Validation, then assign the S/4HANA Business Partner Validation HTML5 App to this group, then click **Save** button to save the assignment and create the group.
    
    ![tempsnip](https://github.wdf.sap.corp/storage/user/105079/files/f31f21f1-939b-49fb-a96c-e15d47c571cb)
    
7. Select **Site Directory** form the left panel, then click **+ Create Site** button to create a new site for the Business Partner Validation application in the Fiori Launchpad
    
    ![tempsnip](https://github.wdf.sap.corp/storage/user/105079/files/fdf27e29-b39a-42bf-a025-646fbfdb8a7d)
    
    Give the **Site Name** as **S/4HANA Business Partner Validation** in the pop up window and then click the **Create** button to create the site.
    
    ![Capture](https://github.wdf.sap.corp/storage/user/105079/files/d848f039-1233-4197-9953-1dccc68367a8)
    
    Once the site created, go back to the **Site Directory** page again. This time you could see that the new S/4HANA Business Partner Validation site has been created and showing up there. Click the **Go to site** icon to check if we could open the business partner validation application.
    
    ![tempsnip](https://github.wdf.sap.corp/storage/user/105079/files/41e71d34-3542-4a27-8a61-4f26d318637e)

## Create User-Provided Variables and Bind to Business Partner Validation Backend CAP App.
In-order to publish message to the Amazon SNS topic, we need to use the Amazon IAM programmatic user's access key and secret access key's value in our CAP application in-order to authenticate and authorize to the Amazon SNS topic we created previsouly. It's not an good idea to store these long-term AWS credentails in application configuration file or hard-coded in the code since it will bring security concerns and cause potential credentials leaking issue.
    
Thus, we will store these sensitive credentials as an user-provided variables and bind those variables to the backend CAP application. So that those user-provided variables will be considering as system envirounment variable, and our CAP application could consume the env variables during the run time.
    
1. Go back to the **SAP BTP Subaccount**, go into the space and click on the **business-partner-validation-srv**. Click on the **User-Provided Variables** icon on the left panel.

    ![f61032a3-56f4-4aa0-b8d2-617d31d1884e](https://github.wdf.sap.corp/storage/user/105079/files/f469d89c-340e-48d9-8ee8-bf583482aa48)

2. Create the User-Provided Variable **IAM_USER_ACCESS_KEY** by click the **Add Variable** button, give the value as the **Amazon IAM programmatic user's** **Access Key ID** we created in this [step](https://github.wdf.sap.corp/SCE/CAP-AWS-SNS-Integration/tree/main/tutorials/Step%201%20-%20Setup%20Amazon%20Simple%20Notification%20Service#grant-publish-message-permission-to-the-iam-programmatic-user-on-amazon-sns-topic). Click on Save.

    ![40f9e0c3-50f6-4780-a9cb-88a5f8dd4228](https://github.wdf.sap.corp/storage/user/105079/files/b57e4822-1d03-4bf8-a033-95081b72780e)

3. Create the User-Provided Variable **IAM_USER_SECRET_ACCESS_KEY** by click the **Add Variable** button, give the value as the **Amazon IAM programmatic user's** **Secret Access Key** we created in this [step](https://github.wdf.sap.corp/SCE/CAP-AWS-SNS-Integration/tree/main/tutorials/Step%201%20-%20Setup%20Amazon%20Simple%20Notification%20Service#grant-publish-message-permission-to-the-iam-programmatic-user-on-amazon-sns-topic). Click on Save.

    ![83967597-b261-43db-b862-7b8184614bd7](https://github.wdf.sap.corp/storage/user/105079/files/3bb912bc-6634-424b-9f05-e71ebdfce2fa)

4. Create the User-Provided Variable **BP_VALIDATION_UI_URL** by click the **Add Variable** button, give the value as the Business Partner Validation UI URL. Click on Save.

    ![f02057b3-0691-43d0-a884-ada0ce4d323e](https://github.wdf.sap.corp/storage/user/105079/files/bc55cd09-222d-40ce-86cd-8e01213bdde4)

5. Go back to the application overview page, click on the **Restart** button to re-start the backedn CAP app.


Now we have finished all the steps to build and deploy the business partner validation application on the Business Technology Platform successfully. I will see you in the next section to show you the End-to-End flow of validating business partner within the application.
    

    
    

