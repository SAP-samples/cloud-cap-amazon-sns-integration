## Setup S/4HANA On-Premise Enterprise Messaging & SAP BTP Event Mesh

### You will learn
  - How to set up SAP S/4HANA On-Premise Enterprise Messaging
  - How to set up SAP Event Mesh on SAP Business Technology Platform
  
> ### Prerequisites
> - SAP Business Technology Platform Global Account & Subaccount
> - SAP S/4HANA On-Premise System
> - Please follow this [documentation](https://github.com/SAP-samples/cloud-extension-s4hana-business-process/blob/mission/mission/configure-oData-Service/README.md#create-technical-user-and-assign-role) to create a technical user in the S/4HANA On-Premise System.

## Setup SAP Event Mesh
#### Please finish the third task in the prerequisites section first.

In order to let S/4HANA On-Premise system's business partner create/change message reach SAP Event Mesh message queue successfully, the first step we need to do is create Event Mesh enterprise messaging instance, obtain a service key of this event mesh instance, and then create a message queue in the Event Mesh.

1. Please follow [this documentation](https://help.sap.com/docs/SAP_EM/bf82e6b26456494cbdd197057c09979f/d0483a9e38434f23a4579d6fcc72654b.html) to create Event Mesh enterprise messaging instance

2. Once the Event Mesh enterprise messaging instance created, click the instance and then click the **Create** button under the **Service Keys** section on the right side to create a service key.

    ![94aba2a3-efde-403b-9a3e-5cab85cb5cb8](https://github.wdf.sap.corp/storage/user/105079/files/2f900052-12db-4d29-a2b1-180524849ccb)

    In the **New Service Key** page, give the proper name of the service key, leave everything as blank, then click the **Create** button.
    
    ![aa7c0e57-bc74-4fa0-b16c-c77781ab72ac](https://github.wdf.sap.corp/storage/user/105079/files/d6cf694e-2784-418e-91b6-d897c2a7ccb1)
    
    Once the service key created, **note down the service key json credentials** since we will need it to configure the enterprise messaging in the S/4HANA On-Premise System later.
    
3. Go back to the BTP subaccount overview page, click the **Instances and Subscription** on the left panel, then click the **Create** button in the right upper side, to create a Event Mesh Standard subscription. 

    ![1362c95a-b901-4ea2-b454-1cb764ffa142](https://github.wdf.sap.corp/storage/user/105079/files/67c89ea9-163e-4e6a-89bf-772998885201)
    
    > ### Note
    > - Remember to assign **Enterprise Messaging Subscription Administrator** role collection to your user so that you could log into the Event Mesh Mangement Console
    > - Please read [this documentation](https://help.sap.com/docs/SAP_EM/bf82e6b26456494cbdd197057c09979f/637d331010e54a2999e2f023d2de1130.html) for the steps of assigning role to the user via SAP Business Technology Platform

4. Once the Event Mesh standard subscription creating successfully, the Event Mesh subscription will be listing under the **Subscriptions** table on the BTP Subaccount. Click the **Go To Application** icon so that we could log into the Event Mesh management console and see the **messaging client** we just created in the previous step.

    ![cd710ad0-0e23-4cc7-a052-d0a2ad0843ef](https://github.wdf.sap.corp/storage/user/105079/files/21952fde-6804-41a2-8199-b86e417afd73)
    
    ![tempsnip](https://github.wdf.sap.corp/storage/user/105079/files/96d6ec50-f342-4052-9644-c212a12d1251)

5. In the Event Mesh management console, click the **messaging client** we just created, select **Queues** tile and click the **Create Queue** button to create a message queue in the messaging client.

    ![b19d9d54-8761-46fa-ae47-d0fe20b45925](https://github.wdf.sap.corp/storage/user/105079/files/490db82a-b04f-4b11-8131-7adb102293c7)
    
    In the **Create a New Queue** pop up window, give a proper name for the message queue, select **NON_EXCLUSIVE** as **Access Type**, and then click **Create** button to create the message queue. After this you will see the message queue created successfully and listing under the Queues tile.
    
    ![Capture](https://github.wdf.sap.corp/storage/user/105079/files/b9254b0a-f2d5-469b-91b0-5547bd37626c)
    
    Now is the time to configure the message queue subscription. In order to receive S/4HANA On-Premise Business partner create/change event message, we need to let the message queue subscribe on the S/4HANA On-Premise business partner event topic.
    
    > ### Note
    > - Please find the S/4HANA On-Premise system business partner event topic name format in [SAP API Hub](https://api.sap.com/event/SAPS4HANABusinessEvents_BusinessPartnerEvents/resource).
    
    Click **Actions** button of the message queue, then select **Queue Subscriptions** to manage messae queue's subscription.
    
    ![tempsnip](https://github.wdf.sap.corp/storage/user/105079/files/343f6870-43a4-406f-965e-26f2997a66c4)

    In the Queue Subscription pop up window, copy and paste the S/4HANA On-Premise Business Partner create and change event topic name in the **Topic** textbox.
    
    > ### Note
    > - If you are using **SAP S/4HANA On-Premise 2020** System, the format of the input topic name should be **namespace/ce/topicName**
    > - Otherwise, the format of the input topic name should be **namespace/topicName**
    
    ![tempsnip](https://github.wdf.sap.corp/storage/user/105079/files/08e2a58b-30b7-4301-9b68-d85df63eba00)

Above are all the steps to configure the SAP Event Mesh and Event Mesh message queue. We will show how to configure the Enterprise Messaging in the S/4HANA On-Premise system in the next Section.

    
## Setup S/4HANA Enterprise Messaging

SAP S/4HANA 2020 On-Premise system provide a really simple way to configure the enterprise messaging when compares with previous version. This time you could create communicaiton channel, BTP Destination, Oauth client, and Oauth profile within several steps.

1. Configure the Enterprise Messaging Communication Channel in S/4HANA On-Premise System.

    Log into your S/4HANA On-Premise system, open the **Channel Configuration** page via T-Code **/IWXBE/CONFIG** (sometimes **/n/IWXBE/CONFIG** works as well), click the **via Service Key** button
    
    ![Capture](https://github.wdf.sap.corp/storage/user/105079/files/5c4da7c9-e862-45dc-8026-526e36d35bc7)
    
    ![8e895098-5b41-40c2-9f36-f997801d57c7](https://github.wdf.sap.corp/storage/user/105079/files/de10d8a3-39a2-41a5-a0b3-2d2ce5b10f4a)
    
    In the **Create Channel Configuration** pop up window, give a proper name of the channel, then paste the **Event Mesh service key json credentials** we obtained in the precious section in the **Service Key** text area, then click the **Save** button. 
    
    > ### Note
    > - Once you click save button, the communication channel from S/4HANA to your BTP Subaccount will be created
    > - the RFC Destination, OAuth client, and OAuth profile will be created automatically as well. 
    
    ![tempsnip](https://github.wdf.sap.corp/storage/user/105079/files/0edb791e-c341-4d8c-a0d3-23ac8b82c99c)
    
    Select the channel just created, then clcik the **Check connectiom** button. If you are seeing the message as "Connection test for channel successful", then you have created the communication channel from S/4HANA On-Premise system to your BTP Subaccout successfully.
    
    ![tempsnip](https://github.wdf.sap.corp/storage/user/105079/files/74239f1a-3137-41a9-b729-a13779277c81)
    
    Avtivate the communication channel by clicking the **Activate <-> Deactivate** button. 
    
2. Configure the Business Partner Create and Change Topic for the communication channel.

    Still in the Channel Configuration screen, select your channel and then click the **Outbound Bindings** button.
    
    ![tempsnip](https://github.wdf.sap.corp/storage/user/105079/files/16f15c98-9087-40bf-8042-6d36e8bb5d43)
    
    In the **Outbound Binding Configuration** screen, select your channel, click the **Create New Topic Binding** button, and then bind business partner create and change event topic. After this you will see a message like "New outbound binding for channel created".
    
    Now we have configured enterprise messaging on both SAP Event Mesh and S/4HANA On-Premise System successfully. Now lets test the setup for a little bit.
    
## Testing Enterprise Messaging From End-To-End

1. From the begining, there is no message exist in the message queue. Lets testing by creating a new business partner in the SAP S/4HANA On-Premise system.

    ![tempsnip](https://github.wdf.sap.corp/storage/user/105079/files/7ec41bba-f63f-470d-9b60-1fe94657d788)
    
2. In the S/4HANA On-Premise System, use T-Code **BP** to open the Maintain Business Partner screen. Lets create a new business partner by clicking the **Create Person** button.

    ![tempsnip](https://github.wdf.sap.corp/storage/user/105079/files/9abda249-b255-46f5-9e24-26845a5231b7)

3. Enter proper Name and Standard Address data in the Create Person Page, then click Save button. After that, we could see that the new Business Partner, which ID is 1000615 was created successfully.

    ![Capture](https://github.wdf.sap.corp/storage/user/105079/files/9a177263-f871-4857-84a3-5a1ffc51ccb6)

4. Lets go back to the Event Mesh Management Console, we could see that there is 1 new message in the message queue. Lets how the message in the message queue looks like.
    
    ![tempsnip](https://github.wdf.sap.corp/storage/user/105079/files/3f1fed7a-835b-407d-8f7a-6361d62498e7)

5. In the Event Mesh management console, click **Test** on the left panel. In the **Consume Messages** area, select the **message queue** and **message client**, then click the **Consume Message** button. 

    ![tempsnip](https://github.wdf.sap.corp/storage/user/105079/files/216074c4-50e5-41bb-8abf-5ac90f4c40e1)

6. Once you click the Consume Message button, you could see the message body, whcih contains a business partner ID 1000615, displaying in the Message Data area, then this means that the enterprise messaing works from end-to-end. Cheers!
    
    ![tempsnip](https://github.wdf.sap.corp/storage/user/105079/files/16c1a80b-c6b0-48d7-8a90-2bebbc5ef5e5)
