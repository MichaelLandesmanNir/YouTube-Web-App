const videoService = require('../services/videoService');
const Video = require('../models/Video');
const net = require('net');

const RECOMMENDATION_SERVER_PORT = 8080;
const RECOMMENDATION_SERVER_HOST = 'localhost';
const TIMEOUT = 5000; // 5 seconds

function communicateWithRecommendationServer(userId, videoId, action) {
  console.log(`Starting communication with recommendation server: Action=${action}, UserId=${userId}, VideoId=${videoId}`);
  return new Promise((resolve, reject) => {
      const client = new net.Socket();
      let data = '';

      client.setTimeout(TIMEOUT);

      client.connect(RECOMMENDATION_SERVER_PORT, RECOMMENDATION_SERVER_HOST, () => {
          console.log(`Connected to recommendation server for ${action}`);
          const message = `${action}:${userId}:${videoId}\n`;
          console.log('Sending message:', message);
          client.write(message);
      });

      client.on('data', (chunk) => {
          console.log('Received chunk:', chunk.toString());
          data += chunk.toString();
          if (data.includes('\n')) {
              console.log('Complete message received, ending connection');
              client.end();
          }
      });

      client.on('end', () => {
          console.log('Connection ended. Full data received:', data);
          const lines = data.trim().split('\n');
          const lastLine = lines[lines.length - 1];
          if (lastLine.startsWith('RECOMMENDATIONS:')) {
              const recommendations = lastLine.substring(16).split(',').filter(Boolean);
              console.log('Parsed recommendations:', recommendations);
              resolve(recommendations);
          } else if (lastLine === 'OK') {
              resolve([]);
          } else {
              console.error('Invalid response format from recommendation server');
              reject(new Error('Invalid response from recommendation server'));
          }
      });

      client.on('error', (err) => {
          console.error('Error communicating with recommendation server:', err.message);
          reject(err);
      });

      client.on('timeout', () => {
          console.error('Connection timed out');
          client.destroy();
          reject(new Error('Connection timed out'));
      });
  });
}

function getRecommendations(userId, videoId) {
  console.log(`Getting recommendations for UserId=${userId}, VideoId=${videoId}`);
  return communicateWithRecommendationServer(userId, videoId, 'GET_RECOMMENDATIONS')
    .then(recommendations => {
      console.log('Received recommendations:', recommendations);
      return recommendations;
    })
    .catch(error => {
      console.error('Error getting recommendations:', error);
      throw error;
    });
}

function sendWatchNotification(userId, videoId) {
  console.log(`Sending watch notification for UserId=${userId}, VideoId=${videoId}`);
  return communicateWithRecommendationServer(userId, videoId, 'WATCH_NOTIFICATION')
    .then(() => {
      console.log('Watch notification sent successfully');
    })
    .catch(error => {
      console.error('Error sending watch notification:', error);
      throw error;
    });
}

exports.getVideo = async (req, res) => {
  try {
    console.log("Fetching video with ID:", req.params.videoId);
    const video = await videoService.getVideo(req.params.videoId);
    console.log("Video fetched:", video ? video._id : "Not found");

    const userId = req.user ? req.user.id : "anonymous";
    console.log("Getting recommendations for user:", userId, "and video:", req.params.videoId);

    let recommendations = [];
    try {
      recommendations = await getRecommendations(userId, req.params.videoId);
      console.log("Recommendations received:", recommendations);
    } catch (recError) {
      console.error("Error getting recommendations:", recError.message);
    }

    res.json({ video, recommendations });
  } catch (error) {
    console.error('Error in getVideo:', error);
    res.status(500).send({ message: 'Error getting video', error: error.message });
  }
};

exports.addView = async (req, res) => {
  const { videoId } = req.params;
  const userId = req.user ? req.user.id : "anonymous";
  console.log(`Adding view for VideoId=${videoId}, UserId=${userId}`);
  try {
    const updatedVideo = await videoService.addView(videoId);
    if (!updatedVideo) {
      console.log('Video not found');
      return res.status(404).json({ message: 'Video not found' });
    }
    console.log('View added successfully');

    let recommendations = [];
    try {
      console.log('Sending watch notification');
      await sendWatchNotification(userId, videoId);
      console.log('Getting recommendations after watch');
      recommendations = await getRecommendations(userId, videoId);
      console.log('Recommendations received:', recommendations);
    } catch (recError) {
      console.error("Error with recommendation server:", recError.message);
    }

    res.json({ updatedVideo, recommendations });
  } catch (error) {
    console.error('Error adding view to video:', error);
    res.status(500).send({ message: 'Error adding view to video', error: error.message });
  }
};


