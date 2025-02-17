const express = require('express');
const userController = require('../controller/userController.js');
const router = express.Router();
const multer = require('multer');


const storage = multer.diskStorage({
    destination: (req, file, cb) => {
      cb(null, 'uploads/');
    },
    filename: (req, file, cb) => {
      cb(null, Date.now() + '-' + file.originalname);
    },
  });
  
  const upload = multer({ storage: storage });
  
router.post('/users', upload.single('profileImage'), userController.createUser);


router.get('/user', userController.getUserByToken);

router.get('/users/:id', userController.getUser);
router.put('/users/:id', userController.updateUser);
router.patch('/users/:id', userController.updateUser);
router.delete('/users/:id', userController.deleteUser);
router.post('/users/create', userController.createUser)
router.get('/users/:id/videos', userController.getAllUserVideos);
router.post('/users/:id/videos', userController.createVideo);
router.post('/tokens', userController.generateAuthToken);
router.get('/users/:id/videos', userController.getAllUserVideos); 
router.post('/users/:id/videos', userController.createVideo); 
router.get('/users/:id/videos/:pid', userController.getVideoByIdUserVideo); 
router.put('/users/:id/videos/:pid', userController.updateVideo); 
router.delete('/users/:id/videos/:pid', userController.deleteVideo);


module.exports = router;
