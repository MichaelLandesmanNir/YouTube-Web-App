const userService = require('../services/userService');
const videoService = require('../services/videoService');
const authService = require('../services/authService');

exports.getUser = async (req, res) => {
  try {
    const user = await userService.getUserById(req.params.id);
    if (!user) {
      return res.status(404).send('User not found');
    }
    res.json(user);
  } catch (err) {
    console.error('Error fetching user:', err);
    res.status(500).send('Internal Server Error');
  }
};

exports.getUserByToken = async (req, res) => {
  try {
    const token = req.header('Authorization').replace('Bearer ', '');
    const user = await userService.getUserByToken(token);
    if (!user) {
      return res.status(404).send('User not found');
    }
    res.json(user);
  } catch (err) {
    console.error('Error fetching user by token:', err);
    res.status(500).send('Internal Server Error');
  }
};


exports.createUser = async (req, res) => {
  try {
    // Add file path to the user data
    const userData = {
      ...req.body,
      imageUrl: req.file ? req.file.path : '',
    };

    const createdUser = await userService.createUser(userData);
    if (!createdUser.success) {
      return res.status(400).json({ error: createdUser.error });
    }
    res.status(201).json(createdUser.data);
  } catch (err) {
    console.error('Error creating user:', err);
    res.status(500).send('Internal Server Error');
  }
};
exports.updateUser = async (req, res) => {
  try {
    const updatedUser = await userService.updateUser(req.params.id, req.body);
    if (!updatedUser) {
      return res.status(404).send('User not found');
    }
    res.json(updatedUser);
  } catch (err) {
    console.error('Error updating user:', err);
    res.status(500).send('Internal Server Error');
  }
};

exports.generateToken = async (req, res) => {
  try {
    const { userId } = req.body; // Assuming userId is available in req.body
    const token = authService.generateAuthToken(userId);
    res.status(200).json({ token });
  } catch (error) {
    console.error('Error generating token:', error);
    res.status(500).json({ error: 'Internal Server Error' });
  }
};

exports.generateAuthToken = async (req, res) => {
  try {
    const { username, password } = req.body;
    const {user, token} = await userService.generateAuthToken({ username, password });
    res.json({user, token});
  } catch (error) {
    console.error('Error generating auth token:', error);
    res.status(401).json({ error: 'Invalid credentials' });
  }
};

exports.getAllUserVideos = async (req, res) => {
  try {
    const videos = await videoService.getVideosById(req.params.id);
    res.json(videos);
  } catch (error) {
    console.error('Error fetching user videos:', error);
    res.status(500).json({ message: 'Internal Server Error' });
  }
};

exports.createVideo = async (req, res) => {
  try {
    const createdVideo = await videoService.createVideo(req.body);
    res.status(201).json(createdVideo);
  } catch (error) {
    console.error('Error creating video:', error);
    res.status(500).json({ message: 'Internal Server Error' });
  }
};

exports.getVideoByIdUserVideo = async (req, res) => {
  try {
    const video = await videoService.getVideoByAuthorIdAndVideoId(req.params.id, req.params.pid);
    if (!video) {
      return res.status(404).json({ message: 'Video not found' });
    }
    res.json(video);
  } catch (error) {
    console.error('Error fetching video by ID:', error);
    res.status(500).json({ message: 'Internal Server Error' });
  }
};

exports.updateVideo = async (req, res) => {
  try {
    const updatedVideo = await videoService.updateVideoById(req.params.id, req.params.pid, req.body);
    if (!updatedVideo) {
      return res.status(404).json({ message: 'Video not found' });
    }
    res.json(updatedVideo);
  } catch (error) {
    console.error('Error updating video:', error);
    res.status(500).json({ message: 'Internal Server Error' });
  }
};

exports.deleteVideo = async (req, res) => {
  try {
    await videoService.deleteVideoById(req.params.id, req.params.pid);
    res.status(204).send(); // 204 No Content
  } catch (error) {
    console.error('Error deleting video:', error);
    res.status(500).json({ message: 'Internal Server Error' });
  }
};

exports.deleteUser = async (req, res) => {
  try {
    const deletedUser = await userService.deleteUser(req.params.id);
    if (!deletedUser) {
      return res.status(404).send('User not found');
    }
    res.send('User deleted');
  } catch (err) {
    console.error('Error deleting user:', err);
    res.status(500).send('Internal Server Error');
  }
};

exports.addVideo = async (req, res) => {
  res.status(501).send('Not Implemented');
};
