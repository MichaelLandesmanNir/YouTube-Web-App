const User = require('../models/User');
const authService = require('./authService');
const bcrypt = require("bcryptjs");
const { generateToken } = require('../utils/authService');

const getUserById = async (id) => {
  try {
    return await User.findById(id);
  } catch (error) {
    throw new Error(`Error fetching user by ID: ${error.message}`);
  }
};

const updateUser = async (id, userData) => {
  try {
    return await User.findByIdAndUpdate(id, userData, { new: true });
  } catch (error) {
    throw new Error(`Error updating user: ${error.message}`);
  }
};

const deleteUser = async (id) => {
  try {
    return await User.findByIdAndDelete(id);
  } catch (error) {
    throw new Error(`Error deleting user: ${error.message}`);
  }
};

const getUserByToken = async (token) => {
  try {
    if (!token) {
      throw new Error('Token is missing');
    }
    const userId = authService.verifyAuthToken(token);
    const user = await User.findById(userId);
    if (!user) {
      throw new Error('User not found');
    }
    return {
      userId: user._id,
      name: user.name,
      imageUrl: user.imageUrl,
    };
  } catch (error) {
    throw new Error(`Error fetching user by token: ${error.message}`);
  }
};

const createUser = async (user) => {
  try {
    const hashedPassword = await bcrypt.hash(user.password, 10);

    const newUser = new User({
      name: user.name,
      password: hashedPassword,
      imageUrl: user.imageUrl,
    });

    await newUser.save();

    return { success: true, data: newUser };
  } catch (error) {
    return { success: false, error: error.message };
  }
};

const generateAuthToken = async (credentials) => {
  const { username, password } = credentials;

  try {
    // Find user by username
    const user = await User.findOne({ name: username });

    if (!user) {
      throw new Error('Invalid credentials');
    }

    // Compare hashed password
    const isMatch = await bcrypt.compare(password, user.password);

    if (!isMatch) {
      throw new Error('Invalid credentials');
    }

    const token = generateToken(user._id);

    return { user, token };
  } catch (error) {
    throw new Error(`Error generating auth token: ${error.message}`);
  }
};



module.exports = { getUserById, updateUser, deleteUser, createUser, generateAuthToken, getUserByToken };


