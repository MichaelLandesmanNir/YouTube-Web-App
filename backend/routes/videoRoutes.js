const router = require('express').Router();
const videoController = require('../controller/videoController');
const multer = require('multer');
const auth = require('../middleware/auth');

const storage = multer.diskStorage({
    destination: (req, file, cb) => {
      cb(null, 'uploads/');
    },
    filename: (req, file, cb) => {
      cb(null, Date.now() + '-' + file.originalname);
    }
  });

const upload = multer({ storage: storage });


router.post('/videos', upload.fields([{ name: 'path' }, { name: 'image' }]), videoController.createVideo);
router.get('/videos', videoController.getVideos);
router.get('/allVideos', videoController.getAllVideos);
router.post('/videos/:videoId/comments', videoController.addComment);
router.post('/videos/:videoId/like', videoController.likeVideo);
router.post('/videos/:videoId/dislike', videoController.dislikeVideo);
router.delete('/videos/:videoId/undislike', videoController.undislikeVideo);
router.delete('/videos/:videoId/unlike', videoController.unlikeVideo);
router.get('/videos/:videoId', videoController.getVideo);


router.put('/videos/:videoId', videoController.editVideo);
router.delete('/videos/:videoId', videoController.deleteVideo);

router.delete('/videos/:videoId/comments', videoController.deleteComment);
router.put('/videos/:videoId/comments', videoController.editComment);
router.put('/videos/:videoId/addView', auth, videoController.addView);

module.exports = router;

