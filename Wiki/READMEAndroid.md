# YouTube

Welcome to our YouTube Android App! 

This project allows users to search for and view YouTube videos on their Android devices. It is built with Android Studio, utilizing Java for development and various Android libraries for a smooth and responsive user experience.


## Features
- Search for videos
- View video details
- Responsive design
- Infinite scrolling for search results
- Play, edit and delete videos
- Comment, Like and download videos
- Dark mode toggle
- Login and Register

### Demo

First you have the main page of the app:
![](../Android/app/src/main/res/raw/readmeImages/img1.png)

Search video using the search box:

![](../Android/app/src/main/res/raw/readmeImages/img2.png)

You can login by pressing the Login button:

![](../Android/app/src/main/res/raw/readmeImages/img3.png)

Create a new account if you don't have one:

![](../Android/app/src/main/res/raw/readmeImages/img4.png)

After login is complete, pressing on the profile button will show your details:

![](../Android/app/src/main/res/raw/readmeImages/img8.png)

You can open a video and share a Comment, Like or Dislike, and even download it!

![](../Android/app/src/main/res/raw/readmeImages/img5.png)

Navigate using the sidebar:

![](../Android/app/src/main/res/raw/readmeImages/img6.png)

Create a new video:

![](../Android/app/src/main/res/raw/readmeImages/img9.jpeg)

![](../Android/app/src/main/res/raw/readmeImages/img10.jpeg)

![](../Android/app/src/main/res/raw/readmeImages/img11.jpeg)

When you upload a video, you can edit its name and even delete it!

![](../Android/app/src/main/res/raw/readmeImages/img12.jpeg)

![](../Android/app/src/main/res/raw/readmeImages/img13.jpeg)



## Installation

To run this project locally, follow these steps:

- Clone the repository

>git clone git clone https://github.com/YTGroupBiu2/YouTube

- Change to your ip in these two paths

> $YourClonedProject/YouTube\Android\app\src\main\res\xml\network_security_config.xml
> $YourClonedProject/YouTube\Android\app\src\main\java\com\example\newyoutube\Strings.java

-First install MongoDB and connect to local host 

> mongodb://localhost:27017

- Navigate to the backend folder

> cd $YourClonedPrject/backend

- Run the server

> node ./server.js

- Navigate to tcp_server and compile using this command

> g++ -o program.exe server.cpp -lws2_32

- Open the file program.exe that shows up

- To run the clinet, Open new terminal and navigate to the Andorid directory

> cd $YourClonedPrject/Android

- Open the project in Android Studio:

Navigate to the project directory and open it in Android Studio.

- Build the project:

Click on Build > Rebuild Project to build the project and resolve any dependencies.

- Run the app:

Click on Run > Run 'app' to start the application on an emulator or connected device.
For emulator, please choose Pixle 5 for best exprience.

## About the Project

This project was created by a team of three members: Michael, Roni, and Lior. Each team member was responsible for different aspects of the application, ensuring a collaborative and comprehensive development process.

### Team Members

- **Michael**: Responsible for developing the video page. Michael implemented the video player, video playback, and designed the layout for displaying video details and related videos.

- **Roni**: Handled the registration and login pages. Roni implemented he login and registration functionalities, ensuring a smooth and safe user experience.

- **Lior**: Focused on the homepage. Lior developed the main interface of the application, including the search bar, featured videos, and overall layout of the homepage, ensuring it is user-friendly and visually appealing.
