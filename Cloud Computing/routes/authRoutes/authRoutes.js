const express = require('express');
const authController = require('./controllers');
const { postPredictHandler } = require('./handler');
const multer = require('multer');
const upload = multer({ storage: multer.memoryStorage() });

const router = express.Router();

//endopoint register
router.post('/register', authController.register);
//endpoint login
router.post('/login', authController.login);
//endpoint delete
router.delete('/delete/:uid', authController.deleteUser);
//endpoint update
router.put('/update/:uid', authController.updateUser);
//endpoint user
router.get('/user/:uid', authController.user);
//endpoint logout
router.post('/logout', authController.logout);
// endpoint /profile
router.get('/profile', authController.profile);
// Endpoint /predict
router.post('/predict', upload.single('image'), postPredictHandler);
//enpoint get /events
router.get('/events', authController.getEvents);
//endpoint post /events
router.post('/events', authController.createEvent);
module.exports = router;