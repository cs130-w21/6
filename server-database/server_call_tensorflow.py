import tensorflow as tf
import numpy as np
import os
from image_processing import resize_image
import cv2
import requests
import json
import time

# call inception server and run inference
# output latent vector
def call_inception(img):
    data  = json.dumps({'signature_name':'serving_default',
                        'instances' : [img.tolist()]})
    # make a REST request
    headers = {'content-type' : 'application/json'}
    json_response = requests.post('http://localhost:8501/v1/models/inception_model:predict', data=data)
    predictions = json.loads(json_response.text)['predictions']
    return predictions

def call_autoencoder_server(feature):
    # make batch dimension
    labels  = np.zeros((1,52),dtype=np.int32)
    data  = json.dumps({'signature_name':'serving_default',
                        'instances' : [{'data':feature[0],
                                        'label':labels.tolist()}
                                        ]})
    # make a REST request
    headers = {'content-type' : 'application/json'}
    json_response = requests.post('http://localhost:8502/v1/models/autoencoder_model:predict', data=data)
    #print(json.loads(json_response.text))
    output = json.loads(json_response.text)['predictions'][0]
    # we return the most likely word for each stage
    proba, preds = output['output_1'], output['output_2']
    #print(np.shape(proba),np.shape(preds))
    return preds

def call_tensorflow(tokenizer, imagedata, num_quotes=5):
    imagedata = resize_image(imagedata)
    feature = call_inception(imagedata)
    retpred = []
    for r in range(num_quotes):
        prediction = call_autoencoder_server(feature)
        prediction = np.reshape(prediction,(-1))
        pred = ''
        for index,j in enumerate(prediction[1:]):
            word = str([key for key,value in tokenizer.items() if value==j][0])
            if word != '<end>':
                if word == '<unk>':
                    continue
                pred += (word + ' ')
            else:
                break
        retpred.append(pred.capitalize()[0:-1])
    return retpred
