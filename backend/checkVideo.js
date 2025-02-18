const Video = require('./models/Video');
const fs = require('fs');
const ffmpeg = require('fluent-ffmpeg');
const path = require('path');

const checkAndCreateVideos = async () => {
  try {
    const videoCount = await Video.countDocuments();
    if (videoCount < 10) {
      const videoFiles = [];
      const imageFiles = [];
      // Scan the uploads folder
      const files = fs.readdirSync(path.join(__dirname, 'uploads'));

      files.forEach(file => {
        if (file.endsWith('.mp4')) {
          videoFiles.push(file);
        }
        if (file.endsWith('.jpg')) {
          imageFiles.push(file);
        }
      });

      const videosToCreate = 10 - videoCount;
      const usedVideos = new Set(); // Track used video files
      const usedImages = new Set(); // Track used image files

      for (let i = 0; i < videosToCreate; i++) {
        let videoFile;
        do {
          videoFile = videoFiles[Math.floor(Math.random() * videoFiles.length)];
        } while (usedVideos.has(videoFile)); // Ensure no duplicate videos
        usedVideos.add(videoFile); // Add the selected video to the used set

        let imageFile;
        do {
          imageFile = imageFiles[Math.floor(Math.random() * imageFiles.length)];
        } while (usedImages.has(imageFile)); // Ensure no duplicate images
        usedImages.add(imageFile); // Add the selected image to the used set

        // Create a new video with unique paths and images
        const vid = await Video.findOne({ title: `Sample Video ${i + 1}` });
        if (vid == null) {
          await Video.create({
            title: `Sample Video ${i + 1}`,
            channel: `Sample Channel ${i + 1}`,
            path: `uploads/${videoFile}`,
            image: `uploads/${imageFile}`,
            views: i,
            likes: i,
            dislikes: i,
            comments: [
              {
                author: `User${i + 1}`,
                comment: 'This is a sample comment'
              }
            ]
          });
        }
      }
      console.log(`Created ${videosToCreate} new videos`);
    } else {
      console.log('There are already 10 or more videos in the collection');
    }
  } catch (err) {
    console.error('Error checking or creating videos:', err);
  }
};

module.exports = checkAndCreateVideos;