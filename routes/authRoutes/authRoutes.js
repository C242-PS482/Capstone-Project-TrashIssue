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
// Endpoint /predict
router.post('/predict', upload.single('image'), postPredictHandler);

module.exports = router;