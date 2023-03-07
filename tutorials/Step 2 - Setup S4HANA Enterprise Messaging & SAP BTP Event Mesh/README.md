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

    ![1](https://user-images.githubusercontent.com/29527722/223290548-7d038462-bafb-481c-a0cc-6e866896214c.png)

    In the **New Service Key** page, give the proper name of the service key, leave everything as blank, then click the **Create** button.
    
    ![2](https://user-images.githubusercontent.com/29527722/223290568-4221258a-9ce0-45dc-9f26-feec44b856c4.png)
    
    Once the service key created, **note down the service key json credentials** since we will need it to configure the enterprise messaging in the S/4HANA On-Premise System later.
    
3. Go back to the BTP subaccount overview page, click the **Instances and Subscription** on the left panel, then click the **Create** button in the right upper side, to create a Event Mesh Standard subscription. 

    ![3](https://user-images.githubusercontent.com/29527722/223290587-d81f7d08-45d0-4f9c-9e03-ea55f2cff0e7.png)
    
    > ### Note
    > - Remember to assign **Enterprise Messaging Subscription Administrator** role collection to your user so that you could log into the Event Mesh Mangement Console
    > - Please read [this documentation](https://help.sap.com/docs/SAP_EM/bf82e6b26456494cbdd197057c09979f/637d331010e54a2999e2f023d2de1130.html) for the steps of assigning role to the user via SAP Business Technology Platform

4. Once the Event Mesh standard subscription creating successfully, the Event Mesh subscription will be listing under the **Subscriptions** table on the BTP Subaccount. Click the **Go To Application** icon so that we could log into the Event Mesh management console and see the **messaging client** we just created in the previous step.

    ![4](https://user-images.githubusercontent.com/29527722/223290609-205f29c0-0e0f-4d01-96d2-6d14f5d7c49a.png)
    
    ![5](https://user-images.githubusercontent.com/29527722/223290626-c6b114f5-f5a4-42dd-bcf5-1534f75c3c7d.png)

5. In the Event Mesh management console, click the **messaging client** we just created, select **Queues** tile and click the **Create Queue** button to create a message queue in the messaging client.

    ![6](https://user-images.githubusercontent.com/29527722/223290643-88a070f1-fed0-4254-84a3-38356094c2fd.png)
    
    In the **Create a New Queue** pop up window, give a proper name for the message queue, select **NON_EXCLUSIVE** as **Access Type**, and then click **Create** button to create the message queue. After this you will see the message queue created successfully and listing under the Queues tile.
    
    ![7](https://user-images.githubusercontent.com/29527722/223290666-afe1d931-d6eb-49ed-8c44-67e3c7207fc1.png)
    
    Now is the time to configure the message queue subscription. In order to receive S/4HANA On-Premise Business partner create/change event message, we need to let the message queue subscribe on the S/4HANA On-Premise business partner event topic.
    
    > ### Note
    > - Please find the S/4HANA On-Premise system business partner event topic name format in [SAP API Hub](https://api.sap.com/event/SAPS4HANABusinessEvents_BusinessPartnerEvents/resource).
    
    Click **Actions** button of the message queue, then select **Queue Subscriptions** to manage messae queue's subscription.
    
    ![8](https://user-images.githubusercontent.com/29527722/223290696-e28d9470-1e0c-43c0-81a7-dd8eecf1e1e6.png)

    In the Queue Subscription pop up window, copy and paste the S/4HANA On-Premise Business Partner create and change event topic name in the **Topic** textbox.
    
    > ### Note
    > - If you are using **SAP S/4HANA On-Premise 2020** System, the format of the input topic name should be **namespace/ce/topicName**
    > - Otherwise, the format of the input topic name should be **namespace/topicName**
    
    ![9](https://user-images.githubusercontent.com/29527722/223290726-9a97b36f-2977-457e-a77e-28d7faa8fe1d.png)

Above are all the steps to configure the SAP Event Mesh and Event Mesh message queue. We will show how to configure the Enterprise Messaging in the S/4HANA On-Premise system in the next Section.

    
## Setup S/4HANA Enterprise Messaging

SAP S/4HANA 2020 On-Premise system provide a really simple way to configure the enterprise messaging when compares with previous version. This time you could create communicaiton channel, BTP Destination, Oauth client, and Oauth profile within several steps.

1. Configure the Enterprise Messaging Communication Channel in S/4HANA On-Premise System.

    Log into your S/4HANA On-Premise system, open the **Channel Configuration** page via T-Code **/IWXBE/CONFIG** (sometimes **/n/IWXBE/CONFIG** works as well), click the **via Service Key** button
    
    ![1](https://user-images.githubusercontent.com/29527722/223290976-a5e3b8ce-0541-4a59-9eda-0921d8e11ea8.png)
    
    ![2](https://user-images.githubusercontent.com/29527722/223290992-d8b492f8-c8b2-4ed2-8189-66e87a5f5a35.png)
    
    In the **Create Channel Configuration** pop up window, give a proper name of the channel, then paste the **Event Mesh service key json credentials** we obtained in the precious section in the **Service Key** text area, then click the **Save** button. 
    
    > ### Note
    > - Once you click save button, the communication channel from S/4HANA to your BTP Subaccount will be created
    > - the RFC Destination, OAuth client, and OAuth profile will be created automatically as well. 
    
    ![3](https://user-images.githubusercontent.com/29527722/223291320-c6896222-51bb-4f7b-be80-11b0b1065c8e.png)
    
    Select the channel just created, then clcik the **Check connectiom** button. If you are seeing the message as "Connection test for channel successful", then you have created the communication channel from S/4HANA On-Premise system to your BTP Subaccout successfully.
    
    ![5](https://user-images.githubusercontent.com/29527722/223291358-263b21af-c9b9-4b78-a986-9e03b2d00f14.png)
    
    Avtivate the communication channel by clicking the **Activate <-> Deactivate** button. 
    
2. Configure the Business Partner Create and Change Topic for the communication channel.

    Still in the Channel Configuration screen, select your channel and then click the **Outbound Bindings** button.
    
    ![6](https://user-images.githubusercontent.com/29527722/223291175-3f7b04e0-b55e-477a-bfc3-a4a7cb887778.png)
    
    In the **Outbound Binding Configuration** screen, select your channel, click the **Create New Topic Binding** button, and then bind business partner create and change event topic. After this you will see a message like "New outbound binding for channel created".
    
    Now we have configured enterprise messaging on both SAP Event Mesh and S/4HANA On-Premise System successfully. Now lets test the setup for a little bit.
    
## Testing Enterprise Messaging From End-To-End

1. From the begining, there is no message exist in the message queue. Lets testing by creating a new business partner in the SAP S/4HANA On-Premise system.

    ![1](https://user-images.githubusercontent.com/29527722/223291556-b90aba15-935f-450e-9ddd-10b974b520ea.png)
    
2. In the S/4HANA On-Premise System, use T-Code **BP** to open the Maintain Business Partner screen. Lets create a new business partner by clicking the **Create Person** button.

    ![2](https://user-images.githubusercontent.com/29527722/223291576-e001ffe5-ea84-47fc-b4dd-1471277198b9.png)

3. Enter proper Name and Standard Address data in the Create Person Page, then click Save button. After that, we could see that the new Business Partner, which ID is 1000615 was created successfully.

    ![3](https://user-images.githubusercontent.com/29527722/223291588-9c76e89c-ea00-43f2-ab75-42496b2a4419.png)

4. Lets go back to the Event Mesh Management Console, we could see that there is 1 new message in the message queue. Lets how the message in the message queue looks like.
    
    ![4](https://user-images.githubusercontent.com/29527722/223291598-06999ada-8848-4c77-bc6f-061280d4f36a.png)

5. In the Event Mesh management console, click **Test** on the left panel. In the **Consume Messages** area, select the **message queue** and **message client**, then click the **Consume Message** button. 

    ![5](https://user-images.githubusercontent.com/29527722/223291611-651fb3d6-3329-4db4-a2b4-1aab5f56aa7c.png)

6. Once you click the Consume Message button, you could see the message body, whcih contains a business partner ID 1000615, displaying in the Message Data area, then this means that the enterprise messaing works from end-to-end. Cheers!
    
    ![6](https://user-images.githubusercontent.com/29527722/223291632-f63a8bfd-69f9-44c2-ae56-9691691070f5.png)
