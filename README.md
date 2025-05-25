# Project Overview

Delifin is a cloud-based business management platform designed for the logistics industry. It streamlines:
*Driver onboarding and profile management
*Multi-file PDF payslip processing and payroll calculations
*JWT-based authentication with role protection
*Historical data tracking and upload visibility

I developed this project end-to-end to apply and showcase skills in full-stack software development, database design, and AWS-based cloud deployment.

Tech Stack: React, Spring Boot, MySQL
Deployment: AWS S3, AWS Elastic Beanstalk, Amazon RDS, IAM, CloudWatch

<img width="1470" alt="Screenshot 2025-05-08 at 6 11 47 PM" src="https://github.com/user-attachments/assets/16e6221d-e093-4612-ad8d-7aed1b5d359b" />

## Architecture Overview

The system is a typical three-tier web application:
*Frontend (React) hosted on S3 static website
*Backend API (Spring Boot) hosted on Elastic Beanstalk
*Database (MySQL) hosted on Amazon RDS, with credentials passed to the backend via environment variables
![diagram](https://github.com/user-attachments/assets/ad799d32-3bf7-4698-b87d-3c6675bc0b0a)

## Frontend Hosting — Amazon S3

The React frontend was built using npm run build and deployed to an S3 bucket with static website hosting enabled.

Key configurations:
*index.html is used as both the index and error document to support React Router SPA routing
*Public read access is granted via a bucket policy
*CORS configured to allow calls to the backend hosted on a separate domain

URL: http://delifin-frontend.s3-website-us-east-1.amazonaws.com/
<img width="1470" alt="Screenshot 2025-05-08 at 4 46 13 PM" src="https://github.com/user-attachments/assets/b2e2259f-ecb4-4b22-a5e7-02cac084bea4" />

## Backend Deployment — AWS Elastic Beanstalk

The backend is a Spring Boot application packaged into a JAR and deployed using AWS Elastic Beanstalk.

Instead of coupling it with a managed RDS service via Elastic Beanstalk, I created and managed an external RDS MySQL instance. The connection is handled using a custom Java class (MySQLConfig.java) that reads credentials and DB URLs from environment variables configured in the EB environment.

<img width="1470" alt="Screenshot 2025-05-08 at 4 43 55 PM" src="https://github.com/user-attachments/assets/5076e3f1-ef81-4bbd-aaaa-a46ab01bbbd9" />

## Database — Amazon RDS (MySQL)

The backend connects to a standalone RDS MySQL instance using JDBC. I manually provisioned the RDS instance to decouple it from Elastic Beanstalk for easier control over security groups and scalability.

Tables include:
*users (username, password, firstName, lastName)
*drivers (name, contact info, payroll details)
*payslip_uploads (date, file info, parsed results)

Security Setup:
*Only the EB environment’s security group can access the DB via port 3306
*Environment variables (DB_URL, DB_USERNAME, DB_PASSWORD) are used instead of hardcoding credentials
<img width="1470" alt="Screenshot 2025-05-08 at 4 45 53 PM" src="https://github.com/user-attachments/assets/9620bc31-94f3-4de9-b2b1-b93da45970ed" />

## JWT Authentication and Protected Routes

Delifin uses Spring Boot to generate JWT tokens upon login. These tokens are stored in browser cookies and validated for every protected route.

Key Features:
*Tokens are set in cookies using document.cookie
*React’s <ProtectedRoute /> checks for cookie presence and token expiry
*Unauthorized users are redirected to the login page
<img width="1470" alt="Screenshot 2025-05-08 at 4 59 16 PM" src="https://github.com/user-attachments/assets/3b1abd67-0410-4135-a8ca-039661bed031" />
<img width="1470" alt="Screenshot 2025-05-08 at 6 30 26 PM" src="https://github.com/user-attachments/assets/0382d9e2-7630-4b4c-8772-192544669315" />

## Payslip Upload Module

This module allows admins to upload multiple PDF payslips at once. The backend:
*Parses the PDFs
*Extracts structured data (name, earnings, deductions)
*Associates the data with the correct driver in the database
*Emails the payslips to the drivers
*Sends confirmation or error messages back to the frontend
<img width="1470" alt="Screenshot 2025-05-08 at 6 45 21 PM" src="https://github.com/user-attachments/assets/10112562-d86b-4a53-95ca-5a5715076ffd" />

## Monitoring — AWS CloudWatch

Elastic Beanstalk logs are configured to stream to CloudWatch for easier log inspection and error tracking. This helps with:
*Debugging application issues
*Monitoring traffic and performance
*Alerting based on application health

<img width="1470" alt="Screenshot 2025-05-08 at 6 51 44 PM" src="https://github.com/user-attachments/assets/ba536433-ac06-44e8-9efe-b5b3e0d4d44c" />

## IAM Roles and Security
*Secured DB access using private security groups and IP restrictions

<img width="1470" alt="Screenshot 2025-05-08 at 7 01 37 PM" src="https://github.com/user-attachments/assets/52c20872-cf90-456d-a4e4-22030d3f916b" />


## Skills Demonstrated

Frontend Development:	React, Axios, React Router, Protected Routes
Backend Development:	Spring Boot, JWT, REST API, JDBC
Cloud Deployment:	S3 Static Hosting, Elastic Beanstalk, RDS
Infrastructure:	IAM, Security Groups, Environment Variables
Monitoring:	AWS CloudWatch, EB log streaming
Security:	JWT Auth, CORS, Cookie Management, IAM


Improvements Planned
*Move to Infrastructure as Code (IaC) with Terraform or AWS CDK
*Integrate GitHub Actions for CI/CD pipeline
*Add email notifications using Amazon SES

Final Thoughts

Delifin demonstrates how a cloud-native web application can be built and deployed using industry-standard tools and AWS services. The project challenged me to work across the stack — from frontend UX design to backend security, and from deployment pipelines to infrastructure provisioning.

This hands-on experience taught me how to think like a developer, an architect, and an operations engineer — all in one.
