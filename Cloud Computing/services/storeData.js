const { Firestore } = require('@google-cloud/firestore');

async function storeData(id, data) {
  const db = new Firestore();

  const predictCollection = db.collection('predictions');
  try {
    await predictCollection.doc(id).set(data);
    console.log('Data berhasil disimpan');
  } catch (error) {
    console.error('Gagal menyimpan data:', error);
  }
}

module.exports = storeData;