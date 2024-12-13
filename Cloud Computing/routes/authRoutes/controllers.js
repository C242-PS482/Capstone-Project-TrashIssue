const admin = require('firebase-admin');

//Firebase
const serviceAccount = require('./serviceAccountKey.json');
const router = require('./authRoutes');
admin.initializeApp({
  credential: admin.credential.cert(serviceAccount)
});

//Koneksi Firestore
const db = admin.firestore();
// register
const register = async (req, res) => {
  try {
    const { name, email, password } = req.body;

    const userRecord = await admin.auth().createUser({
      name: name,
      email: email,
      password: password
    });

    // Simpan data ke Firestore
    const userData = {
      name: name,
      email: email,
      userId: userRecord.uid
    };
    await db.collection('users').doc(userRecord.uid).set(userData);

    res.status(200).json({ error: false, message: 'Pengguna berhasil terdaftar', userId: userRecord.uid });
  } catch (error) {
    res.status(400).json({ error: true, message: 'Email sudah digunakan oleh akun lain.' });
  }
};

// login
const login = async (req, res) => {
  try {
    const { email, password } = req.body;

    // Lakukan autentikasi dengan Firebase Auth
    const userRecord = await admin.auth().getUserByEmail(email);
    const token = await admin.auth().createCustomToken(userRecord.uid);

    // Ambil data pengguna dari Firestore
    const doc = await db.collection('users').doc(userRecord.uid).get();
    const userData = doc.data();

    res.status(200).json({
      error: false,
      message: 'success',
      loginResult: {
        userId: userRecord.uid,
        name: userData.name,
        token: token
      }
    });
  } catch (error) {
    res.status(400).json({ error: error.message });
  }
};

// Fungsi untuk menghapus pengguna
const deleteUser = async (req, res) => {
  try {
    const { uid } = req.params;

    // Hapus pengguna dari Firebase Auth
    await admin.auth().deleteUser(uid);

    // Hapus data pengguna dari Firestore
    await db.collection('users').doc(uid).delete();

    res.status(200).json({ message: 'Pengguna berhasil dihapus' });
  } catch (error) {
    res.status(400).json({ error: error.message });
  }
};

// Fungsi untuk mengupdate pengguna
const updateUser = async (req, res) => {
  try {
    const { uid } = req.params;
    const { name, email } = req.body;

    // Proses pembaruan pengguna berdasarkan uid di Firebase Auth
    await admin.auth().updateUser(uid, { displayName: name, email });

    // Proses pembaruan pengguna berdasarkan uid di Firestore
    const userRef = db.collection('users').doc(uid);
    await userRef.update({ name, email });

    res.status(200).json({ message: 'Pengguna berhasil diperbarui' });
  } catch (error) {
    res.status(400).json({ error: error.message });
  }
};



// Fungsi untuk mendapatkan data pengguna berdasarkan userId
const user = async (req, res) => {
  try {
    const { uid } = req.params;

    // Proses mendapatkan data pengguna berdasarkan uid
    const userDoc = await db.collection('users').doc(uid).get();
    if (!userDoc.exists) {
      throw new Error('Data pengguna tidak ditemukan');
    }

    const userData = userDoc.data();
    res.status(200).json({ user: userData });
  } catch (error) {
    res.status(400).json({ error: error.message });
  }
};

//logout
const logout = async (req, res) => {
  try {
    res.status(200).json({ error: false, message: 'Pengguna berhasil logout' });
  } catch (error) {
    res.status(400).json({ error: error.message });
  }
};

// Fungsi untuk mendapatkan profile pengguna yang sedang login
const profile = async (req, res) => {
  try {
    const userId = req.user.id; // Asumsikan Anda mendapatkan userId dari token atau sesi
    const userDoc = await db.collection('users').doc(userId).get();

    if (!userDoc.exists) {
      return res.status(404).json({
        error: true,
        message: 'User not found'
      });
    }

    const userData = userDoc.data();
    res.status(200).json({
      error: false,
      message: 'Profile fetched successfully',
      profile: userData
    });
  } catch (error) {
    res.status(400).json({
      error: true,
      message: error.message
    });
  }
};

//event
// Get events
const getEvents = async (req, res) => {
  try {
    // Get events from Firestore
    const eventsSnapshot = await db.collection('events').get();
    
    const listEvents = [];
    eventsSnapshot.forEach(doc => {
      const event = doc.data();
      listEvents.push({
        id: event.id,
        name: event.name,
        summary: event.summary,
        description: event.description,
        imageLogo: event.imageLogo,
        mediaCover: event.mediaCover,
        category: event.category,
        ownerName: event.ownerName,
        cityName: event.cityName,
        quota: event.quota,
        registrants: event.registrants,
        beginTime: event.beginTime,
        endTime: event.endTime,
        link: event.link
      });
    });

    res.status(200).json({
      error: false,
      message: "Events fetched successfully",
      listEvents: listEvents
    });

  } catch (error) {
    res.status(400).json({
      error: true,
      message: error.message
    });
  }
};
// Create event
const createEvent = async (req, res) => {
  try {
    const {
      id,
      name,
      summary,
      description,
      imageLogo,
      mediaCover,
      category,
      ownerName,
      cityName,
      quota,
      registrants,
      beginTime,
      endTime,
      link
    } = req.body;

    const newEvent = {
      id,
      name,
      summary,
      description,
      imageLogo,
      mediaCover,
      category,
      ownerName,
      cityName,
      quota,
      registrants,
      beginTime,
      endTime,
      link
    };

    await db.collection('events').doc(id.toString()).set(newEvent);

    res.status(201).json({
      error: false,
      message: "Event created successfully",
      event: newEvent
    });
  } catch (error) {
    res.status(400).json({
      error: true,
      message: error.message
    });
  }
};



module.exports = {
  register,
  login,
  deleteUser,
  updateUser,
  user,
  logout,
  createEvent,
  getEvents,
  profile
}