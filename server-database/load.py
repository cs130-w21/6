import pymongo

"""
TODO:
INPUTS:
   username: string
   tb: database that contains all stories
   valid: set to false if error occurs
OUTPUTS:
   stories: a list of [row_id, image_path, quote] of 
       the user. Note we only return entries that have
       'valid'=1 in the collection table.

entry keys in tb:
   'row_id', 'user_id', 'image_dir','quote','datetime','valid'
"""
def run_load(username,tb,valid):
    query = {'user_id' : username, 'valid' : 1}
    # from latest to earlest
    try:
        cols = tb.find(query).sort('datetime', pymongo.DESCENDING)
        # stories
        stories = []
        for c in cols:
            stories.append([c['row_id'],c['image_dir'],c['quote']])
        return stories
    except Exception as e:
        valid = False
        print(str(e))
        return None # return nothing