exports.createVideo = async (req, res) => {
  try {
    const { title, channel, details, likes, views, dislikes, comments, date } = req.body;

    let parsedComments = [];
    if (comments) {
      try {
        parsedComments = JSON.parse(comments);
      } catch (error) {
        return res.status(400).send({ message: 'Invalid comments format' });
      }
    }

    const newVideo = new Video({
      title,
      channel,
      details,
      path: req.files['path'][0].path,
      image: req.files['image'][0].path,
      likes,
      views,
      dislikes,
      comments: parsedComments,
      date
    });

    await newVideo.save();
    res.status(201).json(newVideo);
  } catch (error) {
    console.error('Error:', error);
    res.status(500).send({ message: 'Error creating video', error: error.message });
  }
};

exports.getAllVideos = async(req, res) => {
  try {
    const videos = await videoService.getAllVideos();
    res.json(videos);
  } catch(err) {
    res.status(500).send({message: 'error getting videos', error: err.message});
  }
}



exports.deleteComment = async(req, res) => {
  try {
    const deleteComment = await videoService.deleteComment(req.params.videoId, req.body.commentText, req.body.author);
    res.json(deleteComment);
  } catch (error) {
    res.status(500).send({ message: 'Error deleting comment', error: error.message });
  }
};

exports.editComment = async (req, res) => {
  const { author, originalComment, newCommentText } = req.body;
  const {videoId} = req.params;
  try {
    const video = await Video.findById(videoId);
    if (!video) {
      return res.status(404).json({ success: false, message: 'Video not found' });
    }

    const comment = video.comments.find(comment => comment.comment === originalComment && comment.author === author);
    if (!comment) {
      return res.status(404).json({ success: false, message: 'Comment not found or not authorized to edit this comment' });
    }

    comment.comment = newCommentText; // Update the comment text
    await video.save(); // Save the video document with the updated comment

    return res.status(200).json({ success: true, message: 'Comment edited successfully' });
  } catch (error) {
    console.error('Error editing comment:', error);
    return res.status(500).json({ success: false, message: error.message });
  }
};

exports.getVideos = async (req, res) => {
  try {
    const videos = await videoService.fetchVideos();
    res.json(videos);
  } catch (error) {
    res.status(500).send({ message: 'Error retrieving videos', error: error.message });
  }
};

exports.addComment = async (req, res) => {
  const { videoId } = req.params;
  const { author, comment } = req.body;
  console.log(author, comment)

  try {
    const updatedVideo = await videoService.addComment(videoId, author, comment);
    res.json(updatedVideo);
  } catch (error) {
    res.status(500).send({ message: 'Error adding comment', error: error.message });
  }
};

exports.likeVideo = async (req, res) => {
  const { videoId } = req.params;

  try {
    const updatedVideo = await videoService.likeVideo(videoId);
    res.json(updatedVideo);
  } catch (error) {
    res.status(500).send({ message: 'Error liking video', error: error.message });
  }
};

exports.dislikeVideo = async (req, res) => {
  const { videoId } = req.params;

  try {
    const updatedVideo = await videoService.dislikeVideo(videoId);
    res.json(updatedVideo);
  } catch (error) {
    res.status(500).send({ message: 'Error disliking video', error: error.message });
  }
};

exports.undislikeVideo = async (req, res) => {
  const { videoId } = req.params;

  try {
    const updatedVideo = await videoService.undislikeVideo(videoId);
    res.json(updatedVideo);
  } catch (error) {
    res.status(500).send({ message: 'Error removing dislike from video', error: error.message });
  }
};

exports.unlikeVideo = async (req, res) => {
  const { videoId } = req.params;

  try {
    const updatedVideo = await videoService.unlikeVideo(videoId);
    res.json(updatedVideo);
  } catch (error) {
    res.status(500).send({ message: 'Error removing like from video', error: error.message });
  }
};


exports.editVideo = async(req, res) => {
  const { videoId } = req.params;
  const {newName} = req.body;

  try {
    const updatedVideo = await videoService.editVideo(videoId, newName);
    res.json(updatedVideo);
  } catch (error) {
    res.status(500).send({ message: 'Error removing like from video', error: error.message });
  }
}

exports.deleteVideo = async(req, res) => {
  const { videoId } = req.params;



  try {
    const updatedVideo = await videoService.deleteVideo(videoId);
    res.json(updatedVideo);
  } catch (error) {
    res.status(500).send({ message: 'Error removing like from video', error: error.message });
  }
}