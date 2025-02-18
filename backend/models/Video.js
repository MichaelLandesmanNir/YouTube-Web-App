const mongoose = require('mongoose');

const commentSchema = new mongoose.Schema({
  author: { type: String, required: true },
  comment: { type: String, required: true }
}, { _id: false }); // To prevent Mongoose from creating separate ObjectIds for comments

const videoSchema = new mongoose.Schema({
  title: { type: String, required: false },
  channel: { type: String, required: false },
  path: { type: String, required: false },
  image: { type: String, required: false },
  views: { type: Number, default: 0 },
  likes: { type: Number, default: 0 },
  dislikes: { type: Number, default: 0 },
  comments: [commentSchema], // Array of comments, each with author and comment fields
  date: { type: Date, default: Date.now }
});

const Video = mongoose.model('Video', videoSchema);

module.exports = Video;
