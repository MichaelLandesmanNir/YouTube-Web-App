# YouTube

Welcome to the our YouTube App! 
This project allows users to search for and view YouTube videos. 
It is built with React and styled-components.

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

![](./public/readmeImages/img1.png)

Search video using the search box:

![](./public/readmeImages/img2.png)

You can login by pressing the Login buttton:

![](./public/readmeImages/img3.png)

Create new account if you dont have one:

![](./public/readmeImages/img4.png)

After login is complete, pressing on the profile button will show your details:

![](./public/readmeImages/img5.png)

You can open a video and share a Comment, Like or Dislike and even download it!

![](./public/readmeImages/img6.png)

Navigate using the side bar:

![](./public/readmeImages/img7.png)

Create new video:

![](./public/readmeImages/img8.png)

![](./public/readmeImages/img9.png)

![](./public/readmeImages/img10.png)

When you uplaod a video, you can edit it's name and even delete it!

![](./public/readmeImages/img11.png)


## Installation

To run this project locally, follow these steps:

- Clone the repository

> git clone https://github.com/YTGroupBiu2/YouTube

- Change to your ip in these two paths

> $YourClonedProject/YouTube\Android\app\src\main\res\xml\network_security_config.xml
> $YourClonedProject/YouTube\Android\app\src\main\java\com\example\newyoutube\Strings.java

- First install MongoDB and connect to local host 

> mongodb://localhost:27017

- Navigate to the backend folder

> cd $YourClonedPrject/backend

- Run the server

> node ./server.js

- Navigate to tcp_server and compile using this command

> g++ -o program.exe server.cpp -lws2_32

- Open the file program.exe that shows up

- To run the clinet, Open new terminal and navigate to the web directory

> cd > cd $YourClonedPrject/Web

- Run the client

> npm start

- The app should now be running on http://localhost:3000.



## About the Project

This project was created by a team of three members: Michael, Roni, and Lior. Each team member was responsible for different aspects of the application, ensuring a collaborative and comprehensive development process.

### Team Members

- **Michael**: Responsible for developing the video page. Michael implemented the video player, video playback, and designed the layout for displaying video details and related videos.

- **Roni**: Handled the registration and login pages. Roni implemented he login and registration functionalities, ensuring a smooth and safe user experience.

- **Lior**: Focused on the homepage. Lior developed the main interface of the application, including the search bar, featured videos, and overall layout of the homepage, ensuring it is user-friendly and visually appealing.
