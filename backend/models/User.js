const mongoose = require('mongoose');
const { v4: uuidv4 } = require('uuid');

const userSchema = new mongoose.Schema({
  _id: {
    type: String,
    default: uuidv4, 
    required: true
  },
  name: { type: String, required: true },
  password: { type: String, required: true },
  imageUrl: { type: String, required: true }
});

const User = mongoose.model('User', userSchema);

module.exports = User;
