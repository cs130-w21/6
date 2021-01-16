import tensorflow as tf
import numpy as np
import os
import collections
import dataset
from image_processing import resize_image
from inception_model import ImageProcess
import cv2
import requests
import json

def main():
    """
    #Download and preprocess data
    image_path      = './train2014/'
    annotation_file = './annotations/captions_train2014.json'
    train_data, train_caption, test_data, test_caption = dataset.create_dataset(annotation_file,
                                                                                image_path,
                                                                                6000)
    """

    """
    #build a preprocessor server
    inception_model = ImageProcess()
    inception_model.create_model()
    inception_model.save_model()
    #nohup tensorflow_model_server --rest_api_port=8501 --model_name=inception_model --model_base_path="/home/ronny/MyStory/tensorflow/inception_server/" >server.log 2>&1
    """

    """
    #testing preprocessor server
    image = cv2.imread('resize_images/1.jpg')
    image = np.array([image]).astype(float)
    data  = json.dumps({'signature_name':'serving_default',
                        'instances' : image.tolist()})
    """
    """
    # make a REST request
    headers = {'content-type' : 'application/json'}
    json_response = requests.post('http://localhost:8501/v1/models/inception_model:predict', data=data)
    predictions = json.loads(json_response.text)['predictions']
    print(np.shape(predictions))
    """
    
if __name__ == "__main__":
    main()
