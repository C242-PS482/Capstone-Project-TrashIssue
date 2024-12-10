const tf = require('@tensorflow/tfjs-node');
const InputError = require('../exceptions/InputError');
 
async function predictClassification(model, image) {
    try {
        // Decode and preprocess image
        const tensor = tf.node
            .decodeJpeg(image)
            .resizeNearestNeighbor([224, 224])
            .toFloat()
            .div(tf.scalar(255)) // Normalisasi ke rentang [0, 1]
            .expandDims();

        // Get prediction
        const prediction = model.predict(tensor);
        const score = await prediction.data(); // Array of probabilities

        // Get predicted class index
        const predictedClassIndex = score.indexOf(Math.max(...score));

        // Define labels (adjust to match your training dataset)
        const labels = ['Organik','Non-Organik'];

        // Retrieve the predicted label
        const label = labels[predictedClassIndex];
        let suggestion;

        // Provide suggestions based on the predicted label
        if (label === 'Organik') {
            suggestion = "Ini sampah organik!";
        } else if (label === 'Non-Organik') {
            suggestion = "Ini sampah non-organik!";
        }

        return { label, suggestion };
    } catch (error) {
        console.error('Error during prediction:', error);
        throw new Error('Terjadi kesalahan dalam melakukan prediksi.');
    }
}

 
module.exports = predictClassification;