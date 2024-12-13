const express = require('express');
const authRoutes = require('./routes/authRoutes/authRoutes');
const cors = require('cors');
const tf = require('@tensorflow/tfjs-node');
const multer = require('multer');
const app = express();

app.use(cors());
app.use(express.json());

app.get('/', (req, res) => {
  res.send('Succes!');
});

app.use(express.urlencoded({ extended: true }));

// Konfigurasi Multer
const upload = multer({ storage: multer.memoryStorage() });

// Load the model and store it in app.locals
tf.loadGraphModel('https://storage.googleapis.com/test-prediction-bucket2/model.json')
  .then(model => {
    app.locals.model = model;
    console.log('Model loaded successfully');

    // Start the server after the model is loaded
    const PORT = process.env.PORT || 8080;
    app.listen(PORT, () => {
      console.log(`Server berjalan di http://localhost:${PORT}`);
    });
  })
  .catch(error => {
    console.error('Error loading model:', error);
  });

app.use('/api', authRoutes);