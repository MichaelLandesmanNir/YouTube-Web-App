const express = require('express');
const app = express();
const cors = require('cors');
const bcrypt = require('bcrypt');
const mongoose = require('mongoose');
const userRoutes = require('./routes/userRoutes.js');
const videoRoutes = require('./routes/videoRoutes');
const loggerMiddleware = require('./middleware/logger');
const checkAndCreateVideos = require('./checkVideo.js');
require('dotenv').config();


app.use('/uploads', express.static('uploads'));
app.use(cors());
app.use(loggerMiddleware);
app.use(express.json()); 
app.use('/api', videoRoutes); 
app.use('/api', userRoutes);




mongoose.connect('mongodb://localhost:27017/youtube', { useNewUrlParser: true, useUnifiedTopology: true })
  .then(() => console.log('MongoDB connected \n \n '))
  .catch(err => console.error('\n \n Could not connect to MongoDB:', err));


checkAndCreateVideos();

app.listen(process.env.PORT, () => {
    console.log("server running on port: " + process.env.PORT)
});