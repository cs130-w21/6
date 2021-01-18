import tensorflow as tf
from sklearn.model_selection import train_test_split
import numpy as np
import json
import random

batch_size = 64

            
class AutoEncoder(tf.keras.Model):
            
    def __init__(self):
        super(AutoEncoder,self).__init__()
        
        # parameters for CNN
        self.embedding_dim = 256 # hidden layer size
        self.dense_layer   = tf.keras.layers.Dense(self.embedding_dim, \
                                                   activation='relu')
        # parameters for RNN
        self.units = 512 # for attention
        self.vocab_size = 10000 + 1 # top 10000 words
        self.embedding_size = 256 # embedding size
        self.embedding = tf.keras.layers.Embedding(self.vocab_size,self.embedding_size)
        self.gru = tf.keras.layers.GRU(self.units,
                                   return_sequences=True,
                                   return_state=True,
                                   recurrent_initializer='glorot_uniform')
        self.fc1 = tf.keras.layers.Dense(self.units)
        self.fc2 = tf.keras.layers.Dense(self.vocab_size)
        self.max_length  = 52
        self.start_index = 3

        # for attention
        self.W1 = tf.keras.layers.Dense(self.units)
        self.W2 = tf.keras.layers.Dense(self.units)
        self.V = tf.keras.layers.Dense(1)

    # inputs have the format (None,64,2048)
    def call(self,inputs,training=False):
        # CNN encoding
        feature, labels = inputs['data'],inputs['label']
        feature  = self.dense_layer(feature)
        output = []
        proba  = []
        # RNN decoding
        # initialize hidden state
        self.hidden = tf.zeros((1, self.units))
        self.dec_input = tf.expand_dims([self.start_index],0)
        for i in range(self.max_length):
            # defining attention as a separate model
            context_vector, attention_weights = self.attention(feature, self.hidden)
            # x shape after passing through embedding == (batch_size, 1, embedding_dim)
            self.dec_input = self.embedding(self.dec_input)
            # x shape after concatenation == (batch_size, 1, embedding_dim + hidden_size)
            extended_context = tf.expand_dims(context_vector, 1)
            self.dec_input = tf.broadcast_to(self.dec_input,tf.shape(extended_context))
            self.dec_input = tf.concat([extended_context, self.dec_input], axis=-1)
            # passing the concatenated vector to the GRU
            out_val, self.hidden = self.gru(self.dec_input)
            # not max_length=1 here
            # shape == (batch_size, max_length, hidden_size)
            x = self.fc1(out_val)
            # x shape == (batch_size * max_length, hidden_size)
            x = tf.reshape(x, (-1, x.shape[2]))
            # output shape == (batch_size * max_length, vocab)
            predictions = self.fc2(x)

            # get the predicted_id
            predicted_id = tf.cast(tf.random.categorical(predictions,1),dtype=tf.int32)

            # update the dec_input
            if training:
                self.dec_input = tf.expand_dims(labels[:,i],1)
            else:
                self.dec_input = tf.stop_gradient(predicted_id)
            
            proba.append(predictions)
            output.append(self.dec_input)
            
        proba = tf.stack(proba)
        proba = tf.transpose(proba,[1,0,2])
        output = tf.stack(output)
        output = tf.transpose(output,[1,0,2])
        return proba,output
    
    @tf.function
    def attention(self, features, hidden):
        # features(CNN_encoder output) shape == (batch_size, 64, embedding_dim)
        # hidden shape == (batch_size, hidden_size)
        # hidden_with_time_axis shape == (batch_size, 1, hidden_size)
        hidden_with_time_axis = tf.expand_dims(hidden, 1)
        # attention_hidden_layer shape == (batch_size, 64, units)
        attention_hidden_layer = (tf.nn.tanh(self.W1(features) +
                                             self.W2(hidden_with_time_axis)))
        
        # score shape == (batch_size, 64, 1)
        # This gives you an unnormalized score for each image feature.
        score = self.V(attention_hidden_layer)
        
        # attention_weights shape == (batch_size, 64, 1)
        attention_weights = tf.nn.softmax(score, axis=1)
        
        # context_vector shape after sum == (batch_size, hidden_size)
        context_vector = attention_weights * features
        context_vector = tf.reduce_sum(context_vector, axis=1)
        
        return context_vector, attention_weights
