import pymongo
import datetime

"""
TODO
INPUTS:
   username:string
   image: PIL Image type
   quote: string
   tb:    table in database that contains all stories
   valid: set to False if error occurs
OUTPUT:
   row_id in tb where the new entry is located

entry keys in tb:
   'row_id', 'user_id', 'image_dir','quote','datetime','valid'
"""

# save the image into a directory './images/'
# only save the directory of the image into database
def run_confirm(username,image,quote,tb,valid):
    # get row_id
    row_id = tb.find().count()
    # get image_dir
    image_dir = f'./images/{row_id}.jpg' 
    image.save(image_dir)
    # get date time
    now = datetime.datetime.now()

    # insert new entry to tb
    entry = {'row_id' : row_id,
             'user_id': username,
             'image_dir': image_dir,
             'quote': quote,
             'datetime': now,
             'valid': 1}
    try:
        tb.insert_one(entry)
        return row_id
    except Exception as e:
        valid = False
        print(str(e))
        return None # return nothing
    
