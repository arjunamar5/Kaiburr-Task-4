# **Task 4 – CI/CD Pipeline using Jenkins**

### **Objective**

Create a CI/CD pipeline for the Spring Boot application (from Task #1) using **Jenkins**.The pipeline automates:

1.  Building the project (Maven build- to create jar file)
    
2.  Creating and pushing Docker images to DockerHub
---

### 🧩 **Tools & Technologies Used**

*   **Jenkins** (for CI/CD automation)
    
*   **Maven** (for Java project build)
    
*   **Docker** (for containerization)
    
*   **GitHub** (for version control)
    
*   **DockerHub** (for image repository)
    
---
### **Jenkins Setup**

#### **Jenkins Installation**

Installed Jenkins on my **Windows machine** using the .msi installer.

*   During setup, Jenkins runs by default on port 8080.
    
*   Configured Jenkins to run on a different port (8099) to avoid conflicts with the application.
    
---
### **Pipeline Flow**

1.  **Code Checkout** – Jenkins clones the GitHub repository.
    
2.  **Build Stage** – Maven compiles and packages the application into a .jar.
    
3.  **Docker Build Stage** – Builds a Docker image from the Dockerfile.
    
4.  **Docker Push Stage** – Pushes the built image to DockerHub.
    
5.  **Cleanup Stage** – Removes temporary Docker files to save space.
---
## Screenshots
1. Jenkins Home Page
![Jenkins home page](https://github.com/user-attachments/assets/66728ff1-dd17-4217-a519-a52ad46239fe)
---
2. Jenkins Console Output containing successful build of JAR file using Maven
![Console-1](https://github.com/user-attachments/assets/493ec5df-e631-4be1-ba5f-4d8becddd639)

---
![Console-2](https://github.com/user-attachments/assets/aee7f73f-862e-4502-8544-5ba3af418be7)

---
3. Jenkins Console Output containing successful build of Docker image
![Console-3](https://github.com/user-attachments/assets/edbd297c-11c1-4f1f-9c45-718d6eff4f8b)
---
4. Jenkins Console Output containing successful login into Dockerhub
![Console-4](https://github.com/user-attachments/assets/abaeac3f-7816-4fff-80c9-3f6a023baab0)
---
5. Jenkins Console Output, successfully pushed Docker image to DockerHub
![Console-5](https://github.com/user-attachments/assets/d3821d9a-f1a7-4234-a7af-badc851fd1da)

## **Summary**

*   Automated CI/CD pipeline successfully created in Jenkins.
    
*   Application builds automatically, containerized with Docker, and pushed to DockerHub.





