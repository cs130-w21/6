# This file only test the cases when client call tensorflow in server
import pytest
import random
from client import Client
import numpy as np

@pytest.fixture
def client():
    #pytest.client = Client(host='4.tcp.ngrok.io',port=19856)
    pytest.client = Client()
        
def test_tensorflow(client):
    image = pytest.client.encode_img('./server-database/images/0.jpg')
    data = {
        'op' : 'upload',
        'image' : image
    }
    msg = pytest.client.send_message(data)
    # make sure the returned dictionary contains only two fileds
    assert set(msg.keys()) == set(['op','quote'])
    # make sure the returned operation name is upload
    assert msg['op'] == 'upload'
    # make sure returns five quotes
    assert len(msg['quote']) == 5
    # make sure each quote is a string
    for s in msg['quote']:
        assert isinstance(s, str)

def test_register(client):
    # first add a new user
    data = {
        'op' : 'register',
        'uid' : 'unique_user_test1',
        'password' : 'helloworld'
    }
    # this must return success
    msg = pytest.client.send_message(data)
    # make sure the keys are correct
    assert set(msg.keys()) == set(['op','success','uid'])
    assert msg['op'] == 'register'
    assert msg['success']
    assert msg['uid'] == data['uid']

    # if we register again, this must return failure
    msg = pytest.client.send_message(data)
    # make sure the keys are correct
    assert set(msg.keys()) == set(['op','success','uid'])
    assert msg['op'] == 'register'
    assert not msg['success']
    assert msg['uid'] == data['uid']

    # we should be able to delete a user
    data2 = {
        'op' : 'delete_user',
        'uid' : 'unique_user_test1',
    }
    msg = pytest.client.send_message(data2)
    assert set(msg.keys()) == set(['op','uid'])
    assert msg['op'] == 'delete_user'
    assert msg['uid'] == data2['uid']

def test_login(client):
    # first register a user
    # assume the correctness of register
    # first add a new user
    data = {
        'op' : 'register',
        'uid' : 'unique_user_test2',
        'password' : 'helloworld'
    }
    # this must return success
    msg = pytest.client.send_message(data)

    # try login

    data1 = {
        'op' : 'login',
        'uid' : 'unique_user_test2',
        'password' : 'helloworld'
    }
    # this should return succes
    msg = pytest.client.send_message(data1)
    assert set(msg.keys()) == set(['op','success','uid'])
    assert msg['op'] == 'login'
    assert msg['success']
    assert msg['uid'] == data1['uid']

    # we next delete the user
    # we should be able to delete a user
    # assume the correctness of deletion
    data2 = {
        'op' : 'delete_user',
        'uid' : 'unique_user_test2',
    }
    msg = pytest.client.send_message(data2)

    # this should return failure
    msg = pytest.client.send_message(data1)
    assert set(msg.keys()) == set(['op','success','uid'])
    assert msg['op'] == 'login'
    assert not msg['success']
    assert msg['uid'] == data1['uid']

def test_confirm_delete(client):
    # first register a user
    # assume the correctness of register
    data = {
        'op' : 'register',
        'uid' : 'unique_user_test3',
        'password' : 'helloworld'
    }
    # this must return success
    msg = pytest.client.send_message(data)

    # add first random image quotes to the user database
    image1 = pytest.client.encode_img('./server-database/images/0.jpg')
    data1 = {
        'op' : 'confirm',
        'uid' : 'unique_user_test3',
        'image' : image1,
        'quote' : 'helloworld1'
    }
    # this should return success
    msg = pytest.client.send_message(data1)
    assert set(msg.keys()) == set(['op','row_id'])
    assert msg['op'] == 'confirm'
    assert isinstance(msg['row_id'],int)
    row_id1 = msg['row_id']

    # add second random image-quote to the user database
    image2 = pytest.client.encode_img('./server-database/images/1.jpg')
    data2 = {
        'op' : 'confirm',
        'uid' : 'unique_user_test3',
        'image' : image2,
        'quote' : 'helloworld2'
    }
    # this should return success
    msg = pytest.client.send_message(data2)
    assert set(msg.keys()) == set(['op','row_id'])
    assert msg['op'] == 'confirm'
    assert isinstance(msg['row_id'],int)
    row_id2 = msg['row_id']

    # If we delete row_id1, this should return success
    # Also return story2
    data3 = {
        'op' : 'delete',
        'row_id' : [row_id1],
        'uid' : 'unique_user_test3'
    }
    # this should return success
    msg = pytest.client.send_message(data3)
    assert set(msg.keys()) == set(['op','data'])
    assert msg['op'] == 'delete'
    # the only story should be the second story
    stories = msg['data']
    assert len(stories) == 1
    story   = stories[0]
    assert set(story.keys()) == set(['row_id','image','quote'])
    assert story['row_id'] == row_id2
    assert story['quote'] == 'helloworld2'

    # if we delete row_id2, this should return success
    data4 = {
        'op' : 'delete',
        'row_id' : [row_id2],
        'uid' : 'unique_user_test3'
    }
    # this should return success
    msg = pytest.client.send_message(data4)
    assert set(msg.keys()) == set(['op','data'])
    assert msg['op'] == 'delete'
    # the story should be empty
    stories = msg['data']
    assert len(stories) == 0
    
    # Delete user
    data2 = {
        'op' : 'delete_user',
        'uid' : 'unique_user_test3',
    }
    msg = pytest.client.send_message(data2)

def test_load(client):
    # first register a user
    # assume the correctness of register
    data = {
        'op' : 'register',
        'uid' : 'unique_user_test4',
        'password' : 'helloworld'
    }
    # this must return success
    msg = pytest.client.send_message(data)

    # assume the correctness of confirm
    # upload two random samples

    # add first random image quotes to the user database
    image1 = pytest.client.encode_img('./server-database/images/0.jpg')
    data1 = {
        'op' : 'confirm',
        'uid' : 'unique_user_test4',
        'image' : image1,
        'quote' : 'helloworld1'
    }
    # this should return success
    msg = pytest.client.send_message(data1)
    row_id1 = msg['row_id']

    # add second random image-quote to the user database
    image2 = pytest.client.encode_img('./server-database/images/1.jpg')
    data2 = {
        'op' : 'confirm',
        'uid' : 'unique_user_test4',
        'image' : image2,
        'quote' : 'helloworld2'
    }
    # this should return success
    msg = pytest.client.send_message(data2)
    row_id2 = msg['row_id']
    
    # load should return two stories
    data3 = {
        'op' : 'load',
        'uid' : 'unique_user_test4',
    }
    # this should be success and return two stories
    msg = pytest.client.send_message(data3)
    assert set(msg.keys()) == set(['op','data'])
    assert msg['op'] == 'load'
    stories = msg['data']
    story1 = stories[0]
    story2 = stories[1]
    assert set([(story1['row_id'],story1['quote']),(story2['row_id'],story2['quote'])]) == set([(row_id1,data1['quote']),(row_id2,data2['quote'])])

    # If we delete row_id1, this should return success
    # Also return story2
    data3 = {
        'op' : 'delete',
        'row_id' : [row_id1],
        'uid' : 'unique_user_test3'
    }
    # this should return success
    msg = pytest.client.send_message(data3)

    # if we delete row_id2, this should return success
    data4 = {
        'op' : 'delete',
        'row_id' : [row_id2],
        'uid' : 'unique_user_test3'
    }
    # this should return success
    msg = pytest.client.send_message(data4)
    
    # Delete user
    data = {
        'op' : 'delete_user',
        'uid' : 'unique_user_test4',
    }
    msg = pytest.client.send_message(data)
