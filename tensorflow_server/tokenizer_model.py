import tensorflow as tf
from sklearn.model_selection import train_test_split
import numpy as np
import json
import cv2
import random

def create_tokenizer(train_captions):
    top_k = 10000 # save 10000 top words
    # create a pretrained tokenizer
    tokenizer = tf.keras.preprocessing.text.Tokenizer(num_words=top_k,
                                                      oov_token="<unk>",
                                                      filters='!"#$%&()*+.,-/:;=?@[\]^_`{|}~ ')
    tokenizer.fit_on_texts(train_captions)
    tokenizer.word_index['<pad>'] = 0
    tokenizer.index_word[0] = '<pad>'
    # create train seqs
    train_seqs = tokenizer.texts_to_sequences(train_captions)
    # calculate the max length and paddings
    max_length = max(len(t) for t in train_seqs)
    train_seqs = tf.keras.preprocessing.sequence.pad_sequences(train_seqs, padding='post')
    return tokenizer, train_seqs, max_length
