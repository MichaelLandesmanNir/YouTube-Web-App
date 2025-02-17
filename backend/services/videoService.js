const Video = require('../models/Video');

exports.fetchVideos = async () => {
  try {

    const topViewedVideos = await Video.find({}).sort({ views: -1 }).limit(10);

    const randomVideos = await Video.aggregate([{ $sample: { size: 10 } }]);

    const combinedVideos = [...topViewedVideos, ...randomVideos];
 
    const shuffledVideos = combinedVideos.sort(() => Math.random() - 0.5);

    
    const uniqueVideos = [];
    const titles = new Set();
    for (const video of shuffledVideos) {
      if (!titles.has(video.title)) {
        titles.add(video.title);
        uniqueVideos.push(video);
      }
    }

    return uniqueVideos;
  } catch (error) {
    console.error('Error fetching videos:', error);
    throw new Error('Could not fetch videos');
  }
};

exports.getAllVideos = async() => {
  const vids = await Video.find({});
  return vids;
}

exports.getVideo = async (videoId) => {
  const video = await Video.findById(videoId); // Await the database call
  return video;
};

exports.createVideo = async (video) => {
  const newVideo = new Video(video);
  await newVideo.save();
  return newVideo;
};

exports.editComment = async (videoId, author, originalComment, newCommentText) => {
  try {
    const video = await Video.findById(videoId);
    if (!video) {
      throw new Error('Video not found');
    }

    const comment = video.comments.find(comment => comment.comment === originalComment && comment.author === author);
    if (!comment) {
      throw new Error('Comment not found or not authorized to edit this comment');
    }

    comment.comment = newCommentText; // Update the comment text
    await video.save(); // Save the video document with the updated comment

    return { success: true, message: 'Comment edited successfully' };
  } catch (error) {
    console.error('Error editing comment:', error);
    return { success: false, message: error.message };
  }
};

exports.editVideo = async (videoId, newName) => {
  try {
    const video = await Video.findById(videoId);
    if (!video) {
      throw new Error('Video not found');
    }
    
    video.title = newName;
    await video.save();
    
    return { success: true, message: 'Video edited successfully', video };
  } catch (error) {
    console.error('Error editing video:', error);
    return { success: false, message: error.message };
  }
};

// Delete Video Function
exports.deleteVideo = async (videoId) => {
  try {
    const video = await Video.findByIdAndDelete(videoId);
    if (!video) {
      throw new Error('Video not found');
    }
    
    return { success: true, message: 'Video deleted successfully' };
  } catch (error) {
    console.error('Error deleting video:', error);
    return { success: false, message: error.message };
  }
};

exports.deleteComment = async (videoId, commentText, author) => {
  try {
    const video = await Video.findById(videoId);
    if (!video) {
      throw new Error('Video not found');
    }

    const commentIndex = video.comments.findIndex(comment => comment.comment === commentText && comment.author === author);
    if (commentIndex === -1) {
      throw new Error('Comment not found or not authorized to delete this comment');
    }

    video.comments.splice(commentIndex, 1); // Remove the comment from the array
    await video.save(); // Save the video document with the updated comments array

    return { success: true, message: 'Comment deleted successfully' };
  } catch (error) {
    console.error('Error deleting comment:', error);
    return { success: false, message: error.message };
  }
};


exports.addComment = async (videoId, author, comment) => {
  try {
    const updatedVideo = await Video.findOneAndUpdate(
      { _id: videoId }, // Query: Find the video by its _id
      { $push: { comments: { author, comment } } }, // Update: Push a new comment object into the comments array
      { new: true } // Options: Return the updated document after update
    );
    
    return updatedVideo; // Return the updated video document with the new comment
  } catch (error) {
    throw error; // Handle any errors that occur during the update operation
  }
};

exports.likeVideo = async (videoId) => {
  const updatedVideo = await Video.findOneAndUpdate(
    { _id: videoId },
    { $inc: { likes: 1 } },
    { new: true }
  );
  return updatedVideo;
};

exports.dislikeVideo = async (videoId) => {
  const updatedVideo = await Video.findOneAndUpdate(
    { _id: videoId },
    { $inc: { dislikes: 1 } },
    { new: true }
  );
  return updatedVideo;
};

exports.undislikeVideo = async (videoId) => {
  const updatedVideo = await Video.findOneAndUpdate(
    { _id: videoId },
    { $inc: { dislikes: -1 } },
    { new: true }
  );
  return updatedVideo;
};

exports.unlikeVideo = async (videoId) => {
  const updatedVideo = await Video.findOneAndUpdate(
    { _id: videoId },
    { $inc: { likes: -1 } },
    { new: true }
  );
  return updatedVideo;
};

exports.addView = async (videoId) => {
  try {
    const updatedVideo = await Video.findOneAndUpdate(
      { _id: videoId },
      { $inc: { views: 1 } },
      { new: true }
    );
    return updatedVideo;
  } catch (error) {
    console.error('Error adding view:', error);
    throw new Error('Could not add view');
  }
};