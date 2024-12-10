const predictClassification = require('../../services/inferenceService');
const crypto = require('crypto');
const storeData = require('../../services/storeData');
const getAllData = require('../../services/getAllData');

async function postPredictHandler(req, res) {
  const { file } = req;
  const { model } = req.app.locals;

  console.log('Gambar diterima:', file);
  console.log('Model:', model);

  if (!file) {
    return res.status(400).json({
      status: 'fail',
      message: 'Data gambar diperlukan'
    });
  }

  if (!model) {
    return res.status(500).json({
      status: 'fail',
      message: 'Model tidak dimuat'
    });
  }

  try {
    const { label, suggestion } = await predictClassification(model, file.buffer);
    console.log('Hasil prediksi:', { label, suggestion });

    const id = crypto.randomUUID();
    const createdAt = new Date().toISOString();

    const data = {
      id: id,
      result: label,
      suggestion: suggestion,
      createdAt: createdAt
    };

    await storeData(id, data);
    console.log('Data berhasil disimpan:', data);

    res.status(201).json({
      status: 'success',
      message: 'Model berhasil diprediksi',
      data: {
        id: id,
        result: label,
        suggestion: suggestion,
        createdAt: createdAt
      }
    });
  } catch (error) {
    console.error('Kesalahan prediksi:', error);
    res.status(400).json({
      status: 'fail',
      message: 'Terjadi kesalahan dalam melakukan prediksi'
    });
  }
}

module.exports = { postPredictHandler };