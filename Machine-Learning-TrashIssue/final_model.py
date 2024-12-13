import tensorflow as tf
print(tf.__version__)

!pip uninstall tensorflow -y
!pip install tensorflow=="2.15.1"

import tensorflow as tf
print(tf.__version__)

import os
import tensorflow as tf
from tensorflow.keras.models import Model
from tensorflow.keras.layers import GlobalAveragePooling2D, Dense, Dropout
from tensorflow.keras.optimizers import Adam
from tensorflow.keras.callbacks import EarlyStopping, LearningRateScheduler
from tensorflow.keras import regularizers
from tensorflow.keras.preprocessing.image import ImageDataGenerator
import matplotlib.pyplot as plt

# Download dataset
import kagglehub
path = kagglehub.dataset_download("techsash/waste-classification-data")

# Menentukan lokasi dataset
dataset_path = os.path.join(path, 'DATASET')

# Dataset direktori
train_dir = os.path.join(dataset_path, 'TRAIN')
test_dir = os.path.join(dataset_path, 'TEST')

# Data augmentation
data_gen = ImageDataGenerator(
    rescale=1./255,
    rotation_range=40,
    width_shift_range=0.3,
    height_shift_range=0.3,
    shear_range=0.3,
    zoom_range=0.3,
    horizontal_flip=True,
    brightness_range=[0.8, 1.2],
    fill_mode='nearest'
)
val_gen = ImageDataGenerator(rescale=1./255)

train_gen = data_gen.flow_from_directory(train_dir, target_size=(224,224), batch_size=128)
validation_gen = val_gen.flow_from_directory(test_dir, target_size=(224,224), batch_size=128)
