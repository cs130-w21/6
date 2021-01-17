from inception_model import ImageProcess
import tensorflow as tf
import numpy as np
import os
import collections
import cv2
import requests
import json

"""
Run inception server
nohup tensorflow_model_server --rest_api_port=8501 --model_name=inception_model --model_base_path="/home/ronny/MyStory/tensorflow/inception_server/" >server.log 2>&1
"""
# save a model of inception model
def build_inception():
    inception_model = ImageProcess()
    inception_model.create_model()
    inception_model.save_model()

# run inference for all training data
def inference_inception():
    # decode and save all images
    model = tf.keras.models.load_model("./inception_server/1/")
    num_examples = 20000
    for i in range(0,num_examples,400):
        print(f"processing {i} to {i+400}")
        # collect images in the interval
        img_collection = []
        for j in range(i,i+400):
            image = cv2.imread(f'resize_images/{j}.jpg')
            img_collection.append(image.astype(float))
        img_collection = np.array(img_collection)
        # get output predictions
        predictions = model.predict(img_collection)
        # save the predictions
        for j in range(i,i+400):
            feature_path = f'./inception_features/{j}'
            np.save(feature_path,predictions[j-i])
    
# call inception server and run inference
# output latent vector
def call_inception():
    data  = json.dumps({'signature_name':'serving_default',
           'instances' : img_collection.tolist()})
    # make a REST request
    headers = {'content-type' : 'application/json'}
    json_response = requests.post('http://localhost:8501/v1/models/inception_model:predict', data=data)
    predictions = json.loads(json_response.text)['predictions']
    print(np.shape(predictions))
