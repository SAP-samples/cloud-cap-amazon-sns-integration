# Welcome to the SAP CAP & Amazon Simple Notification Service Integration App

## Description
This is an example project showcases how to integrate SAP Cloud Application Programming Model (CAP) with Amazone Simple Notification Service (SNS), to build a S/4HANA business process extension project, and send out email notification if it is necessary by using Amazon Simple Notification Service.

This project showcases:

- Develop S/4HANA business process extension App by using [SAP Cloud Application Programming Model(CAP)](https://cap.cloud.sap/docs/) and [Fiori Element UI](https://ui5.sap.com/#/topic/03265b0408e2432c9571d6b3feb6b1fd)
- Consume S/4HANA On-Premise System Event by using [SAP Event Mesh](https://help.sap.com/viewer/bf82e6b26456494cbdd197057c09979f/Cloud/en-US/df532e8735eb4322b00bfc7e42f84e8d.html)
- Consume S/4AHANA On-Premise System OData API From SAP Business Technology Platform (Cloud Env)
- Send Out Email Notification by leveraging [Amazon Simple Notification Service (SNS)](https://docs.aws.amazon.com/sns/latest/dg/welcome.html)

## Business Scenario
There is a third-party firm/team responsible for validating all newly created or changed business partners data in S/4HANA system for a company. This third-party firm/team will do the validation through a standalone S/4HANA business process extension App deployed on SAP BTP platform, instead of getting access into the S/4HANA system. 

In this standalone extension App, the validator could review the business partner's data, perform updates on the business partner address if necessary, and mark business partner’s validation status as NEW, IN PROCESS, VALID, and INVALID based on the condition. The validators could receive notifications through email whenever there is a business partner's data that needs to be validated. Once the business partner data are verified, the business partner get activated in the S/4HANA System.

## Architecture

### Solution Architecture Diagram
![43de0ae1-0aa6-4b3b-910f-8c3459728d24](https://user-images.githubusercontent.com/29527722/223288208-4d04f097-4d84-4e05-a069-f7e7512026ed.png)
- The Business Partner Validation application developed using SAP Cloud Application Programming Model (Java), and deployed on the SAP Business Technology Platform.
- S/4HANA On-Premise System Sends Message to SAP Event Mesh through Enterprise Messaging Mechanism on Every Business Partner Create/Change Event
- Business Partner Validation Application Consumes Message From SAP Event Mesh Message Queue, Persists Business Partner Data Into HANA Cloud DataBase
- Business Partner Validation Application Sends Out Emial Notification to Validator Through Amazon Simple Notification Service
- Authorized Validtor Log Into Business Partner Validation Application Through Fiori Element UI
- Authorized Validtor Review/Update Business Partner Address Data, and Update Business Partner Verfication Code In Fiori Element UI
- Business Partner Validation application Consume S/4HANA On-Premise Business Partner(A2X) OData API To Update/Release Business Partner through SAP Connectivity Service and Cloud Connector.

## Requirement

The required systems and components are:

- SAP Business Technology Platform Global Account & SubAccount
- SAP S/4HANA On-Premise System
- SAP Cloud Connector
- AWS Account

Entitlements required in your SAP Business Technology Platform Account:

| Service                     | Plan             | Number of instances |
| --------------------------- | ---------------- | ------------------- |
| Authorization and Trust Management Service      | application    | 1                   |
| SAP HANA Schemas & HDI Containers      | hdi-shared    | 1                   |
| Event Mesh     | 	default    | 1                   |
| Connectivity Service      | lite    | 1                   |
| Destination Service      | lite    | 1                   |

Subscriptions required in your SAP Business Technology Platform Account:

| Subscription               | Plan                                                   |
| -------------------------- | ------------------------------------------------------ |
| SAP Business Application Studio|  Standard (Application)                                |
| Event Mesh|  Standard (Application)                                |
| Launchpad Service|  Standard (Application)                                |

## Setup and Configuration

#### Step 1. [Setup Amazon Simple Notification Service](./tutorials/Step%201%20-%20Setup%20Amazon%20Simple%20Notification%20Service/README.md)

#### Step 2. [Setup S/4HANA Enterprise Messaging & SAP BTP Event Mesh](/tutorials/Step%202%20-%20Setup%20S4HANA%20Enterprise%20Messaging%20%26%20SAP%20BTP%20Event%20Mesh/README.md)

#### Step 3. [Setup Connectivity From SAP BTP To SAP S/4HANA On-Premise System](/tutorials/Step%203%20-%20Setup%20Connectivity%20From%20SAP%20BTP%20To%20SAP%20S4HANA%20On-Premise%20System/README.md)

#### Step 4. [Build & Deploy the Business Partner Validation Application on SAP BTP](/tutorials/Step%204%20-%20Build%20%26%20Deploy%20the%20Business%20Partner%20Validation%20Application%20on%20SAP%20BTP/README.md)

#### Step 5. [S/4HANA Business Partner Validation Application Walk Through](/tutorials/Step%205.%20S4HANA%20Business%20Partner%20Validation%20Application%20Walk%20Through/README.md)

## How to obtain support
[Create an issue](https://github.com/SAP-samples/<repository-name>/issues) in this repository if you find a bug or have questions about the content.

For additional support, [ask a question in SAP Community](https://answers.sap.com/questions/ask.html).

## License
Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved. This project is licensed under the Apache Software License, version 2.0 except as noted otherwise in the [LICENSE](LICENSE) file.
