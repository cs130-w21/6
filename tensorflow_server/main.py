import tensorflow as tf
import numpy as np
import os
import collections
import dataset
from inception import *
from tokenizer import *
from autoencoder import *
from dataset import *
from image_processing import resize_image
import cv2
import requests
import json
import time

def main():

    """
    #preprocess data
    image_path      = './train2014/'
    annotation_file = './annotations/captions_train2014.json'
    train_captions, resized_imgs_link = dataset.create_dataset(annotation_file,
                                                               image_path,
                                                               20000)
    print(len(resized_imgs_link))
    print(len(train_captions))
    """
    
    # we build & run inception algorithm for all training data
    # latent vectors will be saved in ./inception_features/
    #build_inception()
    #inference_inception()
    
    # build a tokenizer
    # this will read all captions and save a hashtable as tokenizer.npy
    # tokenizer()
    
    # autoencoder server
    # autoencoder_model = autoencoder()
    # save_autoencoder(autoencoder_model)

    img_path = 'testing_imgs/4.jpg'
    num_quotes = 10
    
    st_time = time.time()
    # read an image
    img = cv2.imread(img_path)
    # resize an image
    img = resize_image(img)
    # get features after inception_V3
    feature = call_inception(img)
    # call server
    #i = 200
    #feature = np.load(f'./inception_features/{i}.npy')
    #captions = np.load('./captions.npy')
    #print(captions[i*5])
    for r in range(num_quotes):
        prediction = call_autoencoder_server(feature)
        prediction = np.reshape(prediction,(-1))
        tokenizer = np.load('tokenizer.npy',allow_pickle='TRUE').item()
        pred = ''
        for j in prediction:
            pred += (str([key for key,value in tokenizer.items() if value==j][0]) + ' ')
        print(pred)
    print(f'Total Time: {time.time() - st_time}s')
    
    
if __name__ == "__main__":
    main()
