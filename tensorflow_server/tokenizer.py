from tokenizer_model import create_tokenizer
import tensorflow as tf
import numpy as np
import os
import collections
import cv2
import requests
import json

def tokenizer():
    train_captions = np.load('./captions.npy')
    tokenizer,train_seqs,max_length = create_tokenizer(train_captions)

    np.save('./seqs.npy',train_seqs)
        
    start_index = tokenizer.word_index['<start>']
    # we now convert the tf tokenizer into a hashtable
    tags = list(tokenizer.word_index.keys())
    tokens = list(tokenizer.word_index.values())
    lookup_table = {t1:t2 for t1,t2 in zip(tags,tokens)}
    print(len(lookup_table))
    np.save('./tokenizer.npy', lookup_table)
    print(max_length,start_index)
    return max_length,start_index
    """
    # Load python dictionary
    read_dictionary = np.load('tokenizer.npy',allow_pickle='TRUE').item()
    print(len(read_dictionary))
    """
