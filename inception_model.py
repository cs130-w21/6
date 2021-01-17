import tensorflow as tf
from sklearn.model_selection import train_test_split
import numpy as np
import json
import cv2
import random

class ImageProcess:
    def __init__(self):
        self.inputs_shape = (299,299,3)
        
    def create_model(self, training=False):
        inputs = tf.keras.Input(shape=self.inputs_shape)
        x    = tf.keras.applications.inception_v3.preprocess_input(inputs)
        img_model = tf.keras.applications.InceptionV3(include_top=False, \
                                                       weights='imagenet')
        x    = img_model(x, training=False)
        output    = tf.reshape(x,(-1,64,2048))
        self.model = tf.keras.Model(inputs,output)
        self.model.compile()
        return self.model

    def predict(self, inputs):
        return self.model.predict(inputs)
    
    def save_model(self,dire='./inception_server/1/'):
        tf.keras.models.save_model(
            self.model,
            dire,
            overwrite=True,
            include_optimizer=True,
            save_format=None,
            signatures=None,
            options=None
        )




