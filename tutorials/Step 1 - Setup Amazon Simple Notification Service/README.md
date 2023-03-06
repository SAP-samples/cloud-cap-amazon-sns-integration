# Setup Amazon Simple Notification Service (SNS)

### You will learn
  - How to set up [Amazon SNS Topic](https://docs.aws.amazon.com/sns/latest/dg/sns-create-topic.html)
  - How to grant publish message permission to the IAM programmatic user on Amazon SNS Topic
  
> ### Prerequisites
> - AWS account

## Create Amazon SNS Topic
Amazon Simple Notification Service is a regional service, please create the Amazon SNS Topic in the AWS region which is equals to the region of your BTP subaccount, so that we could minimize the lattency.

1. Open AWS Simple Notification Service Management Console, click **Topics** on the left panel, then click the **Create Topic** button

   ![Capture](https://github.wdf.sap.corp/storage/user/105079/files/cdc64936-84c1-48e5-bd83-2865bb648e1b)
   
   ![tempsnip](https://github.wdf.sap.corp/storage/user/105079/files/0a0aca44-6a4e-41ae-abb8-2c6864436705)

2. Select **Standard** Topic type, give the proper topic name and display name. In the **Access Policy** part, select **Basic** method, check **Only the specified AWS accounts** under the **Define who can publish messages to the topic** section, give your AWS account ID there. Click **Create Topic**

   ![Capture](https://github.wdf.sap.corp/storage/user/105079/files/c0b203ce-b27e-4464-b3d7-789d4d83e8fe)
   
   ![tempsnip](https://github.wdf.sap.corp/storage/user/105079/files/dfc34cf6-d491-4f46-9233-53e09a5d02d6)

3. Goes into the newly created topic, note down the topic **ARN**, click the **Create subscription** button

    ![tempsnip](https://github.wdf.sap.corp/storage/user/105079/files/0131dc5f-94f1-4d40-9118-16224b8348bb)

4. In the Create Subscription page, select **Email** from **Protocol drop down**, give your email address in the Endpoint textbox, then click **Create subscription** button
    
    This step is for end-to-end testing purpose, please give a working email address there so that once there is an business partner waiting for validate in the business application validation app, this email address will receive a email notification send out from Amazon SNS

    ![tempsnip](https://github.wdf.sap.corp/storage/user/105079/files/095bc22b-26a8-4709-9cb6-bdb0655b56c5)
    
    You will receive an subscription confirmation email send out from AWS right after click the Create subscription button. Click the link in the email to confirm the subscription. After that, you will the subscription is confirmed in the Amazon SNS management console. 
    
    ![tempsnip](https://github.wdf.sap.corp/storage/user/105079/files/0ae55dff-78c0-40b2-a710-5330c2170abf)

## Grant publish message permission to the IAM programmatic user on Amazon SNS Topic

In this section, we will need to create a IAM programmatic user and grant only publish message permission to this IAM user. So that we could use this programmatic user's credential in the Business Partner Validation Application to send out Amazon SNS email notification.

In order to grant publish message permission to the IAM programmatic user, the best practice would be 
- Create new IAM identity-based policy with allow publish message action
- Create new IAM user group, attach the policy to the group.
- Create new IAM programmatic user, add IAM programmatic user into the user group.

1. Open Identity and Access Management (IAM) management console, click **Polices** on the left panel, then click **Create policy** button.
    
    ![tempsnip](https://github.wdf.sap.corp/storage/user/105079/files/9dd4cc6d-d8f6-40bb-af9c-537b7df3ebcd)
    
2. In the Create Policy page, select **JSON** and follow below format to create the IAM Policy. Please give your **Topic ARN** in the **Recource** section, and only give **sns:Publish** in the **Action** section.

    ![tempsnip](https://github.wdf.sap.corp/storage/user/105079/files/3d6b3b8e-e2f8-4c34-8b10-d5124161a844)
    
    click next all the way down to the **Review Policy** page, give proper Name and Description, click Create Poliocy button.
    
    ![Capture](https://github.wdf.sap.corp/storage/user/105079/files/80f648ca-1c48-46d0-9388-7d865e3165ac)
    
    Now we create the IAM identity-based policy which only grant publish message permission to the SNS Topic successfully.

3. Go back to the IAM console, click **User groups** on the left panel, click **Create group** button
    
    ![tempsnip](https://github.wdf.sap.corp/storage/user/105079/files/a9421cbc-8c6b-4b35-8489-80457184e795)
    
    In the create user group page, enter the proper user group name, then scrow down to the **Attach permissions policie** section, search and check the **IAM policy** we just created on the last step. Then click Create Group button
    
    ![Capture](https://github.wdf.sap.corp/storage/user/105079/files/c961c176-bfd6-46a5-9c61-da8beb95744e)
    
    ![tempsnip](https://github.wdf.sap.corp/storage/user/105079/files/fe56b168-545e-4ab3-aeda-6cc11a6a0488)
    
    Now we have created a new IAM user group successfully. All the users in this group will have the permission to publish message to the Amazon SNS topic we just created.
    
4. Go back to the IAM console, click  **Users** on the left panel, and click **Add users** button to create a new IAM user.

    ![tempsnip](https://github.wdf.sap.corp/storage/user/105079/files/63a3f583-6226-48eb-b2eb-da2fcb231952)
    
    In the Add user page, give a proper user name, and check the **Access key - Programmatic access** checkbox in the **Select AWS access type**
    
    ![Capture](https://github.wdf.sap.corp/storage/user/105079/files/42fa4345-713d-4e67-9d6f-8b5abc884030)
    
    In the Add user to group section, check the user group we created in previous step, click next button all the way down, and finally click **Create User** button
    
    ![Capture](https://github.wdf.sap.corp/storage/user/105079/files/b90379b0-71c7-47bb-80af-9e6dda4d062a)
    
    Make sure to write down the **Access Key ID** and **Secret access key** of your IAM programmatic user, this is really **important** since this is the **last time to view the Access Key ID and Secret access key of your IAM programmatic user!**
    
    ![tempsnip](https://github.wdf.sap.corp/storage/user/105079/files/aa8e11d3-f18c-4b13-88b7-feed8dac6108)

Now you have finish all the steps to configure Amazon SNS Topic and grant publish message permission. Please **mark down** below information as we will use those infortmation in the code of the Business Partner Validation Application.
- **Amazon SNS Topic ARN**
- **Amazon SNS Topic Region**
- **IAM programmatic user Access Key ID**
- **IAM programmatic user Secret Access Key**


    
