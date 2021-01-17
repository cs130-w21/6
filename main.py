import tensorflow as tf
import numpy as np
import os
import collections
import dataset
from inception import *
from tokenizer import *
from autoencoder import *
from dataset import *
import cv2
import requests
import json

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
    autoencoder_model = autoencoder()
    # save_autoencoder(autoencoder_model)
    
if __name__ == "__main__":
    main()
