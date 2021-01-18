from autoencoder_model import AutoEncoder
import tensorflow as tf
import numpy as np
import os
import collections
import cv2
import requests
import json
import random

def autoencoder():
    autoencoder = AutoEncoder()
    autoencoder.compile()
    train_autoencoder(autoencoder)
    return autoencoder

def loss_function(pred,batch_label,loss_object):
    mask  = tf.cast(tf.math.logical_not(batch_label==0),dtype=tf.float32)
    loss_ = loss_object(batch_label,pred)
    loss  = loss_ * mask
    return tf.reduce_mean(loss)

@tf.function
def train_step(autoencoder,batch_data,batch_label,optimizer,loss_object):
    with tf.GradientTape() as tape:
        proba, predictions = autoencoder({'data':batch_data,'label':batch_label},training=True)
        loss = loss_function(proba,batch_label,loss_object)
    trainable_variables = autoencoder.trainable_variables
    gradients           = tape.gradient(loss,trainable_variables)
    optimizer.apply_gradients(zip(gradients,trainable_variables))
    
    return loss

def train_autoencoder(autoencoder):
    nepoch = 100
    batch_size = 128
    num_batchs = int(100000 / batch_size)
    seqs      = np.load('./seqs.npy')
    img_links = np.load('./img_links.npy')
    num_examples = len(seqs)
    optimizer    = tf.keras.optimizers.Adam()
    loss_object  = tf.keras.losses.SparseCategoricalCrossentropy(from_logits=True,
                                                                 reduction='none')
    for epoch in range(nepoch):
        for batch in range(num_batchs):
            batch_index = np.random.choice(num_examples,batch_size)
            img_list    = np.array([np.load(f'./inception_features/{os.path.splitext(os.path.basename(img_links[i]))[0]}.npy') for i in batch_index])
            batch_data, batch_label = img_list, seqs[batch_index]
            # train once
            batch_loss = train_step(autoencoder,batch_data,batch_label,optimizer,loss_object)
            # record every 100 batch
            if batch % 100 == 0:
                print (f'Epoch {epoch} Batch {batch}: {batch_loss}')
        # save model once after 10 epochs
        if epoch % 10 == 0:
            save_autoencoder(autoencoder)

def save_autoencoder(autoencoder):
    dire='./autoencoder_server/1/'
    tf.keras.models.save_model(
            autoencoder,
            dire,
            overwrite=True,
            include_optimizer=True,
            save_format=None,
            signatures=None,
            options=None
        )

# nohup tensorflow_model_server --rest_api_port=8502 --model_name=autoencoder_model --model_base_path="/home/ronny/MyStory/tensorflow/autoencoder_server/" >autoencoder_server.log 2>&1
#saved_model_cli show --dir ./autoencoder_server/1/ --tag_set serve --signature_def serving_default
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
