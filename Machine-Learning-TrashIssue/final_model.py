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

# Load base model ResNet50
base_model = tf.keras.applications.ResNet50(weights='imagenet', include_top=False, input_shape=(224, 224, 3))
base_model.trainable = False

x = base_model.output
x = GlobalAveragePooling2D()(x)
x = Dense(512, activation='relu', kernel_regularizer=regularizers.l2(0.01))(x)
x = Dropout(0.5)(x)
output_layer = Dense(2, activation='softmax')(x)

model = Model(inputs=base_model.input, outputs=output_layer)
model.compile(optimizer=Adam(), loss='categorical_crossentropy', metrics=['accuracy'])
model.summary()

history = model.fit(
    train_gen,
    epochs=5,
    validation_data=validation_gen
)

# Fine-tuning
base_model.trainable = True
fine_tune_at = 50
for layer in base_model.layers[:fine_tune_at]:
    layer.trainable = False

# Learning rate scheduler
def scheduler(epoch, lr):
    if epoch < 5:
        return lr
    else:
        return lr * 0.1

lr_scheduler = LearningRateScheduler(scheduler)

# Kompilasi ulang model setelah fine-tuning
model.compile(optimizer=Adam(learning_rate=0.0001), loss='categorical_crossentropy', metrics=['accuracy'])

# Callback
early_stopping = EarlyStopping(
    monitor='val_accuracy',
    patience=5,
    restore_best_weights=True
)

history = model.fit(
    train_gen,
    epochs=20,
    validation_data=validation_gen,
    callbacks=[early_stopping]
)
