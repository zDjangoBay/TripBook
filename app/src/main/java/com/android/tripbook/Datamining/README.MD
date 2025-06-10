# Data mining Modules
This document is intended to give you instructions on how to run the data mining project on your computers.


## Table of Content 

1. [Pre-requisite](#Prerequisiste)
  * [What is the datamining intended to do?](#What-is-the-purpose-of-the-datamining-service)
  * [Required to test it on your computer](#test-required)
2. [Step-by-Step guide to run it](#run-it-on-your-own)


  --- 

  ## 🎯 Pre-requisite

  ### What is the datamining intended to do ? 

  The datamining service is one service of the Tripbook application . The system will mostly have a client-side architecture .In that Tripbook system , this module is the one supposed to support the other modules with data for them to provide content to the User Interface.
  The modules it has to communicate with are : `trip catalog`,`trip scheduling`,`company catalog`,`comments`,`posts` and `user profile`. Bluntly this service is supposed to store data and process them --to an extent-- .


  ### Required to test it on your computer 

  Since this module is server-side , you cannot run it using your classic android studio to see a mobile app. 
  You need to install :
  ```
    $ docker desktop 
    $ intellij 
    $ postman(or another thunder , karate ...)
  ```
 

  ## ⚙️ Step-by-Step guide to run it



  ### First go on my `datamining-zingui-fred-mike-ICTU20233585-post-reservations-comments-companycatalog-for-datamining` branch 

  ![First step ](https://github.com/zDjangoBay/TripBook/blob/datamining-zingui-fred-mike-ICTU20233585-post-reservations-comments-companycatalog-for-datamining/app/src/main/java/com/android/tripbook/Datamining/tutrl/Screenshot%202025-06-09%20at%2018.04.15.png)



  ### Second you have to go in the `data mining folder` and open the `Data mining intellig copy.cpgz` file 

![Second Step ](https://github.com/zDjangoBay/TripBook/blob/datamining-zingui-fred-mike-ICTU20233585-post-reservations-comments-companycatalog-for-datamining/app/src/main/java/com/android/tripbook/Datamining/tutrl/Screenshot%202025-06-09%20at%2018.06.18.png)


  ### Third step , on the right side , press download for the archive 

    ![Third Step](https://github.com/zDjangoBay/TripBook/blob/datamining-zingui-fred-mike-ICTU20233585-post-reservations-comments-companycatalog-for-datamining/app/src/main/java/com/android/tripbook/Datamining/tutrl/Screenshot%202025-06-09%20at%2018.06.38.png)


  ### Fourth step , after downloading it , `extract it` in the location of your choice 

    ![Fourth Step](https://github.com/zDjangoBay/TripBook/blob/datamining-zingui-fred-mike-ICTU20233585-post-reservations-comments-companycatalog-for-datamining/app/src/main/java/com/android/tripbook/Datamining/tutrl/Screenshot%202025-06-09%20at%2018.07.18.png)


  ### Fifth Step , Open your intellij application and select `Open` for a new project 

    ![Fifth Step](https://github.com/zDjangoBay/TripBook/blob/datamining-zingui-fred-mike-ICTU20233585-post-reservations-comments-companycatalog-for-datamining/app/src/main/java/com/android/tripbook/Datamining/tutrl/Screenshot%202025-06-09%20at%2018.08.06.png)


  ### Sixth step , `Open` the extracted folder as a new project 

    ![Sixth Step 1](https://github.com/zDjangoBay/TripBook/blob/datamining-zingui-fred-mike-ICTU20233585-post-reservations-comments-companycatalog-for-datamining/app/src/main/java/com/android/tripbook/Datamining/tutrl/Screenshot%202025-06-09%20at%2018.08.22.png)

    ![Sixth Step 2](https://github.com/zDjangoBay/TripBook/blob/datamining-zingui-fred-mike-ICTU20233585-post-reservations-comments-companycatalog-for-datamining/app/src/main/java/com/android/tripbook/Datamining/tutrl/Screenshot%202025-06-09%20at%2018.08.49.png)

  ### Seventh step , `Wait for Gradle` 

    ![Seventh Step](https://github.com/zDjangoBay/TripBook/blob/datamining-zingui-fred-mike-ICTU20233585-post-reservations-comments-companycatalog-for-datamining/app/src/main/java/com/android/tripbook/Datamining/tutrl/Screenshot%202025-06-09%20at%2018.16.26.png)

    #### Do not forget

* Run your docker desktop 
* Go in the terminal and run 
                    ```bash
                    docker compose up -d 
                      ```

                    
  ### Eighth Step , `Press the magic button ` also known as `run` 

    ![Eigth step](https://github.com/zDjangoBay/TripBook/blob/datamining-zingui-fred-mike-ICTU20233585-post-reservations-comments-companycatalog-for-datamining/app/src/main/java/com/android/tripbook/Datamining/tutrl/Screenshot%202025-06-09%20at%2018.17.22.png)







>* After doing all of this all you have to do is to collect the link of the running application , and use it to test the routes on postman . You can find the different route in the readme file of each module
